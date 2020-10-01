package protocol;

public enum ProtocolType {

    // sent from server to clients
    SERVER_NEW_JOIN("new_join"),
    SERVER_NEW_USERNAME("new_username"),

    // sent from clients to server
    CLIENT_MESSAGE("chat"),
    CLIENT_PRIVATE_MESSAGE("private_message");

    public final String prefix;

    ProtocolType(String prefix) {
        this.prefix = "[" + prefix + "]";
    }
}
