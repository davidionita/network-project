package server;

import client.ClientConnectionData;
import client.ClientConnectionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    public static final int PORT = 54321;
    private static final int THREAD_POOL_SIZE = 100;
    private static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started:.");
            System.out.println("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Local Port: " + serverSocket.getLocalPort());

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.printf("Connected to %s:%d on local port %d\n",
                            socket.getInetAddress(), socket.getPort(), socket.getLocalPort());

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    String name = socket.getInetAddress().getHostName();

                    ClientConnectionData client = new ClientConnectionData(socket, in, out, name);
                    clientList.add(client);
                    System.out.println("Added client " + name);

                    //handle client business in another thread
                    pool.execute(new ClientConnectionHandler(clientList, client));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }


    }

}
