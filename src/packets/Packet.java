package packets;

public class Packet {

    public final PacketType type;
    public final String info;

    // interprets packet from message
    public Packet(String message) {
        for(PacketType type : PacketType.values()) {
            if(message.startsWith(type.prefix)) {
                this.type = type;
                this.info = message.substring(type.prefix.length());
                System.out.println(message);
                return;
            }
        }
        this.type = PacketType.UNKNOWN;
        this.info = "";
    }

    public Packet(PacketType type) {
        this.type = type;
        this.info = "";
    }

    public Packet(PacketType type, String info) {
        this.type = type;
        this.info = info;
    }

    @Override
    public String toString() {
        return type.prefix + this.info;
    }
}
