package com.g10.CPEN431.A9;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;

import java.util.List;

public class ReceiveHeartbeatHandler {
    public static void updateHeartbeats(List<InternalRequest.Heartbeat> heartbeatsList) {
        NodePool nodePool = NodePool.getInstance();

        for(InternalRequest.Heartbeat heartbeat : heartbeatsList){
            nodePool.updateTimeStampFromId(heartbeat.getId(), heartbeat.getEpochMillis());
        }

         Logger.logVerbose("Updated heartbeats.");
    }

    public static void handleHeartbeatRequest(KeyValueRequest.KVRequest kvRequest) {
        if (kvRequest.getCommand() == Codes.Commands.INTERNAL_REQUEST) {

            if (kvRequest.hasIr() && kvRequest.getIr().getHeartbeatsCount() > 0) {
                updateHeartbeats(kvRequest.getIr().getHeartbeatsList());
            }

        }
    }
}
