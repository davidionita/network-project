package packets.client;

import packets.Packet;

public class ClientListPacket implements Packet {
    /*
     * Requests lists of all clients connected to server
     */

    @Override
    public String toString() {
        return getClass().toString();
    }
}