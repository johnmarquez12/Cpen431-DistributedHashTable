package com.g10.CPEN431.A11;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class RepairThread extends Thread {

    public enum RepairType {
        REJOIN,
        REMOVE
    };
    public static class NodeToRepair {
        public RepairType repairType;
        public NodePool.Heartbeat hb;

        public NodeToRepair(NodePool.Heartbeat hb, RepairType repairType) {
            this.hb = hb;
            this.repairType = repairType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeToRepair that = (NodeToRepair) o;
            return hb.equals(that.hb);
        }
    }

    private final BlockingQueue<NodeToRepair> nodesToRepair;
    private final KeyTransferHandler keyTransferer;

    private boolean replicated = false;

    public RepairThread(BlockingQueue<NodeToRepair> nodesToRepair, KeyTransferHandler keyTransferer) {
        super("Repair Thread");
        this.nodesToRepair = nodesToRepair;
        this.keyTransferer = keyTransferer;
    }

    public void run() {
        while(true) {
            boolean first = true;
            replicated = false;

            while(!nodesToRepair.isEmpty()) {
                if (first) {
                    try {
                        // Sleep to allow queue to accumulate nodes to repair
                        Logger.log("Received a repair, waiting for nodes to accumulate");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    first = false;
                    Logger.log("Starting Repair, Membership count at start of repair is " + NodePool.getInstance().aliveNodeCount());
                }

                NodeToRepair repair = null;

                try {
                    repair = nodesToRepair.take();
                } catch (InterruptedException e) {
                    Logger.log("Uh oh, shouldn't have gotten this");
                }

                if (repair == null) {
                    Logger.log("Repair should not have been null");
                    continue;
                }

                if (nodesToRepair.contains(repair)) {
                    Logger.log("Duplicate repair for " + repair.hb.host + " " + repair.repairType.toString());
                    continue;
                }

                Logger.log("Repairing node " + repair.hb.host +" "+ repair.repairType.toString());

                if (repair.repairType == RepairType.REMOVE) {
                    // handle remove
                    handleRemovedNode(repair.hb);
                } else if (repair.repairType == RepairType.REJOIN) {
                    // handle rejoin
                    handleJoinedNode(repair.hb);
                }
            }

            Thread.yield();
        }
    }

    private void handleRemovedNode(NodePool.Heartbeat hb) {
        NodePool nodePool = NodePool.getInstance();

        List<Map.Entry<Integer, Host>> myReplicaNodes = nodePool.getMyReplicaNodes();
        int myId = nodePool.getMyId();

        if (nodePool.getIdFromKey(hb.id) == nodePool.getIdFromKey(myId) && !replicated) {
            Logger.log("Our replica (" + hb.host.port +
                ") has died. Re-replicate our keys");

            myReplicaNodes.forEach(entry -> keyTransferer.sendKeys(entry.getValue(), myId, true));
            replicated = true;
        }

        if (nodePool.isPredecessor(hb.id) && !replicated) {
            Logger.log("Previous node (%d) died. Replicate its replicas to new node", hb.host.port);
            myReplicaNodes.forEach(entry -> keyTransferer.sendKeys(entry.getValue(), myId, true));
            replicated = true;
        }
    }

    private void handleJoinedNode(NodePool.Heartbeat hb) {
        NodePool nodePool = NodePool.getInstance();
        int myId = nodePool.getMyId();

        boolean deleteKeys = nodePool.getReplicasForId(hb.id).stream().map(Map.Entry::getKey).noneMatch(id -> id == myId);

        Logger.log("Server (%d) rejoined. Send keys that we have that belong to it", hb.host.port);
        keyTransferer.sendKeysRejoin(hb.host, hb.id, false, deleteKeys);

        if (nodePool.getMyReplicaNodes().stream().map(Map.Entry::getKey).anyMatch(id -> id == hb.id)) { // new node should replicate us
            Logger.log("Rejoined server (%d) is one of our replicas. Replicate our keys", hb.host.port);
            keyTransferer.sendKeys(hb.host, myId, true);
        }

    }


}
