package com.g10.CPEN431.A11;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;

public class NodePool {

    private static NodePool INSTANCE;
    public static final int CIRCLE_SIZE = 1048576;

    private static final int REPLICATION_FACTOR = 4;
    private KeyTransferHandler keyTransferer;
    public static int TOTAL_NUM_NODES;

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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Heartbeat heartbeat = (Heartbeat) o;
            return id == heartbeat.id && host.equals(heartbeat.host);
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
    private final BlockingQueue<KeyTransferSenderThread.KeyTransfer> keysToSend;
    private final BlockingQueue<RepairThread.NodeToRepair> nodesToRepair;

    private final int spacing;
    private final Host me;
    private int myId;

    private NodePool(Host me, List<Host> servers) {

        //TODO: Application may need to have access to this queue, we should have a single place where we instantiate
        // all of our queues.
        TOTAL_NUM_NODES = servers.size();

        keysToSend = new LinkedBlockingQueue<>();
        keyTransferer = new KeyTransferHandler(keysToSend);
        (new KeyTransferSenderThread(keysToSend)).start();

        nodesToRepair = new LinkedBlockingQueue<>();
        (new RepairThread(nodesToRepair, keyTransferer)).start();



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
        return getEntryFromId(id).getValue();
    }

    public int getIdFromKey(int key) {
        return getEntryFromId(key).getKey();
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
    public synchronized List<Heartbeat> getAllHeartbeats() {
        heartbeats.get(indexFrom(myId)).epochMillis = System.currentTimeMillis();

        heartbeats.forEach(hb -> {
            if(!hb.deleted && expired(hb.epochMillis) && hb.id != myId) {
                removeNode(hb);
                Logger.log("Deleted (send) "+ hb +" since " +(SendHeartbeatThread.SLEEP * (log2Nodes() + SendHeartbeatThread.MARGIN)) + "ms have passed");
            }
        });

        return heartbeats;
    }

    public Heartbeat getHeartbeatFromHost(Host host) {
        return heartbeats.stream().filter(heartbeat -> heartbeat.host.equals(host)).findFirst().get();
    }

    public List<Map.Entry<Integer, Host>> getMyReplicaNodes() {
        return getReplicasForId(myId);
    }

    public List<Map.Entry<Integer, Host>> getReplicasForId(int id) {
        List<Map.Entry<Integer, Host>> replicas = new ArrayList<>();

        int nextNodeId = id + 1;

        for (int i = 0; i < REPLICATION_FACTOR - 1; i++) {
            Map.Entry<Integer, Host> entry = getEntryFromId(nextNodeId);
            // should never include "id", or duplicates.
            if(entry.getKey() == id) break;
            replicas.add(entry);
            nextNodeId = entry.getKey() + 1;
        }

        return replicas;
    }

    public List<Heartbeat> getAllHeartbeatsWithoutPruning() {
        return heartbeats;
    }

    /**
     * Kill node (only the one we're updating) if the updated time
     * means it should die
     */
    public synchronized void updateTimeStampFromId(int id, long epochMillis) {
        Heartbeat hb = heartbeats.get(indexFrom(id));

        if(epochMillis <= hb.epochMillis) {
            return;
        }

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
        Logger.log("Server "+hb.host+" has tried to rejoin");
        RepairThread.NodeToRepair repair = new RepairThread.NodeToRepair(hb, RepairThread.RepairType.REJOIN);

        nodesToRepair.add(repair);
    }

    public boolean isAlive(Host host) {
        return !expired(getHeartbeatFromHost(host).epochMillis);
    }

    /**
     * Used to forward a request if it needs to be replicated.
     * @param request
     * @return
     */
    public boolean sendReplicas(KeyValueRequest.KVRequest request) {
        int counter = KeyValueStore.getInstance().getCounterValue(request.getKey()) + 1;

        for(Map.Entry<Integer, Host> host : getMyReplicaNodes()) {
            request = request.toBuilder().setIr(
                InternalRequest.InternalRequestWrapper.newBuilder()
                    .setReplicate(true)
                    .setCounter(counter)
            ).build();

            keyTransferer.sendRequest(request, host.getValue());
        }
        return true;
    }

    public void removeNode(Heartbeat hb) {
        if (hb.deleted) return;

        hb.deleted = true;
        nodes.remove(hb.id);

        nodesToRepair.add(new RepairThread.NodeToRepair(hb, RepairThread.RepairType.REMOVE));
    }

    private int indexFrom(int id) {
        return id / spacing;
    }

    public boolean isPredecessor(int id) {
        return myId == getIdFromKey(id + 1);
    }

    public int hashToId(int hash) {
        int myHash = hash % CIRCLE_SIZE;
        if (myHash < 0) {
            myHash += CIRCLE_SIZE;
        }
        return myHash;
    }

    private Map.Entry<Integer, Host> getEntryFromId(int hash) {
        int id = hashToId(hash);

        if (id > nodes.lastKey()){
            return nodes.firstEntry();
        }

        return nodes.ceilingEntry(id);
    }
}
