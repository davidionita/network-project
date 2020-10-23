package packets.server;

import packets.Packet;

import java.util.Set;

public class ServerRoutedMessagePacket implements Packet {

    public final String senderUsername;
    public final String message;
    public final boolean isPrivate;
    public final Set<String> recipients;

    public ServerRoutedMessagePacket(String senderUsername, String message, boolean isPrivate, Set<String> recipients) {
        this.senderUsername = senderUsername;
        this.message = message;
        this.isPrivate = isPrivate;
        this.recipients = recipients;
    }

}
