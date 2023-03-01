package com.g10.CPEN431.A6;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReceiveHeartbeatHandler {
    public static void updateHeartbeats(byte[] payload) {
        NodePool nodePool = NodePool.getInstance();

        InternalRequest.InternalRequestWrapper response = null;

        try {
            response = InternalRequest.InternalRequestWrapper.parseFrom(payload);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        for(InternalRequest.Heartbeat heartbeat : response.getHeartbeatsList()){
            nodePool.updateTimeStampFromId(heartbeat.getId(), heartbeat.getEpochMillis());
        }

        nodePool.killDeadNodes();
    }
}
