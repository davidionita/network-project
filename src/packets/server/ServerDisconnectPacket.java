package packets.server;

import packets.Packet;

import java.util.List;

public class ServerDisconnectPacket implements Packet {

    public final String username;
    public final List<String> connectedUsers;

    public ServerDisconnectPacket(String username, List<String> connectedUsers) {
        this.username = username;
        this.connectedUsers = connectedUsers;
    }

}
