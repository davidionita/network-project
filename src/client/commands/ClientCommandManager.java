package client.commands;

import client.ServerConnectionData;
import logs.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClientCommandManager {

    private Map<String, ClientCommand> commands = new HashMap<>();

    public ClientCommandManager(Logger logger, ServerConnectionData serverData) {
        addCommand(new PrivateMessageCommand(logger, serverData));
    }

    private void addCommand(ClientCommand command) {
        this.commands.put(command.getPrefix(), command);
    }

    public ClientCommand getCommand(String prefix) {
        return commands.get(prefix);
    }

}
