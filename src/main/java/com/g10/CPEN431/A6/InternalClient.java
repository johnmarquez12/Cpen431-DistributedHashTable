package com.g10.CPEN431.A6;


import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class InternalClient {

    /**
     * @param payload note this payload will be probably be a new protobuf or Message
     * @
     */
    public record InternalRequest(byte[] payload, InetAddress nodeAddress, int nodePort) { }

    private static final int TIMEOUT = 100;
    private static final int MAX_TIMEOUT = 1000;
    private static final int NUM_RETRIES = 3;
    public static final int REQUEST_ID_SIZE = 16;

    public static ByteString sendRequest(InternalRequest internalRequest) throws IOException {

        try (DatagramSocket socket = new DatagramSocket()) {

            int timeout = TIMEOUT;

            byte[] requestId = generateRequestId(internalRequest.nodeAddress, internalRequest.nodePort);
            long checksum = generateCheckSum(requestId, internalRequest.payload);

            Message.Msg requestMessage = Message.Msg.newBuilder()
                    .setMessageID(ByteString.copyFrom(requestId))
                    .setPayload(ByteString.copyFrom(internalRequest.payload))
                    .setCheckSum(checksum)
                    .build();

            byte[] message = requestMessage.toByteArray();

            DatagramPacket packetToSend = new DatagramPacket(
                    message,
                    message.length,
                    internalRequest.nodeAddress,
                    internalRequest.nodePort
            );

            for (int i = 0; i <= NUM_RETRIES; i++) {
                try {
                    socket.setSoTimeout(timeout);
                    socket.send(packetToSend);

                    byte[] buffer = new byte[16384];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                    socket.receive(receivePacket);

                    Message.Msg replyMessage = Message.Msg.parseFrom(receivePacket.getData());

                    if (isResponseValid(requestMessage, replyMessage))
                        return replyMessage.getPayload();

                } catch (SocketTimeoutException soe) {
                    timeout = Math.min(timeout * 2, MAX_TIMEOUT);
                }
            }
        } catch (SocketException se) {
            throw new RuntimeException(se);
        }

        // out of retries, throw timeout
        throw new SocketTimeoutException("Out of retries");
    }

    private static boolean isResponseValid(Message.Msg request, Message.Msg reply) {
        byte[] requestId = request.getMessageID().toByteArray();
        byte[] replyId = reply.getMessageID().toByteArray();

        CRC32 replyCheckSum = new CRC32();
        replyCheckSum.update(replyId);
        replyCheckSum.update(reply.getPayload().toByteArray());

        return Arrays.equals(requestId, replyId) && replyCheckSum.getValue() == reply.getCheckSum();
    }

    private static byte[] generateRequestId(InetAddress address, int port) {

        byte[] ipToByteArray = address.getAddress();

        byte[] portToByteArray = new byte[2];
        portToByteArray[0] = (byte) (port & 0xFF);
        portToByteArray[1] = (byte) ((port >> 8) & 0xFF);

        byte[] randomBytes = new byte[2];
        new Random().nextBytes(randomBytes);

        byte[] requestTimeByteArray = ByteBuffer.allocate(Long.BYTES).putLong(System.nanoTime()).array();

        return ByteBuffer
                .allocate(REQUEST_ID_SIZE)
                .put(ipToByteArray)
                .put(portToByteArray)
                .put(randomBytes)
                .put(requestTimeByteArray)
                .array();
    }

    private static long generateCheckSum(byte[] requestId, byte[] payload) {
        CRC32 checkSum = new CRC32();
        checkSum.update(requestId);
        checkSum.update(payload);

        return checkSum.getValue();
    }


}
