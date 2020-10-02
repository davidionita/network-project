package client;

import logs.LogType;
import logs.Logger;
import packets.Packet;
import packets.PacketType;

import java.io.BufferedReader;

public class ServerConnectionHandler implements Runnable {

    private BufferedReader socketIn;
    private Logger logger;

    public ServerConnectionHandler(BufferedReader socketIn, Logger logger) {
        this.socketIn = socketIn;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            String inputMessage;

            while ((inputMessage = socketIn.readLine()) != null) {
                logger.log(inputMessage, LogType.PACKET);
                Packet receivedPacket = new Packet(inputMessage);

                if(receivedPacket.type == PacketType.CLIENT_MESSAGE) {
                    System.out.println(receivedPacket.info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Client Listener exiting");
        }
    }
}
