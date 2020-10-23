package packets.client;

import packets.Packet;

import java.util.List;

public class ClientMessage implements Packet {

    private final String message;

    public final boolean isPrivate;
    private List<String> recipients;

    public ClientMessage(String message) {
        this.message = message;
        this.isPrivate = false;
    }

    public ClientMessage(String message, List<String> recipients) {
        this.message = message;
        this.isPrivate = true;
        this.recipients = recipients;
    }

    public List<String> getRecipients() {
        return recipients;
    }
}
