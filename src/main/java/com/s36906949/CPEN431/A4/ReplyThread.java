package com.s36906949.CPEN431.A4;

import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class ReplyThread extends Thread {

    public static class Reply {
        public byte[] messageID;
        public ByteString applicationResponse;
        public InetAddress responseAddress;
        public int responsePort;

        public Reply(byte[] messageID, ByteString applicationResponse,
                     InetAddress responseAddress, int responsePort) {
            this.messageID = messageID;
            this.applicationResponse = applicationResponse;
            this.responseAddress = responseAddress;
            this.responsePort = responsePort;
        }
    }

    private Queue<Reply> replies;

    public ReplyThread(Queue<Reply> replies) {
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
            Reply reply = replies.poll();
            if (reply == null) {
                Thread.yield();
                continue;
            }


            crc32.reset();
            crc32.update(reply.messageID);
            crc32.update(reply.applicationResponse.asReadOnlyByteBuffer());

            Message.Msg responseMsg = Message.Msg.newBuilder()
                .setMessageID(ByteString.copyFrom(reply.messageID))
                .setPayload(reply.applicationResponse)
                .setCheckSum(crc32.getValue())
                .build();

            byte[] response = responseMsg.toByteArray();

            try {
                DatagramPacket packet =
                    new DatagramPacket(response, response.length,
                        reply.responseAddress, reply.responsePort);

                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
