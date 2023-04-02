package com.g10.CPEN431.A9;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HeartbeatServer extends Thread {

    private final int port;
    public HeartbeatServer(int port) {
        super("HeartbeatServerThread");
        this.port = port;
    }

    public void run() {
        byte[] buf = new byte[65567];

        DatagramSocket socket;

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            Logger.log("Failed to start Heartbeat server");
            throw new RuntimeException(e);
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        BlockingQueue<UDPServer.Request> requests = new LinkedBlockingQueue<>();

        new ReceiveHeartbeatThread(requests).start();

        Logger.log("Started Heartbeat Server on port: " + port);
        while(true) {
            try {
                socket.receive(packet);

                InetAddress senderAddress = packet.getAddress();
                int senderPort = packet.getPort();

                requests.add(new UDPServer.Request(
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
