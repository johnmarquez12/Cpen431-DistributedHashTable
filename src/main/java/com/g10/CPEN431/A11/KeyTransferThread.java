package com.g10.CPEN431.A11;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KeyTransferThread extends Thread {

    private final List<KeyTransferSenderThread.KeyTransfer> keyTransferList;
    private final InternalClient internalClient;

    public KeyTransferThread(List<KeyTransferSenderThread.KeyTransfer> keyTransferList) {
        super("KeyTransferThread");
        this.keyTransferList = keyTransferList;
        internalClient = new InternalClient();
    }

    @Override
    public void run() {
        for (KeyTransferSenderThread.KeyTransfer keyTransfer : keyTransferList) {

            if (!NodePool.getInstance().isAlive(keyTransfer.host)) {
                Logger.log(
                        "Message to %s with failed cuz host deaaad", keyTransfer.host);
                continue;
            }

            if (keyTransfer.request == null) {
                sendKeyMessage(keyTransfer);
            } else {
                sendRequest(keyTransfer);
            }
        }
    }

    private void sendKeyMessage(KeyTransferSenderThread.KeyTransfer message) {

        // 1. generate payload
        // 2. send payload
        // 3. if success, delete
        // 4. if failure, mark host as failed

        if (message.isRejoin)
            Logger.log("Sending Rejoin %s key '%s' ", message.host, message.entry.getKey().toString());

        byte[] requestPayload = generateKVRequest(message.entry, message.isReplica);
        KeyValueResponse.KVResponse response;
//        NodePool.Heartbeat hb = NodePool.getInstance().getHeartbeatFromHost(message.host);

        try {
            response = internalClient.sendRequestWithRetries(requestPayload, message.host);
        } catch (IOException e) {
            Logger.err("Response while sending keys failed/timed out to host: " + message.host);
            Logger.err("Failed to send key " + message.entry.getKey().toString());
//            NodePool.getInstance().removeNode(hb);
//            messages.add(message);
            return;
        }

        if(response == null || response.getErrCode() != Codes.Errs.SUCCESS) {
            Logger.err("Response while sending keys failed/not success to host: " + message.host);
            Logger.err("Failed to send key " + message.entry.getKey().toString());
//            NodePool.getInstance().removeNode(hb);
//            messages.add(message);
            return;
        }

        if (message.isRejoin)
            Logger.logVerbose("Sent key.");
    }

    private void sendRequest(KeyTransferSenderThread.KeyTransfer message) {

        if (message.isRejoin)
            Logger.log("Sending '%s' '%s' replica to %s... ", Codes.Commands.cmd_name(message.request.getCommand()), message.request.getKey().toStringUtf8(), message.host);

        KeyValueResponse.KVResponse response;

        try {
            response = internalClient.sendRequestWithRetries(message.request.toByteArray(), message.host);
        } catch (IOException e) {
            Logger.err("Response while sending keys failed/timed out to host: " + message.host);
            Logger.err("Failed (and thus lost) to request send key " + message.request.getKey().toString());
//            messages.add(message);
            return;
        }

        if(response == null || response.getErrCode() != Codes.Errs.SUCCESS) {
            Logger.err("Response while sending keys failed/not success to host: " + message.host);
            Logger.err("Failed (and thus lost) to request send key " + message.request.getKey().toString());
//            messages.add(message);
            return;
        }

        Logger.logVerbose("Sent replica.");
    }

    private static byte[] generateKVRequest(Map.Entry<ByteString, KeyValueStore.ValueWrapper> keyToSend,
                                            boolean isReplica) {
        return KeyValueRequest.KVRequest.newBuilder()
                .setCommand(Codes.Commands.PUT)
                .setKey(keyToSend.getKey())
                .setValue(keyToSend.getValue().value)
                .setVersion(keyToSend.getValue().version)
                .setIr(InternalRequest.InternalRequestWrapper.newBuilder()
                    .setReplicate(isReplica)
                    .setCounter(keyToSend.getValue().counter).build())
                .build().toByteArray();
    }
}
