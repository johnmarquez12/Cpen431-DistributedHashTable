package com.g10.CPEN431.A6;


import java.io.IOException;
import java.net.*;

public class InternalClient {

    /**
     * @param payload note this payload will be probably be a new protobuf or Message
     * @
     */
    public record InternalRequest(byte[] payload, InetAddress nodeAddress, int nodePort) { }

    private static final int TIMEOUT = 100;
    private static final int MAX_TIMEOUT = 1000;
    private static final int NUM_RETRIES = 3;


    // TODO: instead of returning byte array, return a protobuf Message or new protobuf we define
    public static byte[] sendRequest(InternalRequest internalRequest) {
        int timeout = TIMEOUT;

        DatagramPacket packetToSend = new DatagramPacket(
                internalRequest.payload,
                internalRequest.payload.length,
                internalRequest.nodeAddress,
                internalRequest.nodePort
        );

        for (int i = 0; i <= NUM_RETRIES; i++) {
            try (DatagramSocket socket = new DatagramSocket()) {

                socket.setSoTimeout(timeout);
                socket.send(packetToSend);

                byte[] buffer = new byte[16384];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);


                socket.receive(receivePacket);
                // TODO: add reply checksum check
//                if (verifyInternalResponseValid())
//                    return receivePacket.getData();

                return receivePacket.getData();
            } catch (SocketTimeoutException soe) {
                timeout = Math.min(timeout * 2, MAX_TIMEOUT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new byte[0];
    }

    // TODO: verify checksum
//    private boolean verifyInternalResponseValid() {
//        return true;
//    }

}
