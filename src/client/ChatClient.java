package client;

import logs.FileLogger;
import logs.LogType;
import logs.Logger;
import server.ServerConnectionHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter socketOut;

    private static Logger logger = new FileLogger();
    private static String EXIT_COMMAND = "/quit";

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);

        logger.log("What's the server IP?", LogType.PROMPT);
        String serverIp = userInput.nextLine();
        logger.log("What's the server port?", LogType.PROMPT);
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(serverIp, port);
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintWriter(socket.getOutputStream(), true);

        // handle socket on different thread
        ServerConnectionHandler handler = new ServerConnectionHandler(socketIn);
        Thread t = new Thread(handler);
        t.start();

        logger.log("Connected to server!", LogType.CONNECTED);
        logger.log("Please enter a username: ", LogType.PROMPT);
        String name = userInput.nextLine().trim();
        socketOut.println(name);

        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith(EXIT_COMMAND)) {
            String msg = String.format("CHAT %s", line);
            socketOut.println(msg);
            line = userInput.nextLine().trim();
        }

        /*
         * Close all streams
         */
        socketOut.println("QUIT");
        socketOut.close();
        userInput.close();
        socketIn.close();
        socket.close();
    }
}
