package client.commands;

import client.ServerConnectionData;
import logs.LogType;
import logs.Logger;
import packets.client.ClientMessagePacket;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrivateMessageCommand implements ClientCommand {

    private Logger logger;
    private ServerConnectionData serverData;

    PrivateMessageCommand(Logger logger, ServerConnectionData serverData) {
        this.logger = logger;
        this.serverData = serverData;
    }

    @Override
    public String getPrefix() {
        return "@";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public void execute(List<String> inputArray) {
        Set<String> recipients = new HashSet<>();
        String message = null;

        for(int i = 0; i < inputArray.size(); i++) {
            String input = inputArray.get(i);

            if(input.startsWith(getPrefix()) && input.length() > getPrefix().length())
                recipients.add(input.substring(1).toLowerCase());
            else {
                message = String.join(" ", inputArray.subList(i, inputArray.size()));
                break;
            }
        }

        if(message == null) {
            logger.log("Invalid PM: No message provided for recipients.");
            return;
        }

        ClientMessagePacket messagePacket = new ClientMessagePacket(message, recipients);

        try {
            serverData.socketOut.writeObject(messagePacket);
        } catch(IOException e) {
            logger.log(String.format("Error thrown when attempting to send private message: %s to %s", message, recipients.toString()), LogType.ERROR);
        }
    }

}