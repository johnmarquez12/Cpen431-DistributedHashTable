package com.g10.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: Should this be a thread??
// Since we may send many keys over, our rejoin function could take a while to run
public class KeyTransferHandler {

    public static void sendKeys(int id) throws IOException {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore keyValueStore = KeyValueStore.getInstance();

        List<Map.Entry<ByteString, KeyValueStore.ValueWrapper>> keysToSend = new ArrayList<>();

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : keyValueStore.keySet()) {
            if (nodePool.getIdFromId(entry.getKey().hashCode()) == id) {
                keysToSend.add(entry);

                try {
                    // TODO: what do with this error
                    keyValueStore.remove(entry.getKey());
                } catch (KeyValueStore.NoKeyError noKeyError) {
                    Logger.log(noKeyError.getMessage());
                }
            }
        }

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> key : keysToSend) {
            byte[] requestPayload = generateKVRequest(key);
            // TODO: Verify message received from other node and maybe retry?
            InternalClient.sendRequestWithRetries(requestPayload, nodePool.getHostFromId(id));
        }
    }

    private static byte[] generateKVRequest(Map.Entry<ByteString, KeyValueStore.ValueWrapper> keyToSend) {
        return KeyValueRequest.KVRequest.newBuilder()
                .setCommand(Codes.Commands.PUT)
                .setKey(keyToSend.getKey())
                .setValue(keyToSend.getValue().value)
                .setVersion(keyToSend.getValue().version)
                .build().toByteArray();
    }

}
