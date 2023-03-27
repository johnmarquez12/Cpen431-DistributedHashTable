package com.g10.CPEN431.A9;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class KeyTransferSenderThread extends Thread {

    public record KeyTransfer(NodePool.Heartbeat recipient, Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry, boolean replicate) {}

    private final BlockingQueue<KeyTransfer> messages;

    public KeyTransferSenderThread(BlockingQueue<KeyTransfer> messages) {
        this.messages = messages;
    }

    public void run() {
        while(true) {

            KeyTransfer message = null;

            try {
                message = messages.take();
            } catch (InterruptedException e) {
                Logger.err(e.getMessage());
            }

            if(message == null) continue;

            // 1. generate payload
            // 2. send payload
            // 3. if success, delete
            // 4. if failure, mark host as failed

            Logger.log("Sending %s (id %d) key with hash <%d> ", message.recipient.host, message.recipient.id, message.entry.getKey().hashCode());

            byte[] requestPayload = generateKVRequest(message.entry, message.replicate);

            if (message.replicate) {
                replicate(requestPayload, message);
                continue;
            }

            partition(requestPayload, message);
        }
    }

    private void replicate(byte[] requestPayload, KeyTransfer message) {
        // TODO: We should probably retry if a kv we need to replicate fails,
        //  currently we have it so we readd the message into the queue again to resend

        KeyValueResponse.KVResponse response;

        try {
            response = InternalClient.sendRequestWithRetries(requestPayload, message.recipient.host);
        } catch (IOException e) {
            Logger.err("Response while sending keys failed/timed out.");

            messages.add(message);
            return;
        }

        if (response == null || response.getErrCode() != Codes.Errs.SUCCESS) {
            Logger.err("Response while sending keys failed.");
            messages.add(message);
        }
    }

    private void partition(byte[] requestPayload, KeyTransfer message) {
        KeyValueResponse.KVResponse response;

        try {
            response = InternalClient.sendRequestWithRetries(requestPayload, message.recipient.host);
        } catch (IOException e) {
            Logger.err("Response while sending keys failed/timed out.");
            NodePool.getInstance().removeNode(message.recipient);
            return;
        }

        if (response == null || response.getErrCode() != Codes.Errs.SUCCESS) {
            Logger.err("Response while sending keys failed.");
            NodePool.getInstance().removeNode(message.recipient);
            return;
        }

        try {
            KeyValueStore.getInstance().remove(message.entry.getKey());
        } catch (KeyValueStore.NoKeyError e) {
            Logger.err("Missing a key on transfer: "+e);
            return;
        }

        Logger.log("Sent key.");
    }

    private static byte[] generateKVRequest(Map.Entry<ByteString, KeyValueStore.ValueWrapper> keyToSend, boolean replicate) {
        int code = replicate ? Codes.Commands.REPLICATE : Codes.Commands.PUT;

        return KeyValueRequest.KVRequest.newBuilder()
            .setCommand(code)
            .setKey(keyToSend.getKey())
            .setValue(keyToSend.getValue().value)
            .setVersion(keyToSend.getValue().version)
            .build().toByteArray();
    }
}
