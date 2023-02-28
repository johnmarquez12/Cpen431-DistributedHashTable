package com.g10.CPEN431.A6;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import com.google.protobuf.InvalidProtocolBufferException;

public class ReceiveHeartbeatHandler extends Thread {

    private byte[] payload;
    private final NodePool nodePool;
    private final Host myHost;
    private InternalRequest.InternalRequestWrapper response;

    public ReceiveHeartbeatHandler(byte[] payload) {
        super("ReceiveHeartbeatHandler");
        this.payload = payload;
        this.nodePool = NodePool.getInstance();
        this.myHost = nodePool.getMyHost();
    }

    public void run() {
        try {
            response = InternalRequest.InternalRequestWrapper.parseFrom(payload);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        for(InternalRequest.Heartbeat heartbeat : response.getHeartbeatsList()){
            nodePool.updateTimeStampFromId(heartbeat.getId(), heartbeat.getEpochMillis());
        }
    }
}
