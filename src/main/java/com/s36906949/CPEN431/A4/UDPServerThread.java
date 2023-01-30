package com.s36906949.CPEN431.A4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPServerThread extends Thread {

    public final int port = 5555;
    private DatagramSocket socket;

    public UDPServerThread() throws IOException {
        this("UDPServer");
    }

    public UDPServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(port);

    }

    public void run() {
        while(true) {
            try {
                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("Waiting...");
                socket.receive(packet);

                System.out.println(Arrays.toString(packet.getData()));

                InetAddress senderAddress = packet.getAddress();
                int senderPort = packet.getPort();

                new RequestHandlerService(
                    senderAddress,
                    senderPort,
                    Arrays.copyOf(packet.getData(), packet.getLength())
                ).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
