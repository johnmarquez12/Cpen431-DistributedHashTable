package com.s36906949.CPEN431.A4;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;
import java.util.concurrent.Callable;


public class Application implements Callable<ByteString> {

    private final byte[] payload;
    private KeyValueRequest.KVRequest request;
    private KeyValueResponse.KVResponse response;

    public Application(byte[] payload) {
        this.payload = payload;
        System.out.print("Received payload: ");
        System.out.println(Arrays.toString(payload));
    }

    @Override
    public ByteString call() throws InvalidProtocolBufferException {
        System.out.println("Miss");

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

        System.out.printf("Requested cmd number is %d%n", request.getCommand());

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



        System.out.printf("(Err %d) Calculated response: %s%n",
            response.getErrCode(), Arrays.toString(response.toByteArray()));

        return response.toByteString();
    }



    void cmdPut() {
        // TODO: do some error checking
        KeyValueStore.getInstance().put(
            request.getKey(),
            request.getValue(),
            request.getVersion()
        );

        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS)
            .build();
    }

    void cmdGet() {
        KeyValueStore.ValueWrapper value =
            KeyValueStore.getInstance().get(request.getKey());

        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS)
            .setValue(value.value)
            .setVersion(value.version)
            .build();
    }

    void cmdRemove() {
        KeyValueStore.getInstance().remove(request.getKey());

        response = KeyValueResponse.KVResponse.newBuilder().build();
    }

    void cmdShutdown() {}

    void cmdWipeout() {
        KeyValueStore.getInstance().wipeout();

        response = KeyValueResponse.KVResponse.newBuilder()
            .build();

    }

    void cmdIsAlive() {
        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS)
            .build();
    }

    void cmdGetPID() {
        long pid = ProcessHandle.current().pid();

        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS)
            .setPid((int) pid)
            .build();
    }

    void cmdGetMembershipCount() {
        int memCount = KeyValueStore.getInstance().getMembershipSize();

        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.SUCCESS)
            .setMembershipCount(memCount)
            .build();
    }

    void cmdError() {
        response = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(Codes.Errs.CMD_UNKNOWN)
            .build();
    }
}
