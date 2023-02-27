package com.g10.CPEN431.A6;

import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;


public class NodePoolTest {
    InetAddress tmp = InetAddress.getByName("localhost");

    Host[] servers = {new Host(tmp, 1), new Host(tmp, 5555), new Host(tmp, 3)};

    public NodePoolTest() throws UnknownHostException {}


    @Before
    public void setup() throws UnknownHostException {
        int port = 5555;
        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);
    }

    @Test
    public void testGetNodesById() {
        assertEquals(servers[1],
            NodePool.getInstance().getHostFromId(NodePool.CIRCLE_SIZE/3 - 1));
        assertEquals(servers[2],
            NodePool.getInstance().getHostFromId(NodePool.CIRCLE_SIZE/3 + 5));
        assertEquals(servers[0],
            NodePool.getInstance().getHostFromId(NodePool.CIRCLE_SIZE - 1));
    }

    private InetAddress getMyHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}
