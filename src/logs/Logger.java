package logs;

public interface Logger {

    void log(String message, boolean isError);
    void log(String message);

}
