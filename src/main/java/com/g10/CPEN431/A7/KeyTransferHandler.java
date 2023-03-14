package com.g10.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

// TODO: Should this be a thread??
// Since we may send many keys over, our rejoin function could take a while to run
public class KeyTransferHandler {

    public static void sendKeys(BlockingQueue<KeyTransferSenderThread.KeyTransfer> keysToSend, NodePool.Heartbeat recipient) {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore kvStore = KeyValueStore.getInstance();

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : kvStore.keySet()) {
            if (nodePool.getIdFromKey(entry.getKey().hashCode()) != recipient.id) continue;

            keysToSend.add(new KeyTransferSenderThread.KeyTransfer(recipient, entry));
        }
    }
}
