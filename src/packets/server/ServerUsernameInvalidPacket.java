package packets.server;

import packets.Packet;

public class ServerUsernameInvalidPacket implements Packet {

    @Override
    public String toString() {
        return this.getClass().toString();
    }
}
