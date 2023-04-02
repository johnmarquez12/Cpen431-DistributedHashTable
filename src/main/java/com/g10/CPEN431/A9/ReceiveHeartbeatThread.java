package com.g10.CPEN431.A9;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest;
import ca.NetSysLab.ProtocolBuffers.Message;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.concurrent.BlockingQueue;
import java.util.zip.CRC32;

public class ReceiveHeartbeatThread extends Thread{

    private final BlockingQueue<UDPServer.Request> heartbeatMessages;

    public ReceiveHeartbeatThread(BlockingQueue<UDPServer.Request> heartbeatMessages) {
        super("ReceiveHeartbeatThread");
        this.heartbeatMessages = heartbeatMessages;
    }

    public void run() {
        Logger.log("Received Heartbeat Thread started");

        while(true) {

            UDPServer.Request heartbeatMessage = null;

            try {
                heartbeatMessage = heartbeatMessages.take();
            } catch (InterruptedException e) {
                Logger.err(e.getMessage());
            }

            if (heartbeatMessage == null) continue;

            KeyValueRequest.KVRequest kvRequest = unpackRequest(heartbeatMessage.payload);

            if (kvRequest == null) continue;

            ReceiveHeartbeatHandler.handleHeartbeatRequest(kvRequest);
        }
    }

    private KeyValueRequest.KVRequest unpackRequest(byte[] requestPayload) {
        Message.Msg request;
        byte[] messageID, applicationRequestPayload;
        CRC32 crc32 = new CRC32();

        try {
            request = getMessage(requestPayload);
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

        KeyValueRequest.KVRequest kvRequest = null;

        try {
            kvRequest = KeyValueRequest.KVRequest.parseFrom(applicationRequestPayload);
        } catch (InvalidProtocolBufferException e) {
            Logger.log("Unable to decode kvRequest from receive heartbeat");
        }

        return kvRequest;
    }

    public Message.Msg getMessage(byte[] requestPayload) throws InvalidProtocolBufferException {
        return Message.Msg.parseFrom(requestPayload);
    }
}
