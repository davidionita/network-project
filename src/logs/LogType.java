package logs;

public enum LogType {

    CONNECTED("Connected >"),
    DISCONNECTED("Disconnected >"),
    INFO("Info >"),
    ERROR("Error >"),
    PROMPT("Prompt >"),
    PACKET_RECEIVED("Received Packet >"),
    PACKET_SENT("Sent Packet >"),
    PACKET_ERROR("Packet Error >"),
    CHAT("Chat >"),
    USER_INPUT("User Input >"),
    COMMAND_NOT_FOUND("Invalid Command >");

    public final String prefix;

    LogType(String prefix) {
        this.prefix = prefix;
    }

    public String format(String message) {
        return this.prefix + " " + message;
    }
}
