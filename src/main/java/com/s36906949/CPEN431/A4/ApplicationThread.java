package com.s36906949.CPEN431.A4;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationThread extends Thread {

    private final Queue<UDPServer.Request> requests;

    public ApplicationThread(Queue<UDPServer.Request> requests) {
        super("ApplicationThread");
        this.requests = requests;
    }

    public void run() {
        Queue<ReplyThread.Reply> replies = new LinkedBlockingQueue<>();

        new ReplyThread(replies).start();

        while(true) {
            UDPServer.Request request = requests.poll();
            if (request == null) continue;

            new RequestHandlerService(
                request.address,
                request.port,
                request.payload,
                replies
            ).run();
        }
    }
}
