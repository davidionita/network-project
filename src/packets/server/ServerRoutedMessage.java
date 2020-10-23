package packets.server;

import packets.Packet;

public class ServerRoutedMessage implements Packet {

    public final String senderUsername;
    public final boolean isPrivate;

    public ServerRoutedMessage(String senderUsername, boolean isPrivate) {
        this.senderUsername = senderUsername;
        this.isPrivate = isPrivate;
    }

}
