package com.g10.CPEN431.A11;

import com.google.protobuf.ByteString;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    public static class NoKeyError extends Exception {}


    public static class ValueWrapper {
        public final ByteString value;
        public final int version;
        public final int counter;

        ValueWrapper(ByteString value, int version, int counter) {
            this.value = value;
            this.version = version;
            this.counter = counter;
        }
    }

    public static final int MAX_KEY_LENGTH = 32;
    public static final int MAX_VALUE_LENGTH = 10_000;

    private static KeyValueStore INSTANCE;
    private final ConcurrentHashMap<ByteString, ValueWrapper> store;

    private KeyValueStore() {
        store = new ConcurrentHashMap<>();
    }

    public static KeyValueStore getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new KeyValueStore();
        }

        return INSTANCE;
    }

    public void put(ByteString key, ByteString value, int version) {
        int counter = getCounterValue(key);

        store.put(key, new ValueWrapper(value, version, counter + 1));
    }

    public void putConsistency(ByteString key, ByteString value, int version, int newCounter) {
        int counter = getCounterValue(key);

        if(counter < newCounter) {
            store.put(key, new ValueWrapper(value, version, counter+1));
        } else {
            // Todo: maybe return an error?
            Logger.err("Consistency says ignore new value!");
        }
    }

    public int getCounterValue(ByteString key) {
        int counter = 0;
        ValueWrapper existing = store.get(key);
        if(existing != null) {
            counter = existing.counter;
        }

        return counter;
    }

    public ValueWrapper get(ByteString key) throws NoKeyError {
        ValueWrapper val = store.get(key);
        if (val == null) throw new NoKeyError();

        return store.get(key);
    }

    public void remove(ByteString key) throws NoKeyError {
        if (!store.containsKey(key)) throw new NoKeyError();
        store.remove(key);
    }

    public void wipeout() {
        store.clear();
    }

    public Enumeration<ByteString> getMembershipList() {
        return store.keys();
    }

    public int getMembershipSize() {
        return store.size();
    }

    public Set<Map.Entry<ByteString, ValueWrapper>> keySet() {
        return store.entrySet();
    }

    public void deleteKeysForNodeWithId(int id) {
        NodePool nodePool = NodePool.getInstance();
        store.entrySet().removeIf(item -> nodePool.getIdFromKey(item.getKey().hashCode()) == id);
    }
}
