package logs;

public interface Logger {

    void log(String message, LogType type, boolean newLine);
    void log(String message, LogType type);
    void log(String message);

    void close();
}