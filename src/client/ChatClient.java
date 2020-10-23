package client;

import client.commands.ClientCommand;
import client.commands.ClientCommandManager;
import logs.FileLogger;
import logs.LogType;
import logs.Logger;
import packets.Packet;
import packets.client.ClientUsernameRequestPacket;
import packets.server.ServerUsernameValidPacket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ChatClient {

    private static Socket socket;
    private static ObjectInputStream socketIn;
    private static ObjectOutputStream socketOut;

    private static Logger logger = new FileLogger();

    // exit string is one step above commands
    private static String EXIT_STRING = "/quit";
    private static String PRIVATE_MESSAGE_PREFIX = "@";
    private static String COMMAND_PREFIX = "/";

    public static final boolean DEBUG_MODE = false;

    // only allow alpha numeric username
    private static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9]*$");
    }

    private static String getUsername(Scanner userInput) {
        String username = userInput.nextLine();
        logger.log(username, LogType.USER_INPUT);

        while(!isValidUsername(username)) {
            logger.log("Username's must be alphanumeric.", LogType.ERROR);
            username = userInput.nextLine();
            logger.log(username, LogType.USER_INPUT);
        }
        return username;
    }

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);

        /*
         * Connect to server
         */

        logger.log("What's the server IP? ", LogType.PROMPT);
        String serverIp = userInput.nextLine();
        logger.log(serverIp, LogType.USER_INPUT);

        logger.log("What's the server port? ", LogType.PROMPT);
        int port = userInput.nextInt();
        logger.log(String.valueOf(port), LogType.USER_INPUT);

        userInput.nextLine();

        socket = new Socket(serverIp, port);
        socketIn = new ObjectInputStream(socket.getInputStream());
        socketOut = new ObjectOutputStream(socket.getOutputStream());

        /*
         * Send a valid username
         */

        logger.log("Reached server!", LogType.CONNECTED);
        logger.log("Please enter a username: ", LogType.PROMPT);

        while(true) {
            String username = getUsername(userInput);
            socketOut.writeObject(new ClientUsernameRequestPacket(username));

            Packet response = (Packet) socketIn.readObject();
            logger.log(response.getClass().toString(), LogType.PACKET_RECEIVED, DEBUG_MODE);

            if (response instanceof ServerUsernameValidPacket) {
                logger.log("Now connected as " + username + "!", LogType.CONNECTED);
                break;
            } else {
                logger.log("Username already taken. Please enter another username. ", LogType.ERROR);
            }
        }

        /*
         * Handle server messages on another thread
         */

        ServerConnectionHandler serverHandler = new ServerConnectionHandler(socketIn, logger);
        Thread t = new Thread(serverHandler);
        t.start();

        /*
         * Handle commands on main thread
         */

        ClientCommandManager commandManager = new ClientCommandManager(logger, new ServerConnectionData(socketIn, socketOut));
        String input = userInput.nextLine();

        while(!input.startsWith(EXIT_STRING)) {
            logger.log(input, LogType.USER_INPUT);

            if (input.startsWith(COMMAND_PREFIX)) {
                String[] commandParts = input.substring(1).split("\\s+");
                String prefix = commandParts[0];
                List<String> cArgs = commandParts.length > 1 ? Arrays.asList(commandParts).subList(1, commandParts.length) : new ArrayList<>();

                ClientCommand command = commandManager.getCommand(prefix);

                if(command != null) {
                     command.execute(cArgs);
                } else {
                    logger.log("Command not found!", LogType.COMMAND_NOT_FOUND);
                }
            } else if (input.startsWith(PRIVATE_MESSAGE_PREFIX)) {
                // TODO: Better client-side error checking for PMs - must include message, must include username, should not be the same username as current client ...
                String username = input.substring(1, input.indexOf(" "));
                String message = input.substring(input.indexOf(" ")+1);
                Packet packet = new Packet(PacketType.CLIENT_PRIVATE_MESSAGE, username+"^"+message);
                socketOut.println(packet);
            } else {
                Packet packet = new Packet(PacketType.CLIENT_MESSAGE, input);
                socketOut.println(packet);
            }
            input = userInput.nextLine();
        }


        /*
         * Close all streams
         */

        logger.log(EXIT_STRING);
        socketOut.close();
        userInput.close();
        socketIn.close();
        socket.close();
        logger.close();
    }
}
