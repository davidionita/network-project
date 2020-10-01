package client;

import logs.Logger;
import protocol.Packet;
import protocol.ProtocolType;

import java.io.BufferedReader;

public class ServerConnectionHandler implements Runnable {

    private BufferedReader socketIn;
    private Logger logger;

    public ServerConnectionHandler(BufferedReader socketIn, Logger logger) {
        this.socketIn = socketIn;
    }

    @Override
    public void run() {
        try {
            String inputMessage;

            while ((inputMessage = socketIn.readLine()) != null) {
                Packet receivedPacket = new Packet(inputMessage);

                if(receivedPacket.type == ProtocolType.CLIENT_MESSAGE) {
                    System.out.println(receivedPacket.info);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener " + ex);
        } finally {
            System.out.println("Client Listener exiting");
        }
    }
}
