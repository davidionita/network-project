package packets.server;

import packets.Packet;

public class ServerErrorPacket implements Packet {

    public final String message;

    public ServerErrorPacket(String message) {
        this.message = message;
    }

}
