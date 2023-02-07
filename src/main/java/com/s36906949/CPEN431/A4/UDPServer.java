package com.s36906949.CPEN431.A4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPServer {

    public static class Request {
        public final InetAddress address;
        public final int port;
        public final byte[] payload;

        public Request(InetAddress address, int port, byte[] payload) {
            this.address = address;
            this.port = port;
            this.payload = payload;
        }
    }

    public static void run(int port) throws SocketException {
        byte[] buf = new byte[65567];
        DatagramSocket socket = new DatagramSocket(port);

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        Queue<Request> requests = new LinkedBlockingQueue<>();

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
