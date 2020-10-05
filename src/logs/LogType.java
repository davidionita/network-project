package logs;

public enum LogType {

    CONNECTED("Connected >"),
    INFO("Info >"),
    ERROR("Error >"),
    PROMPT("Prompt >"),
    PACKET("Received Packet >"),
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
