package com.g10.CPEN431.A6;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;

public class ReceiveHeartbeatHandler {
    public static void updateHeartbeats(List<InternalRequest.Heartbeat> heartbeatsList) {
        NodePool nodePool = NodePool.getInstance();

        for(InternalRequest.Heartbeat heartbeat : heartbeatsList){
            nodePool.updateTimeStampFromId(heartbeat.getId(), heartbeat.getEpochMillis());
        }

//        System.out.println("Updated heartbeats");

        nodePool.killDeadNodes();
    }
}
