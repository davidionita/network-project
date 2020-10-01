package logs;

public enum LogType {

    SUCCESS("SUCCESS >"),
    INFO("Info >"),
    ERROR("Error >"),
    PROMPT("Prompt >"),
    INPUT("User Input >"),
    CHAT("Chat >"),
    PRIVATE_CHAT("PRIVATE Chat >"),
    WELCOME("Welcome >");

    public final String prefix;

    LogType(String prefix) {
        this.prefix = prefix;
    }

    public String format(String message) {
        return this.prefix + " " + message;
    }
}
