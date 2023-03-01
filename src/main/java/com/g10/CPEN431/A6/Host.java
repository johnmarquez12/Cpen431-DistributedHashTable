package com.g10.CPEN431.A6;

import ca.NetSysLab.ProtocolBuffers.InternalRequest;
import com.google.protobuf.ByteString;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

    public final InetAddress address;
    public final int port;

    public Host(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress address() {
        return address;
    }

    public int port() {
        return port;
    }

    public InternalRequest.Host toProtobuf() {
        return InternalRequest.Host.newBuilder()
            .setIp(ByteString.copyFrom(address.getAddress()))
            .setPort(port)
            .build();
    }

    public static Host fromProtobuf(InternalRequest.Host host)
        throws UnknownHostException {
        return new Host(InetAddress.getByAddress(host.getIp().toByteArray()),
            host.getPort());
    }
}
