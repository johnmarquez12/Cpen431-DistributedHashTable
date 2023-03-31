package com.g10.CPEN431.A9;

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
    }

    private final BlockingQueue<NodeToRepair> nodesToRepair;
    private final KeyTransferHandler keyTransferer;

    public RepairThread(BlockingQueue<NodeToRepair> nodesToRepair, KeyTransferHandler keyTransferer) {
        super("Repair Thread");
        this.nodesToRepair = nodesToRepair;
        this.keyTransferer = keyTransferer;
    }

    public void run() {
        while(true) {
            boolean first = true;

            while(!nodesToRepair.isEmpty()) {
                if (first) {
                    try {
                        // Sleep to allow queue to accumulate nodes to repair
                        Thread.sleep(5000);
                        Logger.log("Starting Repair");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    first = false;
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

                Logger.log("Repairing node " + repair.hb.host);

                if (repair.repairType == RepairType.REMOVE) {
                    // handle remove
                    handleRemovedNodeRepair(repair.hb);
                } else if (repair.repairType == RepairType.REJOIN) {
                    // handle rejoin
                    handleJoinedNode(repair.hb);
                }
            }

            Thread.yield();
        }
    }

    private void handleRemovedNodeRepair(NodePool.Heartbeat hb) {
        /* If the removed node is one of our replicas, send all my keys to a new
         * replica.
         */
        NodePool nodePool = NodePool.getInstance();
        List<Map.Entry<Integer, Host>> myReplicaNodes = nodePool.getMyReplicaNodes();

        // if I now handle its keyspace, just re-replicate all keys in my keyspace
        if (nodePool.isInMyKeyspace(hb.id)) {

            // TODO: replace this with IR with replication
            myReplicaNodes.forEach(node -> keyTransferer.sendKeys(node.getValue(), nodePool.getMyId(), true));
        }

//        /* If we are the first replica of the removed node, we need to send
//           our copies of the removed nodes stuff to a new replica.
//         */
//        if(nodePool.shouldHandleTransfer(hb)) {
//            Logger.log("Previous node died. Take over as master and send replicas to new node");
//            keyTransferer.sendKeys(myReplicaNodes.get(myReplicaNodes.size()-1).getValue(), hb.id);
//        }
    }

    private void handleJoinedNode(NodePool.Heartbeat hb) {

//        NodePool nodePool = NodePool.getInstance();
//
//        if(!nodePool.shouldHandleTransfer(hb)) {
//            List<Map.Entry<Integer, Host>> oldReplicasFromHb = nodePool.getReplicasForId(nodePool.getIdFromKey(hb.id));
//            if(oldReplicasFromHb.get(oldReplicasFromHb.size()-1).getKey() == nodePool.getMyId()) {
//                Logger.log("Deleting unnecessary replicas since server "+hb.host.port + " rejoined.");
//                KeyValueStore.getInstance().deleteKeysForNodeWithId(hb.id);
//            }
//        }
//
//        if (nodePool.shouldHandleTransfer(hb)) {
//            Logger.log("Previous server rejoined.");
            keyTransferer.sendKeys(hb.host, hb.id, false);
            KeyValueStore.getInstance().deleteKeysForNodeWithId(hb.id);
        }


}
