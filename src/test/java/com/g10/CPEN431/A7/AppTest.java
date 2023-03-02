package com.g10.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static com.g10.CPEN431.A7.App.parseServerFile;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void test() throws InvalidProtocolBufferException {
        KeyValueResponse.KVResponse x = KeyValueResponse.KVResponse.newBuilder()
            .setErrCode(0x00)
            .build();

        System.out.println(x.toByteString());


        KeyValueResponse.KVResponse y = KeyValueResponse.KVResponse.parseFrom(x.toByteString());

        System.out.println(y.getErrCode());
    }

    @Test
    public void testParseServerFile() throws UnknownHostException {
        List<Host> servers = parseServerFile(
            "src/test/java/com/g10/CPEN431/A7/serverListTest.txt");

        assertEquals(5, servers.size());

        assertEquals(new Host(InetAddress.getByName("localhost"), 5555),
            servers.get(0));
        assertEquals(new Host(InetAddress.getByName("localhost"), 1234),
            servers.get(1));
        assertEquals(new Host(InetAddress.getByName("ece.ubc.ca"), 90),
            servers.get(4));
    }
}
