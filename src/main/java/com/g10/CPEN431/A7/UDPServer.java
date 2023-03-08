package com.g10.CPEN431.A7;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPServer {

    public static class Request {
        public final Host requestHost;
        public final byte[] payload;

        public Request(InetAddress address, int port, byte[] payload) {
            this.requestHost = new Host(address, port);
            this.payload = payload;
        }
    }

    public static void run(int port) throws SocketException {
        byte[] buf = new byte[65567];
        DatagramSocket socket = new DatagramSocket(port);

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        BlockingQueue<Request> requests = new LinkedBlockingQueue<>();

        new ApplicationThread(requests).start();

        while(true) {
            try {
                socket.receive(packet);

                InetAddress senderAddress = packet.getAddress();
                int senderPort = packet.getPort();

                requests.add(new Request(
                    senderAddress,
                    senderPort,
                    Arrays.copyOf(packet.getData(), packet.getLength())
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
