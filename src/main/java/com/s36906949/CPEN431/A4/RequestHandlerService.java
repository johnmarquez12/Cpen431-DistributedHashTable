package com.s36906949.CPEN431.A4;

import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.zip.CRC32;

public class RequestHandlerService {
    private final InetAddress responseAddress;
    private final int responsePort;
    private final byte[] requestPayload;

    private final Queue<ReplyThread.Reply> replies;

    public RequestHandlerService(InetAddress address, int port, byte[] packetPayload, Queue<ReplyThread.Reply> replies) {
        responseAddress = address;
        responsePort = port;
        requestPayload = packetPayload;
        this.replies = replies;
    }

    public void run() {
        Message.Msg request;
        byte[] messageID, applicationRequestPayload;
        CRC32 crc32 = new CRC32();

        try {
            request = getMessage();
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
            // TODO: KILL THREAD
        }

        messageID = request.getMessageID().toByteArray();
        applicationRequestPayload = request.getPayload().toByteArray();

        crc32.reset();
        crc32.update(messageID);
        crc32.update(request.getPayload().asReadOnlyByteBuffer());

        if (request.getCheckSum() != crc32.getValue()) {
            throw new RuntimeException("Incorrect checksum sent");
            // TODO: Kill thread
        }

        ByteString applicationResponse;

        try {
            applicationResponse = RequestReplyCache.getInstance().get(
                request.getMessageID(),
                // TODO: maybe applicationRequestPayload should be weak ref?
                new Application(applicationRequestPayload)
            );
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
            // TODO: log/kill thread or something
        }
        /*
        // For now, we want to really SEE this error. Later, we'll catch it.
        catch (OutOfMemoryError e) {
            System.err.println("[MEM] Out of memory");
            throw new RuntimeException(e);
            // TODO: kill thread
        }
         */

//        ReplyThread Stuff
        replies.add(new ReplyThread.Reply(messageID, applicationResponse,
            responseAddress, responsePort));
    }

    public Message.Msg getMessage() throws InvalidProtocolBufferException {
        return Message.Msg.parseFrom(requestPayload);
    }
}
