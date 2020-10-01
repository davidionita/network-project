package protocol;

public enum ProtocolType {

    // sent from server to clients
    SERVER_NEW_JOIN("new_join"),
    SERVER_USERNAME_INVALID("invalid_username"),
    SERVER_USERNAME_VALID("valid_username"),
    SERVER_ROUTED_MESSAGE("server_message"),

    // sent from clients to server
    CLIENT_USERNAME("username"),
    CLIENT_MESSAGE("message"),
    CLIENT_PRIVATE_MESSAGE("private_message"),

    UNKNOWN("");

    public final String prefix;

    ProtocolType(String prefix) {
        this.prefix = "[" + prefix + "]";
    }
}
