package com.g10.CPEN431.A11;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

// TODO: Should this be a thread??
// Since we may send many keys over, our rejoin function could take a while to run
public class KeyTransferHandler {

    public static final int BATCH_SIZE = 1000;

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

            try {
                keysToSend.put(new KeyTransferSenderThread.KeyTransfer(recipient, entry, isReplica));
            } catch (InterruptedException e) {
                Logger.err("Error putting key into queue: " + Arrays.toString(e.getStackTrace()));
            }
        }

        Logger.log("Sending " + keysSent + " keys to: " + recipient);
    }

    public void sendKeysRejoin(Host recipient, int idToMatch, boolean isReplica) {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore kvStore = KeyValueStore.getInstance();
        int keysSent = 0;

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : kvStore.keySet()) {
            if (nodePool.getIdFromKey(entry.getKey().hashCode()) != idToMatch) continue;
            keysSent++;
            
            try {
                keysToSend.put(new KeyTransferSenderThread.KeyTransfer(recipient, entry, isReplica, true));
            } catch (InterruptedException e) {
                Logger.err("Error putting key into queue: " + Arrays.toString(e.getStackTrace()));
            }
        }

        Logger.log("Sending " + keysSent + " keys to: " + recipient);
        Logger.log("Size of queue after putting keys for rejoin: " + keysToSend.size());
    }

    public void sendKeys2(Host recipient, int idToMatch, boolean isReplica) {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore kvStore = KeyValueStore.getInstance();

        List<KeyTransferSenderThread.KeyTransfer> keyTransferList = new ArrayList<>();

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : kvStore.keySet()) {
            if (nodePool.getIdFromKey(entry.getKey().hashCode()) != idToMatch) continue;

            keyTransferList.add(new KeyTransferSenderThread.KeyTransfer(recipient, entry, isReplica));
        }

        if (keyTransferList.size() == 0) {
            Logger.log("No keys to send to: " + recipient);
            return;
        }

        Logger.log("Sending " + keyTransferList.size() + " total keys to: " + recipient);
        sendKeyTransferBatches(keyTransferList, recipient);
    }

    public void sendKeysRejoin2(Host recipient, int idToMatch, boolean isReplica) {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore kvStore = KeyValueStore.getInstance();

        List<KeyTransferSenderThread.KeyTransfer> keyTransferList = new ArrayList<>();

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : kvStore.keySet()) {
            if (nodePool.getIdFromKey(entry.getKey().hashCode()) != idToMatch) continue;

            keyTransferList.add(new KeyTransferSenderThread.KeyTransfer(recipient, entry, isReplica, true));
        }

        if (keyTransferList.size() == 0) {
            Logger.log("No keys to send to: " + recipient);
            return;
        }

        Logger.log("Sending " + keyTransferList.size() + " total keys to: " + recipient);
        sendKeyTransferBatches(keyTransferList, recipient);
    }

    public void sendRequest(KeyValueRequest.KVRequest request, Host host) {
        keysToSend.add(new KeyTransferSenderThread.KeyTransfer(host, request));
    }

    private void sendKeyTransferBatches(List<KeyTransferSenderThread.KeyTransfer> keyTransferList, Host recipient) {
        List<List<KeyTransferSenderThread.KeyTransfer>> batches = new ArrayList<>();

        int batchCount = (keyTransferList.size() + BATCH_SIZE - 1) / BATCH_SIZE;
        for (int i = 0; i < batchCount; i++) {
            int fromIndex = i * BATCH_SIZE;
            int toIndex = Math.min(fromIndex + BATCH_SIZE, keyTransferList.size());
            batches.add(keyTransferList.subList(fromIndex, toIndex));
        }

        for (List<KeyTransferSenderThread.KeyTransfer> batchKeyTransferList : batches) {
            Logger.log("Sending " + keyTransferList.size() + " keys to: " + recipient);

            (new KeyTransferThread(batchKeyTransferList)).start();
        }
    }
}
