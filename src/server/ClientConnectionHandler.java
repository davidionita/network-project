package server;

import logs.LogType;
import logs.Logger;
import packets.Packet;
import packets.PacketType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class ClientConnectionHandler implements Runnable {

    // Maintain data about the client serviced by this thread
    private List<ClientConnectionData> clientList;
    private ClientConnectionData client;
    private Logger logger;

    public ClientConnectionHandler(List<ClientConnectionData> clientList, ClientConnectionData client, Logger logger) {
        this.clientList = clientList;
        this.client = client;
        this.logger = logger;
    }

    // sending packets to clients
    private void broadcastPacket(Packet packet) {
        logger.log("-- BROADCAST -- Sent packet '" + packet.toString() + "' to all clients.", LogType.PACKET_SENT);
        synchronized (clientList) {
            for(ClientConnectionData client : clientList) {
                client.getOut().println(packet);
            }
        }
    }
    private void sendPacket(Packet packet, String username) {
        synchronized (clientList) {
            for(ClientConnectionData client : clientList) {
                if(client.getUsername().equalsIgnoreCase(username)) {
                    client.getOut().println(packet);
                    logger.log(String.format("Sent packet '%s' to client @%s (%s).", packet.toString(), username, client.getName()));
                    break;
                }
            }
        }
        logger.log(String.format("Could not send packet '%s' to client with username %s. Client not connected.", packet.toString(), username));
    }

    private boolean isUsernameAvailable(String username) {
        synchronized (clientList) {
            for(ClientConnectionData client : clientList) {
                if (client.getUsername().equalsIgnoreCase(username)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        try {
            PrintWriter clientOut = client.getOut();
            BufferedReader clientIn = client.getIn();
            String input = clientIn.readLine();

            // 1. get valid username
            String username = new Packet(input).info;
            logger.log(String.format("Client (%s) attempting to connect with username %s.", client.getName(), username), LogType.PACKET_RECEIVED);
            while(!isUsernameAvailable(username)) {
                clientOut.println(new Packet(PacketType.SERVER_USERNAME_INVALID));
                logger.log(String.format("Username, %s, not available for client %s. Sending invalid packet.", username, client.getName()), LogType.PACKET_SENT);

                username = new Packet(clientIn.readLine()).info;
                logger.log(String.format("Received new username, (%s), from client %s.", username, client.getName()), LogType.PACKET_RECEIVED);
            }
            clientOut.println(new Packet(PacketType.SERVER_USERNAME_VALID));
            client.setUsername(username);
            synchronized (clientList) {
                clientList.add(client);
            }

            logger.log(String.format("Client (%s) successfully set username to %s! Valid username packet sent.", client.getName(), client.getUsername()), LogType.PACKET_SENT);
            broadcastPacket(new Packet(PacketType.SERVER_NEW_JOIN, client.getUsername()));

            // 2. setup packet listening / routing
            String clientInput;

            while((clientInput = clientIn.readLine()) != null) {
                Packet receivedPacket = new Packet(clientInput);
                logger.log(String.format("@%s (%s) sent packet: '%s'", client.getUsername(), client.getName(), receivedPacket.toString()), LogType.PACKET_RECEIVED, true);

                // 3. filter different types of packets here
                // TODO: List packets
                if (receivedPacket.type == PacketType.CLIENT_MESSAGE) {
                    String message = receivedPacket.info.replaceAll("\\^", "");
                    String packetInfo = String.format("%s^%s^%s", client.getUsername(), new Date().getTime(), message);

                    broadcastPacket(new Packet(PacketType.SERVER_ROUTED_MESSAGE, packetInfo));
                }
                else if (receivedPacket.type == PacketType.CLIENT_PRIVATE_MESSAGE) {
                    // TODO: Better server-side error checking for PMs - username must be legit or else send back error packet instead of pm packet, ...
                    String[] parts = receivedPacket.info.split("\\^", 2);
                    String privateUsername = parts[0];
                    String message = parts[1].replaceAll("\\^", "");
                    String packetInfo = String.format("%s^%s^%s", client.getUsername(), new Date().getTime(), message);

                    sendPacket(new Packet(PacketType.SERVER_ROUTED_PRIVATE_MESSAGE, packetInfo), privateUsername);
                }
            }
        } catch(IOException e) {
        } finally {
            // Remove client from clientList, notify all, disconnect client
            synchronized (clientList) {
                clientList.remove(client);
            }
            logger.log(String.format("@%s (%s) has disconnected.", client.getUsername(), client.getName()), LogType.DISCONNECTED, true);

            try {
                client.getSocket().close();
            } catch (IOException e) {

            }
        }
    }

}
