package protocol;

public class Packet {

    public final ProtocolType type;
    public final String info;

    // interprets packet from message
    public Packet(String message) {
        for(ProtocolType type : ProtocolType.values()) {
            if(message.startsWith(type.prefix)) {
                this.type = type;
                this.info = message.substring(type.prefix.length());
                System.out.println(message);
                return;
            }
        }
        this.type = ProtocolType.UNKNOWN;
        this.info = "";
    }

    public Packet(ProtocolType type) {
        this.type = type;
        this.info = "";
    }

    public Packet(ProtocolType type, String info) {
        this.type = type;
        this.info = info;
    }

    @Override
    public String toString() {
        return type.prefix + this.info;
    }
}
