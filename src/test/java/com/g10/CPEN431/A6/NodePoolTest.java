package com.g10.CPEN431.A6;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;


public class NodePoolTest {

    @Before
    public void setup() {
        NodePool.create();
    }

    @Test
    public void testGetNodesById() {
        System.out.println("Hello world!");
        Map.Entry<Integer, NodePool.Heartbeat[]> x =
            NodePool.getInstance().getNodesFromId(128+8);
        System.out.println(x.getKey());
        System.out.println(Arrays.toString(x.getValue()));
    }
}
