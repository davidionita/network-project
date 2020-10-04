package server;

import packets.Packet;
import packets.PacketType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ClientConnectionHandler implements Runnable {

    // Maintain data about the client serviced by this thread
    private List<ClientConnectionData> clientList;
    private ClientConnectionData client;

    public ClientConnectionHandler(List<ClientConnectionData> clientList, ClientConnectionData client) {
        this.clientList = clientList;
        this.client = client;
    }

    // sending packets to clients
    private void broadcastPacket(Packet packet) {
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
                    break;
                }
            }
        }
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
            while(!isUsernameAvailable(username)) {
                clientOut.println(new Packet(PacketType.SERVER_USERNAME_INVALID));
                username = new Packet(clientIn.readLine()).info;
            }
            clientOut.println(new Packet(PacketType.SERVER_USERNAME_VALID));
            client.setUsername(username);
            synchronized (clientList) {
                clientList.add(client);
            }

            broadcastPacket(new Packet(PacketType.SERVER_NEW_JOIN, client.getUsername()));

            // 2. setup packet listening / routing
            String clientInput;

            while((clientInput = clientIn.readLine()) != null) {
                Packet receivedPacket = new Packet(clientInput);

                // 3. filter different types of packets here
                // TODO: Private message packets, list packets
                if (receivedPacket.type == PacketType.CLIENT_MESSAGE) {
                    String message = receivedPacket.info;
                    String packetInfo = String.format("[%s]%s", client.getUsername(), message);

                    broadcastPacket(new Packet(PacketType.SERVER_ROUTED_MESSAGE, packetInfo));
                }
            }
        } catch(IOException e) {
        } finally {
            // Remove client from clientList, notify all, disconnect client
            synchronized (clientList) {
                clientList.remove(client);
            }
            System.out.println(client.getName() + " has left.");

            try {
                client.getSocket().close();
            } catch (IOException e) {

            }
        }
    }

}
