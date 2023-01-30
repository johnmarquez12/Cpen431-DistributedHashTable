package com.s36906949.CPEN431.A4;

import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;

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
}
