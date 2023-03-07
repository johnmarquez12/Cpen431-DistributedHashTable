package com.g10.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class ReplyThread extends Thread {

    public static class Reply {
        public final byte[] messageID;
        public final ByteString applicationResponse;
        public final Host responseHost;

        public Reply(byte[] messageID, ByteString applicationResponse,
                     Host responseHost) {
            this.messageID = messageID;
            this.applicationResponse = applicationResponse;
            this.responseHost = responseHost;
        }
    }

    private final LinkedBlockingQueue<Reply> replies;

    public ReplyThread(LinkedBlockingQueue<Reply> replies) {
        super("ReplyThread");
        this.replies = replies;
    }

    public void run() {
        CRC32 crc32 = new CRC32();
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }


        while(true) {
            try {
                Reply reply = replies.take();
                crc32.reset();
                crc32.update(reply.messageID);
                crc32.update(reply.applicationResponse.asReadOnlyByteBuffer());

                Message.Msg responseMsg = Message.Msg.newBuilder()
                        .setMessageID(ByteString.copyFrom(reply.messageID))
                        .setPayload(reply.applicationResponse)
                        .setCheckSum(crc32.getValue())
                        .build();

                byte[] response = responseMsg.toByteArray();

                DatagramPacket packet =
                        new DatagramPacket(response, response.length,
                                reply.responseHost.address(), reply.responseHost.port());

                socket.send(packet);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
