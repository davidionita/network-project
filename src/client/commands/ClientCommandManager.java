package client.commands;

import client.ServerConnectionData;
import logs.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClientCommandManager {

    private Map<String, ClientCommand> commands = new HashMap<>();

    public ClientCommandManager(Logger logger, ServerConnectionData serverData) {

    }

    private void addCommand(ClientCommand command) {
        this.commands.put(command.getPrefix(), command);
        for(String alias : command.getAliases()) {
            this.commands.put(alias, command);
        }
    }

    public ClientCommand getCommand(String prefix) {
        return commands.get(prefix);
    }

}
