package packets.server;

import packets.Packet;

import java.util.Date;
import java.util.Set;

public class ServerRoutedMessagePacket implements Packet {

    public final String senderUsername;
    public final String message;
    public final boolean isPrivate;
    public final Set<String> recipients;

    public final Date timestamp;

    public ServerRoutedMessagePacket(String senderUsername, String message, boolean isPrivate, Set<String> recipients) {
        this.senderUsername = senderUsername;
        this.message = message;
        this.isPrivate = isPrivate;
        this.recipients = recipients;

        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        String recipients = this.recipients != null ? String.join(", ", this.recipients) : "No recipients";
        return String.format("ServerRoutedMessagePacket (%s, %s, %b, %s)", senderUsername, message, isPrivate, recipients);
    }
}
