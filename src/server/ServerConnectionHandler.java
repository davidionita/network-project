package server;

import java.io.BufferedReader;

public class ServerConnectionHandler implements Runnable {

    private BufferedReader socketIn;

    public ServerConnectionHandler(BufferedReader socketIn) {
        this.socketIn = socketIn;
    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while ((incoming = socketIn.readLine()) != null) {
                // handle different headers (WELCOME, CHAT, EXIT
                System.out.println(incoming);
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener " + ex);
        } finally {
            System.out.println("Client Listener exiting");
        }
    }
}
