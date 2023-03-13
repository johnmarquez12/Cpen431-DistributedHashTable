package com.g10.CPEN431.A7;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

public class NodePool {

    private static NodePool INSTANCE;
    public static final int CIRCLE_SIZE = 128;

    public static class Heartbeat {
        public Host host;
        public long epochMillis;
        public int id;
        public boolean deleted;

        public Heartbeat(Host host, int id) {
            this.host = host;
            this.id = id;
            this.epochMillis = System.currentTimeMillis();
            this.deleted = false;
        }

        @Override
        public String toString() {
            return "Heartbeat{("+id+") " + host +
                (epochMillis == 0 ? "" : ", timestamp=" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault()).toLocalTime()) +
                '}';
        }
    }

    private final List<Heartbeat> heartbeats;
    private final ConcurrentSkipListMap<Integer, Host> nodes;
    private final int spacing;
    private final Host me;
    private int myId;

    private NodePool(Host me, List<Host> servers) {
        this.me = me;
        heartbeats = new ArrayList<>();

        nodes = new ConcurrentSkipListMap<>();

        spacing = CIRCLE_SIZE / servers.size();

        for (int i = 0; i < servers.size(); i++) {
            heartbeats.add(new Heartbeat(servers.get(i), i * spacing));

            if(servers.get(i).equals(me)) {
                myId = i * spacing;
            }

            nodes.put(i * spacing, servers.get(i));
        }
    }

    public static NodePool create(Host me, List<Host> servers) {
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

    public int totalNodeCount() {
        return heartbeats.size();
    }

    public int aliveNodeCount() {
        return nodes.size();
    }

    public Host getHostFromId(int id) {
        int myId = id % CIRCLE_SIZE;
        if (myId < 0) {
            myId += CIRCLE_SIZE;
        }

        if (myId > nodes.lastKey()){
           return nodes.firstEntry().getValue();
        }

        return nodes.ceilingEntry(myId).getValue();
    }

    public Host getHostFromIndex(int i) {
        return heartbeats.get(i).host;
    }

    public Host getMyHost() {
        return me;
    }

    public int getMyId() { return myId; }

    /**
     * Kill nodes (all) if updated time means it should die
     */
    public List<Heartbeat> getAllHeartbeats() {
        heartbeats.get(indexFrom(myId)).epochMillis = System.currentTimeMillis();

        heartbeats.forEach(hb -> {
            if(!hb.deleted && expired(hb.epochMillis) && hb.id != myId) {
                removeNode(hb);
                Logger.log("Deleted (send) "+ hb +" since " +(SendHeartbeatThread.SLEEP * (log2Nodes() + SendHeartbeatThread.MARGIN)) + "ms have passed");
            }
        });

        return heartbeats;
    }

    /**
     * Kill node (only the one we're updating) if the updated time
     * means it should die
     */
    public void updateTimeStampFromId(int id, long epochMillis) {
        Heartbeat hb = heartbeats.get(indexFrom(id));

        hb.epochMillis = Math.max(
            epochMillis,
            hb.epochMillis
        );

        if(!hb.deleted && expired(hb.epochMillis) && id != myId) {
            removeNode(hb);
            Logger.log("Deleted (receive) "+ hb +" since " +(SendHeartbeatThread.SLEEP * (log2Nodes() + SendHeartbeatThread.MARGIN)) + "ms have passed");
        } else if (hb.deleted && !expired(hb.epochMillis)) {
            // TODO: should we also double check the host isn't in `nodes`?
            rejoined(hb);
        }
    }

    private int log2Nodes() {
        return (int) (Math.log(heartbeats.size()) / Math.log(2)) + 1;
    }

    private boolean expired(long epochMillis) {
        long now = System.currentTimeMillis();
        long margin = SendHeartbeatThread.SLEEP * (log2Nodes() + SendHeartbeatThread.MARGIN);

        return now - epochMillis >= margin;
    }

    private void rejoined(Heartbeat hb) {
        nodes.put(hb.id, hb.host);
        // get next alive node since this node may contain data that should
        // belong to the rejoining node. If I am that node, handle it.
        hb.deleted = false;
        Logger.log("Server "+hb.id+" has tried to rejoin");

        if (shouldHandleTransfer(hb)) {
            try {
                KeyTransferHandler.sendKeys(hb.id);
            } catch (IOException e) {
                // TODO: this is really hacky since we keep passing up IOEXception, lets find another way to do this lol
                Logger.err(e.getMessage());
            }
        }
    }

    private void removeNode(Heartbeat hb) {
        if (hb.deleted) return;
        hb.deleted = true;
        nodes.remove(hb.id);
    }

    private int indexFrom(int id) {
        return id / spacing;
    }

    private boolean shouldHandleTransfer(Heartbeat hb) {
        return myId == getIdFromKey(hb.id + 1);
    }

    public int getIdFromKey(int key) {
        int myId = key % CIRCLE_SIZE;
        if (myId < 0) {
            myId += CIRCLE_SIZE;
        }

        if (myId > nodes.lastKey()){
            return nodes.firstEntry().getKey();
        }

        return nodes.ceilingKey(myId);
    }
}
