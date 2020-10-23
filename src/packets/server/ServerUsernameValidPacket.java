package packets.server;

import packets.Packet;

public class ServerUsernameValidPacket implements Packet {

    public final String username;

    public ServerUsernameValidPacket(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("ServerUsernameValidPacket (%s)", username);
    }
}
