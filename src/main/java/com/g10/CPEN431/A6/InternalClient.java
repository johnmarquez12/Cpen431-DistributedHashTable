package com.g10.CPEN431.A6;


import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.CRC32;

public class InternalClient {
    public static void sendRequest(byte[] payload, Host recipient) throws IOException {

        try (DatagramSocket socket = new DatagramSocket()) {

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
