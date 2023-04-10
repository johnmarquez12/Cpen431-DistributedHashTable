package com.g10.CPEN431.A11;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

// TODO: Should this be a thread??
// Since we may send many keys over, our rejoin function could take a while to run
public class KeyTransferHandler {

    private static final int BATCH_SIZE = 1000;

    private final BlockingQueue<KeyTransferSenderThread.KeyTransfer> keysToSend;

    public KeyTransferHandler(BlockingQueue<KeyTransferSenderThread.KeyTransfer> keysToSend) {
        this.keysToSend = keysToSend;
    }

    public void sendKeys(Host recipient, int idToMatch, boolean isReplica) {
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

    // TODO: This logic can just be put into the function above
    public void sendKeysRejoin(Host recipient, int idToMatch, boolean isReplica, boolean deleteKeys) {
        NodePool nodePool = NodePool.getInstance();
        KeyValueStore kvStore = KeyValueStore.getInstance();

        List<KeyTransferSenderThread.KeyTransfer> keyTransferList = new ArrayList<>();

        for (Map.Entry<ByteString, KeyValueStore.ValueWrapper> entry : kvStore.keySet()) {
            if (nodePool.getIdFromKey(entry.getKey().hashCode()) != idToMatch) continue;

            keyTransferList.add(new KeyTransferSenderThread.KeyTransfer(recipient, entry, isReplica, true));

            if (deleteKeys) {
                try {
                    kvStore.remove(entry.getKey());
                } catch (KeyValueStore.NoKeyError nke) {
                    Logger.err("Key to remove was not found");
                }
            }
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
