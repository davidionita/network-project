package server;

import logs.FileLogger;
import logs.Logger;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 54321;
    private static final int THREAD_POOL_SIZE = 100;

    private static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();
    private static final Logger logger = new FileLogger();

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.log("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            logger.log("Local Port: " + serverSocket.getLocalPort());

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    String connectionMessage = String.format("Connected to %s:%d on local port %d.", socket.getInetAddress(), socket.getPort(), socket.getLocalPort());
                    logger.log(connectionMessage);

                    ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());

                    //handle client business in another thread
                    ClientConnectionData client = new ClientConnectionData(socket, socketIn, socketOut, socket.getInetAddress().getHostName());
                    pool.execute(new ClientConnectionHandler(clientList, client, logger));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

}
