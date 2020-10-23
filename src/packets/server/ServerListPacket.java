package packets.server;

import packets.Packet;

import java.util.List;

public class ServerListPacket implements Packet {

    public final List<String> connectedUsers;

    public ServerListPacket(List<String> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

}
