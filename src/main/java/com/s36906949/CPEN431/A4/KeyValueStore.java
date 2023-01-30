package com.s36906949.CPEN431.A4;

import com.google.protobuf.ByteString;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    public static class ValueWrapper {
        public ByteString value;
        public int version;

        ValueWrapper(ByteString value, int version) {
            this.value = value;
            this.version = version;
        }
    }

    private static KeyValueStore INSTANCE;
    private ConcurrentHashMap<ByteString, ValueWrapper> store;

    private KeyValueStore() {
        store = new ConcurrentHashMap<>();
    }

    public static KeyValueStore getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new KeyValueStore();
        }

        return INSTANCE;
    }

    public void put(ByteString key, ByteString value) {
        put(key, value, 0);
    }

    public void put(ByteString key, ByteString value, int version) {
        store.put(key, new ValueWrapper(value, version));
    }

    public ValueWrapper get(ByteString key) {
        return store.get(key);
    }

    public void remove(ByteString key) {
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
