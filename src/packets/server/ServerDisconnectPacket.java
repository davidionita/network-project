package packets.server;

import packets.Packet;

public class ServerDisconnectPacket implements Packet {

    public final String username;

    public ServerDisconnectPacket(String username) {
        this.username = username;
    }

}
