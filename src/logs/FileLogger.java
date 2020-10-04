package logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger implements Logger {

    private FileWriter fileWriter;

    public FileLogger() {
        String file_name = new SimpleDateFormat("yyyy-dd-M--HH-mm-ss").format(new Date());
        createFile(file_name);
    }

    private void createFile(String file_name) {
        String file_path = String.format("logs/%s.txt", file_name);
        File logFile = new File(file_path);

        try {
            // create parent folder if not exists
            if(!logFile.getParentFile().exists())
                logFile.getParentFile().mkdirs();

            if(!logFile.exists())
                logFile.createNewFile();

            fileWriter = new FileWriter(logFile);
        } catch (IOException e) {
            System.out.println("Could not create new file for FileLogger.");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void log(String message, LogType type, boolean logToConsole) {
        // format and print message to console
        if(type != LogType.USER_INPUT && type != LogType.PACKET && logToConsole) {
            System.out.println(message);
        }

        try {
            if(fileWriter != null) {
                fileWriter.write(type.format(message) + "\n");
                fileWriter.flush();
            }
        } catch (IOException e) {
            System.out.println(LogType.ERROR.format("Could not write to log file for FileLogger > " + e.getMessage()));
        }
    }

    @Override
    public void log(String message, LogType type) {
        log(message, type, true);
    }

    @Override
    public void log(String message) {
        this.log(message, LogType.INFO);
    }

    @Override
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(LogType.ERROR.format("Could not close file writer for FileLogger > " + e.getMessage()));
        }
    }
}
