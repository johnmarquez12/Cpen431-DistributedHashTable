package com.g10.CPEN431.A11;


import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class InternalClient {

    private static final int MAX_RETRIES = 3;
    private static final int MAX_PAYLOAD_SIZE = 16384;

    private final DatagramSocket socket;
    public InternalClient() {
        try {
            socket = new DatagramSocket();
        } catch(SocketException socketException) {
            throw new RuntimeException(socketException);
        }
    }

    public synchronized void sendRequest(byte[] payload, Host recipient) throws IOException {

        try {

            byte[] requestId = generateRequestId(recipient);
            long checksum = generateCheckSum(requestId, payload);

            Message.Msg requestMessage = Message.Msg.newBuilder()
                    .setMessageID(ByteString.copyFrom(requestId))
                    .setPayload(ByteString.copyFrom(payload))
                    .setCheckSum(checksum)
                    .build();

            byte[] message = requestMessage.toByteArray();

            DatagramPacket packetToSend = new DatagramPacket(
                    message,
                    message.length,
                    recipient.address(),
                    recipient.port()
            );

            socket.send(packetToSend);
        } catch (SocketException se) {
            throw new RuntimeException(se);
        }
    }

    public synchronized KeyValueResponse.KVResponse sendRequestWithRetries(byte[] payload, Host recipient) throws IOException {

        byte[] requestID = generateRequestId(recipient);


        long checksum = generateCheckSum(requestID, payload);

        Message.Msg m = Message.Msg.newBuilder()
                .setMessageID(ByteString.copyFrom(requestID))
                .setPayload(ByteString.copyFrom(payload))
                .setCheckSum(checksum)
                .build();

        Logger.log("Sent packet with id hash " + m.getMessageID().toString() + " to port" + recipient.port);

        byte[] txBuf = m.toByteArray();
        DatagramPacket txPacket = new DatagramPacket(txBuf, txBuf.length, recipient.address, recipient.port);

        // Modified from chatGPT
        int retries = 0;
        int timeoutMs = 100;
        Message.Msg resp = null;

        boolean mismatched = false;

        while (retries <= MAX_RETRIES) {
            byte[] rxBuf = new byte[MAX_PAYLOAD_SIZE];
            DatagramPacket rxPacket = new DatagramPacket(rxBuf, rxBuf.length);
            CRC32 crc32 = new CRC32();

            try {
                if(!mismatched)
                    socket.send(txPacket);
                socket.setSoTimeout(timeoutMs);
                socket.receive(rxPacket);

                resp = Message.Msg.parseFrom(
                        Arrays.copyOf(rxPacket.getData(), rxPacket.getLength())
                );

                if(!resp.isInitialized()) throw new SocketException("Protobuf message not fully initialized");

                crc32.reset();
                crc32.update(resp.getMessageID().toByteArray());
                crc32.update(resp.getPayload().toByteArray());

                if(crc32.getValue() != resp.getCheckSum()) {
                    throw new SocketException("CRC32 failed");
                }

                mismatched = false;

                for (int i = 0; i < requestID.length; i++) {
                    if (requestID[i] != resp.getMessageID().byteAt(i)) {
                        // TODO: Why do we hit this?
                        Logger.log("err >>>");
                        Logger.log("Received packet with id hash " + resp.getMessageID().toString());

                        Logger.log("Received from port %d. On try %d.", rxPacket.getPort(), retries);

                        Logger.log("err <<<");
                        mismatched = true;
                        break;
//                        throw new SocketException("Mismatched request IDs");
                    }
                }
                if(mismatched) continue;
                break;
            } catch (SocketTimeoutException | SocketException |
                     InvalidProtocolBufferException e) {
                if (e.getClass().equals(SocketTimeoutException.class)) {
                    Logger.err("Timed out after %d ms... retrying%n", timeoutMs);
                } else { 
                    Logger.err("%s... retrying%n", e.getMessage());
                }
                if(retries++ == MAX_RETRIES) {
                    if (e.getClass() == InvalidProtocolBufferException.class)
                        throw new SocketException("Invalid message protocol received");
                    throw e;
                }
                timeoutMs *= 2;
            }
        }

        // This should never occur, but we have it to help Intellij with typechecking
        if (resp == null) throw new SocketException("Protobuf message not fully initialized");

        return KeyValueResponse.KVResponse.parseFrom(resp.getPayload());
    }

    private static byte[] generateRequestId(Host recipient) {
        ByteBuffer bb = ByteBuffer.allocate(16);

        bb.put(recipient.address().getAddress(), 0, 4);
        bb.putShort((short) recipient.port());

        byte[] randomBytes = new byte[2];
        new Random().nextBytes(randomBytes);

        Random rd = new Random();
        bb.putShort((short) rd.nextInt());
        bb.putLong(System.nanoTime());

        if(bb.hasRemaining()) throw new RuntimeException("Missing bytes in unique ID");

        return bb.array();
    }

    private static long generateCheckSum(byte[] requestId, byte[] payload) {
        CRC32 checkSum = new CRC32();
        checkSum.update(requestId);
        checkSum.update(payload);

        return checkSum.getValue();
    }


}
