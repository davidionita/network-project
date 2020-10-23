package packets;

public class ErrorPacket implements Packet {

    public final String message;

    public ErrorPacket(String message) {
        this.message = message;
    }

}
