package com.g10.CPEN431.A11;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import com.google.protobuf.ByteString;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

// TODO: Should this be a thread??
// Since we may send many keys over, our rejoin function could take a while to run
public class KeyTransferHandler {
    private BlockingQueue<KeyTransferSenderThread.KeyTransfer> keysToSend;

    public KeyTransferHandler(BlockingQueue<KeyTransferSenderThread.KeyTransfer> keysToSend) {
        this.keysToSend = keysToSend;
    }

    public void sendKeys(Host recipient, int idToMatch, boolean isReplica) {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore kvStore = KeyValueStore.getInstance();
        int keysSent = 0;

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : kvStore.keySet()) {
            if (nodePool.getIdFromKey(entry.getKey().hashCode()) != idToMatch) continue;
            keysSent++;
            keysToSend.add(new KeyTransferSenderThread.KeyTransfer(recipient, entry, isReplica));
        }

        Logger.log("Sent " + keysSent + " keys to: " + recipient);
    }

    public void sendRequest(KeyValueRequest.KVRequest request, Host host) {
        keysToSend.add(new KeyTransferSenderThread.KeyTransfer(host, request));
    }
}
