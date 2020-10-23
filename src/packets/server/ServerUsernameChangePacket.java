package packets.server;

import packets.Packet;

import java.util.List;

public class ServerUsernameChangePacket implements Packet {

    public final String oldUsername;
    public final String newUsername;
    public final List<String> connectedUsers;

    public ServerUsernameChangePacket(String oldUsername, String newUsername, List<String> connectedUsers) {
        this.oldUsername = oldUsername;
        this.newUsername = newUsername;
        this.connectedUsers = connectedUsers;
    }

    @Override
    public String toString() {
        return String.format("ServerUsernameChangePacket (%s, %s, %s)", oldUsername, newUsername, String.format(", ", connectedUsers));
    }
}
