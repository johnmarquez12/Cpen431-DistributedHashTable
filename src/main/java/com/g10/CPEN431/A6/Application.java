package com.g10.CPEN431.A6;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.concurrent.Callable;


public class Application implements Callable<ByteString> {

    private final byte[] payload;
    private KeyValueRequest.KVRequest request;
    private KeyValueResponse.KVResponse.Builder response;

    public Application(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public ByteString call() throws InvalidProtocolBufferException {
        // Start building a response object
        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS);

        // TODO: do something if this fails
        request = KeyValueRequest.KVRequest.parseFrom(payload);

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

            default -> cmdError();
        }

        return response.build().toByteString();
    }

    private boolean outOfMemory() {
        return App.freeMemory() <= KeyValueStore.MAX_KEY_LENGTH + request.getValue().size();
    }

    boolean divertRequest() {
        Host serviceHost = NodePool.getInstance().getHostFromId(request.getKey().hashCode());
        if (serviceHost.equals(NodePool.getInstance().getMyHost())) {
            return false;
        }

        /*
        TODO: actually divert the request!

            send to B using same messageId as original
              At B:
                - B caches, B responds

              At A:
                - Send using Internal Request (no retries required)
                - DONT CACHE, DONT RESPOND, DO NOT PASS GO, DO NOT COLLECT $200
         */


        return true;
    }

    void cmdPut() {
        if(keyInvalid()) return;
        if(valueInvalid()) return;

        if(divertRequest()) return;

        if(outOfMemory()) System.gc();
        if (outOfMemory()) {
            response.setErrCode(Codes.Errs.OUT_OF_SPACE);
            System.err.println("OUT OF SPACE!!");
            return;
        }

        // TODO: do some error checking

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
            response.setErrCode(Codes.Errs.KEY_DOES_NOT_EXIST);
        }
    }

    void cmdRemove() {
        if(keyInvalid()) return;

        if(divertRequest()) return;

        try {
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

        // TODO: make sure this is correct behavior
        RequestReplyCache.getInstance().wipeout();

        System.gc();
    }

    void cmdIsAlive() {
        // No Op
    }

    void cmdGetPID() {
        long pid = ProcessHandle.current().pid();

        response.setPid((int) pid);
    }

    void cmdGetMembershipCount() {
        int memCount = KeyValueStore.getInstance().getMembershipSize();

        response.setMembershipCount(memCount);
    }

    void cmdError() {
        response.setErrCode(Codes.Errs.CMD_UNKNOWN);
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
}
