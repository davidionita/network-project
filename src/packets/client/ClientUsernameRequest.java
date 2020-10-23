package packets.client;

import packets.Packet;

public class ClientUsernameRequest implements Packet {

    public final String username;

    public ClientUsernameRequest(String username) {
        this.username = username;
    }

}
