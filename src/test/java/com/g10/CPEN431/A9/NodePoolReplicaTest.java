package com.g10.CPEN431.A9;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class NodePoolReplicaTest {

    InetAddress tmp = InetAddress.getByName("localhost");

    List<Host> servers = List.of(new Host(tmp, 1), new Host(tmp, 5555), new Host(tmp, 3), new Host(tmp, 4), new Host(tmp, 5), new Host(tmp, 6));

    public NodePoolReplicaTest() throws UnknownHostException {}


    @After
    public void cleanup() throws NoSuchFieldException, IllegalAccessException {
        Field instance = NodePool.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void givenMyIdStart_WhenGetReplicas_ExpectReplicasValid() {
        int port = 1;
        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);

        Map<Integer, Host> result = NodePool.getInstance().getMyReplicaNodes();

        assertEquals(3, result.size());

        assertTrue(result.containsValue(new Host(tmp, 5555)));
        assertTrue(result.containsValue(new Host(tmp, 3)));
        assertTrue(result.containsValue(new Host(tmp, 4)));
    }

    @Test
    public void givenMyIdInMiddle_WhenGetReplicas_ExpectReplicasValid() {
        int port = 5555;
        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);

        Map<Integer, Host> result = NodePool.getInstance().getMyReplicaNodes();

        assertEquals(3, result.size());

        assertTrue(result.containsValue(new Host(tmp, 3)));
        assertTrue(result.containsValue(new Host(tmp, 4)));
        assertTrue(result.containsValue(new Host(tmp, 5)));
    }

    @Test
    public void givenMyIdNearTheEnd_WhenGetReplicas_ExpectReplicasValid() {
        int port = 5;
        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);

        Map<Integer, Host> result = NodePool.getInstance().getMyReplicaNodes();

        assertEquals(3, result.size());

        assertTrue(result.containsValue(new Host(tmp, 6)));
        assertTrue(result.containsValue(new Host(tmp, 1)));
        assertTrue(result.containsValue(new Host(tmp, 5555)));
    }

    @Test
    public void givenMyIdAtTheEnd_WhenGetReplicas_ExpectReplicasValid() {
        int port = 6;
        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);

        Map<Integer, Host> result = NodePool.getInstance().getMyReplicaNodes();

        assertEquals(3, result.size());

        assertTrue(result.containsValue(new Host(tmp, 1)));
        assertTrue(result.containsValue(new Host(tmp, 5555)));
        assertTrue(result.containsValue(new Host(tmp, 3)));
    }


    private static InetAddress getMyHost() {
        return InetAddress.getLoopbackAddress();
    }
}
