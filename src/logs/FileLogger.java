package logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Logger {

    private FileWriter fileWriter;

    public FileLogger(String file_name) {
        String file_path = String.format("/logs/%s.txt", file_name);
        File logFile = new File(file_path);

        try {
            if(!logFile.exists())
                logFile.createNewFile();

            fileWriter = new FileWriter(logFile);
        } catch (IOException e) {
            System.out.println("> Error: Could not create new file for FileLogger.");
            System.out.println("> Error: " + e.getCause());
        }
    }

    @Override
    public void log(String message, boolean isError) {
        String formatted_message = isError ? String.format("Error > %s", message) : String.format("Info > %s", message);
        System.out.println(formatted_message);

        try {
            if(fileWriter != null)
                fileWriter.write(formatted_message);
        } catch (IOException e) {
            System.out.println("> Error: Could not write to log file for FileLogger.");
            System.out.println("> Error: " + e.getCause());
        }
    }

    @Override
    public void log(String message) {
        this.log(message, false);
    }
}
