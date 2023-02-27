package com.g10.CPEN431.A6;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class NodePool {

    private static NodePool INSTANCE;
    public static final int CIRCLE_SIZE = 128;

    public static class Heartbeat {
        public Heartbeat(String host, int port) {
            try {
                this.host = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                // TODO: what do we wanna do with this error?
                throw new RuntimeException(e);
            }
            this.port = port;
        }

        public InetAddress host;

        // todo: should port be "short" instead of "int"?
        public int port;
        public long epochMillis = 0; // System.currentTimeMillis()

        @Override
        public String toString() {
            return "Heartbeat{" +
                host + ":" + port +
                (epochMillis == 0 ? "" : ", epochMillis=" + epochMillis) +
                '}';
        }
    }



    // Note: we need an array of heatbeats in case two nodes share an id.
    private final TreeMap<Integer, Heartbeat[]> nodes;
    private final int myNodeId;

    private NodePool() {
        // Todo: figure out how we initially populate the tree
        //       For now, we'll stick some dummy data in it
        nodes = new TreeMap<>();

        Random rand = new Random();

        myNodeId = rand.nextInt() % CIRCLE_SIZE;

        // TODO: when adding an id, ensure its between 0 and circleSize

        nodes.put(7, new Heartbeat[] { new Heartbeat("google.com", 1) });
        nodes.put(18, new Heartbeat[] { new Heartbeat("bing.com", 2) });
        nodes.put(99, new Heartbeat[] { new Heartbeat("microsoft.com", 3) });
        nodes.put(1, new Heartbeat[] { new Heartbeat("google.com", 4) });
    }

    public static NodePool create() {
        if (INSTANCE != null) {
            throw new RuntimeException("The NodePool has already been created");
        }

        return INSTANCE = new NodePool();
    }

    public static NodePool getInstance() {
        if(INSTANCE == null) {
            throw new RuntimeException(
                "You must first instantiate the NodePool with NodePool.create()");
        }
        return INSTANCE;
    }

    public Map.Entry<Integer, Heartbeat[]> getNodesFromId(int id) {
        int myId = id % CIRCLE_SIZE;
        if (myId > nodes.lastKey()){
           return nodes.firstEntry();
        }

        return nodes.ceilingEntry(myId);
    }

    public int getMyNodeId() {
        return myNodeId;
    }

    /**
     * NOTE: You really shouldn't use this method. Instead, do this manually,
     * so you don't need to search the tree if false.
     */
    public boolean iShouldService(int id) {
        return getNodesFromId(id).getKey() == getMyNodeId();
    }
}
