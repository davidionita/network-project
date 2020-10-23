package client;

import logs.LogType;
import logs.Logger;
import packets.Packet;
import packets.server.*;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;

public class ServerConnectionHandler implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    private ObjectInput socketIn;
    private Logger logger;

    public ServerConnectionHandler(ObjectInputStream socketIn, Logger logger) {
        this.socketIn = socketIn;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            Packet input;

            while ((input = (Packet) socketIn.readObject()) != null) {
                logger.log(input.toString(), LogType.PACKET_RECEIVED, ChatClient.DEBUG_MODE);

                if(input instanceof ServerDisconnectPacket) {

                } else if(input instanceof ServerJoinPacket) {
                    ServerJoinPacket status = (ServerJoinPacket) input;

                    logger.log(String.format("'%s' has joined the server! Connected users: %s.", status.username, String.join(", ", status.connectedUsers)), LogType.CONNECTED);
                } else if(input instanceof ServerRoutedMessagePacket) {
                    ServerRoutedMessagePacket messagePacket = (ServerRoutedMessagePacket) input;

                    if(messagePacket.isPrivate) {
                        // private messages
                        if(messagePacket.recipients != null) {
                            if(messagePacket.recipients.size() == 0) {
                                // no one received the message
                                logger.log("Sending Error: None of the recipients you addressed your message are currently connected.", LogType.ERROR);
                            } else {
                                // list of recipients only returned to the sender
                                String recipients = String.join(", ", messagePacket.recipients);
                                logger.log(String.format("%s%s(Privately)%s @ %s > %s", recipients, ANSI_RED, ANSI_RESET, new SimpleDateFormat().format(messagePacket.timestamp), messagePacket.message), LogType.CHAT);
                            }
                        } else {
                            logger.log(String.format("%s %s (Privately)%s @ %s > %s", messagePacket.senderUsername, ANSI_RED, ANSI_RESET, new SimpleDateFormat().format(messagePacket.timestamp), messagePacket.message), LogType.CHAT);
                        }
                    } else {
                        // public messages
                        logger.log(String.format("%s @ %s > %s", messagePacket.senderUsername, new SimpleDateFormat().format(messagePacket.timestamp), messagePacket.message), LogType.CHAT);
                    }
                } else if(input instanceof ServerUsernameInvalidPacket) {
                    logger.log("Invalid username provided. Please try again.");
                } else if(input instanceof ServerUsernameValidPacket) {
                    logger.log(String.format("Username set to %s.", ((ServerUsernameValidPacket) input).username));
                } else if(input instanceof ServerErrorPacket) {
                    logger.log(String.format("Server Error > %s", ((ServerErrorPacket) input).message), LogType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Client Listener exiting");
        }
    }
}
