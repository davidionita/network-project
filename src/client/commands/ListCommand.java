package client.commands;

import logs.LogType;
import logs.Logger;
import packets.client.ClientListPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ListCommand implements ClientCommand {

    private final Logger logger;
    private final ObjectOutputStream socketOut;

    public ListCommand(Logger logger, ObjectOutputStream socketOut) {
        this.logger = logger;
        this.socketOut = socketOut;
    }

    @Override
    public String getPrefix() {
        return "/whoishere";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("/list");
        return aliases;
    }

    @Override
    public void execute(List<String> input) {
        try {
            socketOut.writeObject(new ClientListPacket());
        } catch(IOException e) {
            logger.log("IO Connection Error: Could not request ClientList.", LogType.ERROR);
        }
    }
}
