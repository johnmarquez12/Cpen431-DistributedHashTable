package com.g10.CPEN431.A7;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationThread extends Thread {

    private final LinkedBlockingQueue<UDPServer.Request> requests;

    public ApplicationThread(LinkedBlockingQueue<UDPServer.Request> requests) {
        super("ApplicationThread");
        this.requests = requests;
    }

    public void run() {
        LinkedBlockingQueue<ReplyThread.Reply> replies = new LinkedBlockingQueue<>();

        new ReplyThread(replies).start();

        while(true) {
            try {
                UDPServer.Request request = requests.take();

                new RequestHandlerService(
                        request.requestHost,
                        request.payload,
                        replies
                ).run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
