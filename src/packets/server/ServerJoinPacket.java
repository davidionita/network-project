package packets.server;

import packets.Packet;

import java.util.List;

public class ServerJoinPacket implements Packet {

    public final String username;
    public final List<String> connectedUsers;

    public ServerJoinPacket(String username, List<String> connectedUsers) {
        this.username = username;
        this.connectedUsers = connectedUsers;
    }

    @Override
    public String toString() {
        return String.format("ServerJoinPacket (%s, %s)", username, String.join(", ", connectedUsers));
    }
}
