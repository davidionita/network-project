package server;

import logs.LogType;
import logs.Logger;
import packets.server.*;
import packets.Packet;
import packets.client.ClientMessagePacket;
import packets.client.ClientUsernameRequestPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
                    logger.log(String.format("Could not send packet %s to %s<%s>!", packet.getClass(), client.getUsername(), client.name), LogType.ERROR);
                }
            }
        }
    }
    private void sendPacket(Packet packet, Set<String> usernames) {
        synchronized (clientList) {
            for(ClientConnectionData client : clientList) {
                if(usernames.contains(client.getUsername())) {
                    try {
                        client.out.writeObject(packet);
                        logger.log(String.format("Sent packet '%s' to client %s<%s>.", packet.getClass(), client.getUsername(), client.name));
                    } catch(Exception e) {
                        logger.log(String.format("Could not send packet %s to %s<%s>!", packet.getClass(), client.getUsername(), client.name), LogType.ERROR);
                    }
                    break;
                }
            }
        }
        logger.log(String.format("Could not send packet '%s' to client %s<%s>. Client not connected.", packet.getClass(), client.getUsername(), client.name));
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
                        client.setUsername(newUsername);
                        client.out.writeObject(new ServerUsernameValidPacket(newUsername));
                        logger.log(String.format("Client (%s) successfully set username to %s! Valid username packet sent.", client.name, client.getUsername()), LogType.PACKET_SENT);

                        List<String> connected = new ArrayList<>();
                        synchronized (clientList) {
                            clientList.add(client);

                            if(isConnecting) {
                                for (ClientConnectionData connectedClient : clientList) {
                                    connected.add(connectedClient.getUsername());
                                }
                            }
                        }

                        if(isConnecting)
                            broadcastPacket(new ServerJoinPacket(newUsername, connected));
                    } else {
                        client.out.writeObject(new ServerUsernameInvalidPacket());
                    }
                } else if(input instanceof ClientMessagePacket && !isConnecting) {
                    ClientMessagePacket messagePacket = (ClientMessagePacket) input;
                    ServerRoutedMessagePacket routedMessage;

                    if(messagePacket.isPrivate) {
                        routedMessage = new ServerRoutedMessagePacket(client.getUsername(), messagePacket.message, true, messagePacket.getRecipients());
                        sendPacket(routedMessage, messagePacket.getRecipients());
                    } else {
                        routedMessage = new ServerRoutedMessagePacket(client.getUsername(), messagePacket.message, false, null);
                        broadcastPacket(routedMessage);
                    }

                    client.out.writeObject(routedMessage);
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
                broadcastPacket(new ServerDisconnectPacket(client.getUsername()));

            try {
                client.socket.close();
            } catch (IOException e) { }
        }
    }

}
