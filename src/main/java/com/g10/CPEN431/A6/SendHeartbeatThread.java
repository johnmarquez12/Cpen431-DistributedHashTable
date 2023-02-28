package com.g10.CPEN431.A6;
import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import com.g10.CPEN431.A6.NodePool.Heartbeat;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SendHeartbeatThread extends Thread {

    //These values are arbitrary at the moment
    public static final long SLEEP = 500;
    public static final long MARGIN = 50;
    private final NodePool nodePool;

    private final Host myHost;
    private final List<Heartbeat> heartbeats;

    public SendHeartbeatThread() {
        super("SendHeartbeatThread");
        this.nodePool = NodePool.getInstance();
        this.myHost = nodePool.getMyHost();
        this.heartbeats = new ArrayList<>(nodePool.getAllHeartbeats());
    }

    public void run() {
        Random rand = new Random();

        while(true) {
            int destNode = rand.nextInt() % NodePool.CIRCLE_SIZE;
            Host host = nodePool.getHostFromId(destNode);

            if(host.equals(myHost)) {
                continue;
            }

            //TODO: send payload using InternalClient to destNode
            //InternalClient.sendInternalRequest(new InternalClient.InternalRequest(
                //generateHeartbeatPayload(), host.address(), host.port());

            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<InternalRequest.Heartbeat> generateProtobufHeartbeats() {
        List<InternalRequest.Heartbeat> protoHeartbeats = new ArrayList<>();

        for (Heartbeat heartbeat : heartbeats) {
            if (heartbeat.host.equals(myHost)) {
                heartbeat.epochMillis = System.currentTimeMillis();
            }
            InternalRequest.Host host = InternalRequest.Host.newBuilder()
                .setIp(ByteString.copyFrom(heartbeat.host.address().getAddress()))
                .setPort(heartbeat.host.port())
                .build();

            protoHeartbeats.add(InternalRequest.Heartbeat.newBuilder()
                .setHost(host)
                .setId(heartbeat.id)
                .setEpochMillis(heartbeat.epochMillis)
                .build());
        }
        return protoHeartbeats;
    }

    private byte[] generateHeartbeatPayload() {
        InternalRequest.InternalRequestWrapper request =
            InternalRequest.InternalRequestWrapper.newBuilder()
            .addAllHeartbeats(generateProtobufHeartbeats())
            .build();

        return request.toByteArray();
    }
}
