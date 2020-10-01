package logs;

public interface Logger {

    void log(String message, LogType logType);
    void log(String message);

    void close();
}