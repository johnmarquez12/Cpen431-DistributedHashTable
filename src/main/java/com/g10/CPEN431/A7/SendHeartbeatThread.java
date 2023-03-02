package com.g10.CPEN431.A7;
import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import com.g10.CPEN431.A7.NodePool.Heartbeat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SendHeartbeatThread extends Thread {

    //These values are arbitrary at the moment
    public static final long SLEEP = 500;
    public static final long MARGIN = 20;
    private final NodePool nodePool;

    private final Host myHost;

    public SendHeartbeatThread() {
        super("SendHeartbeatThread");
        this.nodePool = NodePool.getInstance();
        this.myHost = nodePool.getMyHost();
    }

    public void run() {
        Random rand = new Random();

        while(true) {
            int destNode = rand.nextInt() % NodePool.CIRCLE_SIZE;
            Host host = nodePool.getHostFromId(destNode);

            if(host.equals(myHost)) {
                continue;
            }

            NodePool.getInstance().killDeadNodes();

            //TODO: send payload using InternalClient to destNode
            try {
                Logger.log("Sending heartbeat to "+host + ":  "+nodePool.getAllHeartbeats());
                InternalClient.sendRequest(generateHeartbeatPayload(), host);
            } catch (IOException e) {
                System.err.println("Uh oh! Problem sending internal request");
                System.err.println(e.getMessage());
            }

            try {
                // Todo: is there a better way to do this?
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<InternalRequest.Heartbeat> generateProtobufHeartbeats() {
        List<NodePool.Heartbeat> heartbeats = new ArrayList<>(nodePool.getAllHeartbeats());
        List<InternalRequest.Heartbeat> protoHeartbeats = new ArrayList<>();

        for (Heartbeat heartbeat : heartbeats) {
            protoHeartbeats.add(InternalRequest.Heartbeat.newBuilder()
                .setHost(heartbeat.host.toProtobuf())
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

        return KeyValueRequest.KVRequest.newBuilder()
            .setCommand(Codes.Commands.INTERNAL_REQUEST)
            .setIr(request)
            .build().toByteArray();
    }
}
