package server;

import logs.LogType;
import logs.Logger;
import packets.client.ClientListPacket;
import packets.server.*;
import packets.Packet;
import packets.client.ClientMessagePacket;
import packets.client.ClientUsernameRequestPacket;

import java.io.IOException;
import java.util.*;

public class ClientConnectionHandler implements Runnable {

    // Maintain data about the client serviced by this thread
    private List<ClientConnectionData> clientList;
    private ClientConnectionData client;
    private Logger logger;

    ClientConnectionHandler(List<ClientConnectionData> clientList, ClientConnectionData client, Logger logger) {
        this.clientList = clientList;
        this.client = client;
        this.logger = logger;
    }

    // sending packets to clients
    private void broadcastPacket(Packet packet) {
        logger.log("-- BROADCAST -- Sent packet '" + packet.toString() + "' to all clients.", LogType.PACKET_SENT);
        synchronized (clientList) {
            for(ClientConnectionData client : clientList) {
                try {
                    client.out.writeObject(packet);
                } catch(IOException e) {
                    logger.log(String.format("Could not send packet %s to %s<%s>!", packet.toString(), client.getUsername(), client.name), LogType.ERROR);
                }
            }
        }
    }
    private Set<String> sendPacket(Packet packet, Set<String> usernames) {
        Set<String> received = new HashSet<>();

        synchronized (clientList) {
            for(ClientConnectionData client : clientList) {
                if(usernames.contains(client.getUsername().toLowerCase())) {
                    received.add(client.getUsername());

                    try {
                        client.out.writeObject(packet);
                        logger.log(String.format("Sent packet '%s' to client %s<%s>.", packet.toString(), client.getUsername(), client.name));
                    } catch(Exception e) {
                        e.printStackTrace();
                        logger.log(String.format("Could not send packet %s to %s<%s>!", packet.toString(), client.getUsername(), client.name), LogType.ERROR);
                    }
                }
            }
            return received;
        }
    }

    // other packet methods
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
    private List<String> getConnectedUsers() {
        List<String> connected = new ArrayList<>();
        synchronized (clientList) {
            for (ClientConnectionData connectedClient : clientList) {
                connected.add(connectedClient.getUsername());
            }
        }
        return connected;
    }

    @Override
    public void run() {
        try {
            Packet input;
            boolean isConnecting;

            // handle packets all under one loop
            while((input = (Packet) client.in.readObject()) != null) {
                isConnecting = client.getUsername() == null;

                if(input instanceof ClientUsernameRequestPacket) {
                    String newUsername = ((ClientUsernameRequestPacket) input).username;

                    if(isConnecting) {
                        logger.log(String.format("Client (%s) attempting to connect with username %s.", client.name, newUsername), LogType.PACKET_RECEIVED);
                    } else {
                        logger.log(String.format("Client (%s<%s>) attempting to change username to %s.", client.name, client.getUsername(), newUsername), LogType.PACKET_RECEIVED);
                    }

                    if(newUsername != null && isUsernameAvailable(newUsername)) {
                        String oldUsername = client.getUsername();
                        client.setUsername(newUsername);
                        client.out.writeObject(new ServerUsernameValidPacket(newUsername));
                        logger.log(String.format("Client (%s) successfully set username to %s! Valid username packet sent.", client.name, client.getUsername()), LogType.PACKET_SENT);

                        synchronized (clientList) {
                            clientList.add(client);
                        }
                        List<String> connectedUsers = getConnectedUsers();

                        if(isConnecting) {
                            broadcastPacket(new ServerJoinPacket(newUsername, connectedUsers));
                        } else {
                            broadcastPacket(new ServerUsernameChangePacket(oldUsername, newUsername, connectedUsers));
                        }
                    } else {
                        client.out.writeObject(new ServerUsernameInvalidPacket());
                    }
                } else if(input instanceof ClientMessagePacket && !isConnecting) {
                    ClientMessagePacket messagePacket = (ClientMessagePacket) input;
                    ServerRoutedMessagePacket routedMessage;

                    if(messagePacket.isPrivate) {
                        if(messagePacket.getRecipients().size() == 0) {
                            client.out.writeObject(new ServerErrorPacket("You must specify recipients in your private message!"));
                        } else if(messagePacket.getRecipients().contains(client.getUsername())) {
                            client.out.writeObject(new ServerErrorPacket("You cannot send a message to yourself!"));
                        } else {
                            routedMessage = new ServerRoutedMessagePacket(client.getUsername(), messagePacket.message, true, null);
                            Set<String> receivedRecipients = sendPacket(routedMessage, messagePacket.getRecipients());

                            client.out.writeObject(new ServerRoutedMessagePacket(client.getUsername(), messagePacket.message, true, receivedRecipients));
                        }
                    } else {
                        routedMessage = new ServerRoutedMessagePacket(client.getUsername(), messagePacket.message, false, null);
                        broadcastPacket(routedMessage);
                    }
                } else if(input instanceof ClientListPacket) {
                    List<String> connectedUsers = getConnectedUsers();
                    client.out.writeObject(new ServerListPacket(connectedUsers));
                } else {
                    client.out.writeObject(new ServerErrorPacket("Unknown packet received."));
                }
            }
        } catch(ClassNotFoundException e) {
            logger.log(String.format("%s<%s> sent object not as packet. Invalid Protocol - ClassNotFoundException", client.getUsername(), client.name), LogType.ERROR);
        } catch(IOException e) {
        } finally {
            // Remove client from clientList, notify all & disconnect client
            synchronized (clientList) {
                clientList.remove(client);
            }
            logger.log(String.format("%s<%s> has disconnected.", client.getUsername(), client.name), LogType.DISCONNECTED, true);
            if(client.getUsername() != null)
                broadcastPacket(new ServerDisconnectPacket(client.getUsername(), getConnectedUsers()));

            try {
                client.socket.close();
            } catch (IOException e) { }
        }
    }

}
