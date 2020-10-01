package protocol;

public class Packet {

    public final ProtocolType type;
    public final String info;

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
        return type.prefix + info;
    }
}
