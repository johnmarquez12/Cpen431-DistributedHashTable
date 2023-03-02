package com.g10.CPEN431.A7;

import com.google.protobuf.ByteString;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    public static class NoKeyError extends Exception {}


    public static class ValueWrapper {
        public final ByteString value;
        public final int version;

        ValueWrapper(ByteString value, int version) {
            this.value = value;
            this.version = version;
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
        store.put(key, new ValueWrapper(value, version));
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
}
