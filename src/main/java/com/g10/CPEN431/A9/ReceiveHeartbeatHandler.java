package com.g10.CPEN431.A9;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;

import java.util.List;

public class ReceiveHeartbeatHandler {
    public static void updateHeartbeats(List<InternalRequest.Heartbeat> heartbeatsList) {
        NodePool nodePool = NodePool.getInstance();

        for(InternalRequest.Heartbeat heartbeat : heartbeatsList){
            nodePool.updateTimeStampFromId(heartbeat.getId(), heartbeat.getEpochMillis());
        }

        // Logger.log("Updated heartbeats.");
    }
}
