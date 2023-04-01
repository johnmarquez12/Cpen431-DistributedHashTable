package com.g10.CPEN431.A9;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class KeyTransferSenderThread extends Thread {

    public static class KeyTransfer {
        public Host host;
        public Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry;

        public KeyValueRequest.KVRequest request;
        public boolean forReplication;

        public KeyTransfer(Host host,
                           Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry) {
            this.host = host;
            this.entry = entry;
            this.forReplication = false;
        }

        public KeyTransfer(Host host,
                           KeyValueRequest.KVRequest request) {
            this.host = host;
            this.request = request;
            this.forReplication = false;
        }

        public KeyTransfer(Host host,
                           Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry,
                           boolean forReplication) {
            this.host = host;
            this.entry = entry;
            this.forReplication = forReplication;
        }
    }

    private final BlockingQueue<KeyTransfer> messages;
    private final InternalClient internalClient;

    public KeyTransferSenderThread(BlockingQueue<KeyTransfer> messages) {
        this.messages = messages;
        this.internalClient = new InternalClient();
    }

    public void run() {
        try {
            while (true) {

                KeyTransfer message = null;

                try {
                    message = messages.take();
                } catch (InterruptedException e) {
                    Logger.err(e.getMessage());
                }

                if (message == null) {
                    Logger.log("Ruh roh! Null message!");
                    continue;
                }

                if (!NodePool.getInstance().isAlive(message.host)) {
//                    Logger.log(
//                        "Message to %s with id %s failed cuz host deaaad",
//                        message.host, message.entry.getKey().toStringUtf8());
                    continue;
                }

                if (message.request == null) {
                    sendKeyMessage(message);
                } else {
                    sendRequest(message);
                }
            }
        } catch (Exception e) {
            Logger.err("BROKEN BROKEN, the keytransferSenderThread crashed.");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();

            Logger.err("[keytransferSenderThread] Err: "+stackTrace);
        }
    }

    private void sendKeyMessage(KeyTransfer message) {

        // 1. generate payload
        // 2. send payload
        // 3. if success, delete
        // 4. if failure, mark host as failed

        Logger.log("Sending %s key '%s' ", message.host, message.entry.getKey().toStringUtf8());

        KeyValueRequest.KVRequest requestPayload = generateKVRequest(message.entry, message.forReplication);
        byte[] arrayRequestPayload = requestPayload.toByteArray();
        KeyValueResponse.KVResponse response;
        NodePool.Heartbeat hb = NodePool.getInstance().getHeartbeatFromHost(message.host);

        try {
            response = internalClient.sendRequestWithRetries(arrayRequestPayload, message.host);
        } catch (IOException e) {
            Logger.err("Response while sending keys failed/timed out.");
            Logger.err("Failed to send key " + message.entry.getKey().toStringUtf8());
//            NodePool.getInstance().removeNode(hb);
            messages.add(message);
            return;
        }

        if(response == null || response.getErrCode() != Codes.Errs.SUCCESS) {
            Logger.err("Response while sending keys failed.");
            Logger.err("Failed to send key " + message.entry.getKey().toStringUtf8());
//            NodePool.getInstance().removeNode(hb);
            messages.add(message);
            return;
        }

        Logger.log("Sent key.");
    }

    private void sendRequest(KeyTransfer message) {
        Logger.log("Sending '%s' '%s' replica to %s... ", Codes.Commands.cmd_name(message.request.getCommand()), message.request.getKey().toStringUtf8(), message.host);

        KeyValueResponse.KVResponse response;

        try {
            response = internalClient.sendRequestWithRetries(message.request.toByteArray(), message.host);
        } catch (IOException e) {
            Logger.err("Response while sending request replica failed/timed out.");
            Logger.err("Failed (and thus lost) to request send key " + message.request.getKey().toStringUtf8());
            messages.add(message);
            return;
        }

        if(response == null || response.getErrCode() != Codes.Errs.SUCCESS) {
            Logger.err("Response while sending request replica failed.");
            Logger.err("Failed (and thus lost) to request send key " + message.request.getKey().toStringUtf8());
            messages.add(message);
            return;
        }

        Logger.log("Sent replica.");
    }

    private static KeyValueRequest.KVRequest generateKVRequest(Map.Entry<ByteString, KeyValueStore.ValueWrapper> keyToSend, boolean forReplication) {
        KeyValueRequest.KVRequest.Builder request = KeyValueRequest.KVRequest.newBuilder()
                .setCommand(Codes.Commands.PUT)
                .setKey(keyToSend.getKey())
                .setValue(keyToSend.getValue().value)
                .setVersion(keyToSend.getValue().version);

        if (forReplication) {
            request.setIr(
                    InternalRequest.InternalRequestWrapper.newBuilder()
                        .setReplicate(true)
            );
        }

        return request.build();
    }
}
