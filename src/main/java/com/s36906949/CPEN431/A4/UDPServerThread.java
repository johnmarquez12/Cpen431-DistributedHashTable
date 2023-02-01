package com.s36906949.CPEN431.A4;

import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import static com.s36906949.CPEN431.A4.App.trueFreeMemory;
import static java.lang.Math.*;

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
        long last_free = 0;
        byte[] buf = new byte[65567];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

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

                // Todo: make pool of threads instead
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
