package com.s36906949.CPEN431.A4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPServer {

    public static class Request {
        public InetAddress address;
        public int port;
        public byte[] payload;

        public Request(InetAddress address, int port, byte[] payload) {
            this.address = address;
            this.port = port;
            this.payload = payload;
        }
    }

    public static void run(int port) throws SocketException, InterruptedException {
        long last_free = 0;

        byte[] buf = new byte[65567];
        DatagramSocket socket = new DatagramSocket(port);

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        Queue<Request> requests = new LinkedBlockingQueue<>();

        new ApplicationThread(requests).start();

        while(true) {
            try {
                // todo: do we want/need to do this?
//                System.gc();

//                long totalMemory = Runtime.getRuntime().totalMemory();
//                long maxMemory = Runtime.getRuntime().maxMemory();
//                long freeMemory = Runtime.getRuntime().freeMemory();
//                int activeThreads = Thread.activeCount();
//
//                System.out.printf("[MEM (%d)] Free: %,8d kB | Total: %,8d kB | Max: %,8d kB | Threads: %2d | Free of total: %3d%% | Membership count: %d %n",
//                    ProcessHandle.current().pid(),freeMemory/1024, totalMemory/1024, maxMemory/1024, activeThreads, freeMemory*100/totalMemory, KeyValueStore.getInstance().getMembershipSize());

//                if (abs(trueFreeMemory()-last_free) > 1000*1024) {
//                    System.out.printf(
//                        "%,8d kB free of %,8d kB (%3d%%) | %,5d members%n",
//                        trueFreeMemory() / 1024,
//                        Runtime.getRuntime().maxMemory() / 1024,
//                        trueFreeMemory() * 100 /
//                            Runtime.getRuntime().maxMemory(),
//                        KeyValueStore.getInstance().getMembershipSize());
//                    last_free = trueFreeMemory();
//                }

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
