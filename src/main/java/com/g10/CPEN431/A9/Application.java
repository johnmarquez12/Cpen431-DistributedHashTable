package com.g10.CPEN431.A9;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.UnknownHostException;
import java.util.concurrent.Callable;


public class Application implements Callable<Application.ApplicationResponse> {

    private final byte[] payload;
    private KeyValueRequest.KVRequest request;
    private KeyValueResponse.KVResponse.Builder response;

    private Host client;
    private ApplicationResponse appResponse;

    public record ApplicationResponse(boolean shouldCache, ByteString messageData, Host replyTo) {}

    public Application(byte[] payload, Host client) {
        this.payload = payload;
        this.client = client;
    }

    @Override
    public ApplicationResponse call()
        // TODO: maybe we don't want the UnknownHostException here. Not really
        //       sure when it would happen...
        throws InvalidProtocolBufferException, UnknownHostException {
        // Start building a response object
        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS)
            .setPid((int) App.pid);

        // TODO: do something if this fails
        request = KeyValueRequest.KVRequest.parseFrom(payload);

        if(request.hasIr() && request.getIr().hasClient()) {
            client = Host.fromProtobuf(request.getIr().getClient());
        }

        /*
        0x01 - Put: This is a put operation.
        0x02 - Get: This is a get operation.
        0x03 - Remove: This is a remove operation.
        0x04 - Shutdown: shuts-down the node (used for testing and management).
        The expected behaviour is that your implementation immediately calls System.exit().
        0x05 - Wipeout: deletes all keys stored in the node (used for testing)
        0x06 - IsAlive: does nothing but replies with success if the node is alive.
        0x07 - GetPID: the node is expected to reply with the processID of the Java process
        0x08 - GetMembershipCount: the node is expected to reply with the count of the currently active members based on your membership protocol.  (this will be used later, for now you are expected to return 1)
        [Note: We may add some more management operations]
            anything > 0x20. Your own commands if you want.  They may be useful for debugging.  For example
            0x22 - GetMembershipList:  We recommend that when you implement a key/value store that uses multiple nodes, you add a command that queries a participating node and receives its view of the membership.

         */

        switch (request.getCommand()) {
            case Codes.Commands.PUT -> cmdPut();
            case Codes.Commands.GET -> cmdGet();
            case Codes.Commands.REMOVE -> cmdRemove();
            case Codes.Commands.SHUTDOWN -> cmdShutdown();
            case Codes.Commands.WIPEOUT -> cmdWipeout();
            case Codes.Commands.IS_ALIVE -> cmdIsAlive();
            case Codes.Commands.GET_PID -> cmdGetPID();
            case Codes.Commands.GET_MEMBERSHIP_COUNT -> cmdGetMembershipCount();
            case Codes.Commands.INTERNAL_REQUEST -> cmdInternalRequest();

            default -> cmdError();
        }

        if (appResponse == null) {
            appResponse = new ApplicationResponse(true,
                response.build().toByteString(), client);
        }

        return appResponse;
    }

    private boolean outOfMemory() {
        return App.freeMemory() <= KeyValueStore.MAX_KEY_LENGTH + request.getValue().size();
    }

    boolean divertRequest() {
        Host serviceHost = NodePool.getInstance().getHostFromId(request.getKey().hashCode());
        if (serviceHost.equals(NodePool.getInstance().getMyHost())) {
            return false;
        }

        ByteString messageData = request.toBuilder().setIr(
            InternalRequest.InternalRequestWrapper.newBuilder().setClient(
                client.toProtobuf()
            ).build()
        ).build().toByteString();

        appResponse = new ApplicationResponse(false, messageData, serviceHost);

        return true;
    }

    void cmdPut() {
        if(keyInvalid()) return;
        if(valueInvalid()) return;

        if(!isReplication()) {

            if (divertRequest()) {
                return;
            }

            replicate(); // TODO: should we continue if this fails?
        }

        if(outOfMemory()) System.gc();
        if (outOfMemory()) {
            response.setErrCode(Codes.Errs.OUT_OF_SPACE);
            System.err.println("OUT OF SPACE!!");
            return;
        }

        // TODO: do some error checking

        Logger.log("Putting '%s' (id %d) locally%s!%n", request.getKey().toStringUtf8(), NodePool.getInstance().hashToId(request.getKey().hashCode()),
            (isReplication() ? " (replicated)" : ""));

        KeyValueStore.getInstance().put(
            request.getKey(),
            request.getValue(),
            request.getVersion()
        );
    }

    void cmdGet() {
        if(keyInvalid()) return;

        if(divertRequest()) return;

        try {
            KeyValueStore.ValueWrapper value = KeyValueStore.getInstance()
                .get(request.getKey());

            response.setValue(value.value).setVersion(value.version);
        } catch (KeyValueStore.NoKeyError e) {
            Logger.log("Missing key '%s'%n", request.getKey().toStringUtf8());
            response.setErrCode(Codes.Errs.KEY_DOES_NOT_EXIST);
        }
    }

    void cmdRemove() {
        if(keyInvalid()) return;

        if(!isReplication()) {

            if (divertRequest()) return;

            replicate(); // TODO: should we continue if this fails?
        }

        try {
            Logger.log("Deleting Key "+request.getKey().toStringUtf8()+ " Locally");
            KeyValueStore.getInstance().remove(request.getKey());
        } catch (KeyValueStore.NoKeyError e) {
            response.setErrCode(Codes.Errs.KEY_DOES_NOT_EXIST);
        }
    }

    void cmdShutdown() {
        System.exit(0);
    }

    void cmdWipeout() {
        KeyValueStore.getInstance().wipeout();

        RequestReplyCache.getInstance().wipeout();

        System.gc();
    }

    void cmdIsAlive() {
        // No Op
    }

    void cmdGetPID() {
        response.setPid((int) App.pid);
    }

    void cmdGetMembershipCount() {
        int memCount = NodePool.getInstance().aliveNodeCount();

        response.setMembershipCount(memCount);
    }

    void cmdError() {
        response.setErrCode(Codes.Errs.CMD_UNKNOWN);
    }

    void cmdInternalRequest() {
        if(request.hasIr() && request.getIr().getHeartbeatsCount() > 0) {
            ReceiveHeartbeatHandler.updateHeartbeats(request.getIr().getHeartbeatsList());
        }

        appResponse = new ApplicationResponse(false, null, null);
    }

    private boolean keyInvalid() {
        if (request.getKey().size() > KeyValueStore.MAX_KEY_LENGTH) {
            response.setErrCode(Codes.Errs.KEY_INVALID);
            return true;
        }
        return false;
    }

    private boolean valueInvalid() {
        if (request.getValue().size() > KeyValueStore.MAX_VALUE_LENGTH) {
            response.setErrCode(Codes.Errs.VALUE_INVALID);
            return true;
        }
        return false;
    }

    private boolean replicate() {
        // 1. get a list of nodes we should replicate to
        // 2. for each one, send an IR (ensure success send) to replicate
        // TODO: What to do if that fails?
        NodePool.getInstance().sendReplicas(request);
        return true;
    }

    private boolean isReplication() {
        if (!request.hasIr() || !request.getIr().getReplicate()) return false;

        return true;
    }
}
