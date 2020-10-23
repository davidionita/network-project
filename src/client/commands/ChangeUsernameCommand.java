package client.commands;

import logs.LogType;
import logs.Logger;
import packets.client.ClientUsernameRequestPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class ChangeUsernameCommand implements ClientCommand {

    private final Logger logger;
    private final ObjectOutputStream socketOut;

    public ChangeUsernameCommand(Logger logger, ObjectOutputStream socketOut) {
        this.logger = logger;
        this.socketOut = socketOut;
    }

    @Override
    public String getPrefix() {
        return "/changeusername";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public void execute(List<String> input) {
        if(input.size() < 2) {
            logger.log("No username provided.", LogType.ERROR);
            return;
        }

        String newUsername = input.get(1);
        try {
            socketOut.writeObject(new ClientUsernameRequestPacket(newUsername));
        } catch (IOException e) {
            logger.log("IO Connection Error: Could not request username change.", LogType.ERROR);
        }
    }
}
