package packets.client;

import packets.Packet;

public class ClientUsernameRequestPacket implements Packet {

    public final String username;

    public ClientUsernameRequestPacket(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("ClientUsernameRequestPacket (%s)", username);
    }
}
