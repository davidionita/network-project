package client;

import logs.LogType;
import logs.Logger;
import packets.Packet;
import packets.PacketType;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerConnectionHandler implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    private BufferedReader socketIn;
    private Logger logger;

    public ServerConnectionHandler(BufferedReader socketIn, Logger logger) {
        this.socketIn = socketIn;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            String inputMessage;

            while ((inputMessage = socketIn.readLine()) != null) {
                logger.log(inputMessage, LogType.PACKET_RECEIVED, ChatClient.DEBUG_MODE);
                Packet receivedPacket = new Packet(inputMessage);

                if (receivedPacket.type == PacketType.SERVER_ROUTED_MESSAGE) {
                    String[] parts = receivedPacket.info.split("\\^", 3);
                    String username = parts[0];
                    Date timestamp = new Date(Long.parseLong(parts[1]));
                    String message = parts[2];

                    logger.log(String.format("%s @ %s > %s", username, new SimpleDateFormat().format(timestamp), message), LogType.CHAT);
                } else if (receivedPacket.type == PacketType.SERVER_ROUTED_PRIVATE_MESSAGE) {
                    String[] parts = receivedPacket.info.split("\\^", 3);
                    String username = parts[0];
                    Date timestamp = new Date(Long.parseLong(parts[1]));
                    String message = parts[2];

                    logger.log(String.format("%s %s (Privately) %s @ %s > %s", username, ANSI_RED, ANSI_RESET, new SimpleDateFormat().format(timestamp), message), LogType.CHAT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Client Listener exiting");
        }
    }
}
