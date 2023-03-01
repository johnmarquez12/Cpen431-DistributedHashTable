package com.g10.CPEN431.A6;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class NodePoolTest {
    InetAddress tmp = InetAddress.getByName("localhost");

    List<Host> servers = List.of(new Host(tmp, 1), new Host(tmp, 5555), new Host(tmp, 3));

    public NodePoolTest() throws UnknownHostException {}


    @Before
    public void setup() throws UnknownHostException {
        int port = 5555;
        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);
    }

    @After
    public void cleanup() throws NoSuchFieldException, IllegalAccessException {
        Field instance = NodePool.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetNodesById() {
        assertEquals(servers.get(1),
            NodePool.getInstance().getHostFromId(NodePool.CIRCLE_SIZE/3 - 1));
        assertEquals(servers.get(2),
            NodePool.getInstance().getHostFromId(NodePool.CIRCLE_SIZE/3 + 5));
        assertEquals(servers.get(0),
            NodePool.getInstance().getHostFromId(NodePool.CIRCLE_SIZE - 1));
    }

    @Test
    public void testUpdateTimeStamp() {
        List<NodePool.Heartbeat> heartbeats = new ArrayList<>(NodePool.getInstance().getAllHeartbeats());

        for (NodePool.Heartbeat heartbeat : heartbeats) {
            assertEquals(0, heartbeat.epochMillis);

            NodePool.getInstance().updateTimeStampFromId(heartbeat.id, 10L * heartbeat.id);
            assertEquals(10L * heartbeat.id, heartbeat.epochMillis);

            //Set smaller value; shouldn't update
            NodePool.getInstance().updateTimeStampFromId(heartbeat.id, 5L * heartbeat.id);
            assertEquals(10L * heartbeat.id, heartbeat.epochMillis);
        }

    }

    @Test
    public void testKillDeadNodes() {
        NodePool.getInstance().updateTimeStampFromId(0, System.currentTimeMillis());

        NodePool.getInstance().killDeadNodes();

        assertEquals(1, NodePool.getInstance().getAllHeartbeats().size());

        assertEquals(0, NodePool.getInstance().getAllHeartbeats().get(0).id);
    }

    private static InetAddress getMyHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}
