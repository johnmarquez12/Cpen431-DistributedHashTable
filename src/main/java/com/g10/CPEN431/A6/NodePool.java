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
        public Heartbeat(Host host) {
            this.host = host;
        }

        public Host host;
        public long epochMillis = 0; // System.currentTimeMillis()

        @Override
        public String toString() {
            return "Heartbeat{" + host +
                (epochMillis == 0 ? "" : ", epochMillis=" + epochMillis) +
                '}';
        }
    }



    // Note: we need an array of heatbeats in case two nodes share an id.
    private final TreeMap<Integer, Heartbeat> nodes;
    private Host me;

    private NodePool(Host me, Host[] servers) {
        // Todo: figure out how we initially populate the tree
        //       For now, we'll stick some dummy data in it
        this.me = me;

        nodes = new TreeMap<>();

        int spacing = CIRCLE_SIZE / servers.length;

        for (int i = 0; i < servers.length; i++) {
            nodes.put(i * spacing, new Heartbeat(servers[i]));
        }
    }

    public static NodePool create(Host me, Host[] servers) {
        if (INSTANCE != null) {
            throw new RuntimeException("The NodePool has already been created");
        }

        return INSTANCE = new NodePool(me, servers);
    }

    public static NodePool getInstance() {
        if(INSTANCE == null) {
            throw new RuntimeException(
                "You must first instantiate the NodePool with NodePool.create()");
        }
        return INSTANCE;
    }

    public Host getNodesFromId(int id) {
        int myId = id % CIRCLE_SIZE;
        if (myId > nodes.lastKey()){
           return nodes.firstEntry().getValue().host;
        }

        return nodes.ceilingEntry(myId).getValue().host;
    }

    public Host getMyHost() {
        return me;
    }
}
