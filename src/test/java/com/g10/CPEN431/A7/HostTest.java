package com.g10.CPEN431.A7;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class HostTest {

    @Test
    public void testEquality() throws UnknownHostException {
        InetAddress a = InetAddress.getByName("localhost");
        InetAddress b = InetAddress.getByName("127.0.0.1");
        InetAddress c = InetAddress.getByName("8.8.8.8");

        assertEquals(new Host(a, 10), new Host(b, 10));
        assertNotEquals(new Host(a, 10), new Host(b, 11));
        assertNotEquals(new Host(a, 10), new Host(c, 10));
    }
}
