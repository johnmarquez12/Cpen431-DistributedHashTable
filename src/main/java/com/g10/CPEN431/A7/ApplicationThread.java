package com.g10.CPEN431.A7;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationThread extends Thread {

    private final BlockingQueue<UDPServer.Request> requests;

    public ApplicationThread(BlockingQueue<UDPServer.Request> requests) {
        super("ApplicationThread");
        this.requests = requests;
    }

    public void run() {
        BlockingQueue<ReplyThread.Reply> replies = new LinkedBlockingQueue<>();

        new ReplyThread(replies).start();

        while(true) {
            UDPServer.Request request = null;

            try {
                request = requests.take();
            } catch (InterruptedException e) {
                Logger.err(e.getMessage());
            }

            if (request != null)
                new RequestHandlerService(
                        request.requestHost,
                        request.payload,
                        replies
                ).run();
        }
    }
}
