package com.g10.CPEN431.A7;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationThread extends Thread {

    private final Queue<UDPServer.Request> requests;

    public ApplicationThread(Queue<UDPServer.Request> requests) {
        super("ApplicationThread");
        this.requests = requests;
    }

    public void run() {
        //Queue<ReplyThread.Reply> replies = new LinkedBlockingQueue<>();

        //new ReplyThread(replies).start();

        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            UDPServer.Request request = requests.poll();
            if (request == null) {
                Thread.yield();
                continue;
            }

            new RequestHandlerService(
                request.requestHost,
                request.payload,
                socket
            ).run();
        }
    }
}
