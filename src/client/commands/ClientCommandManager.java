package client.commands;

import client.ServerConnectionData;
import logs.Logger;

import java.util.HashMap;
import java.util.Map;

public class ClientCommandManager {

    private Map<String, ClientCommand> commands = new HashMap<>();

    public ClientCommandManager(Logger logger, ServerConnectionData serverData) {
        addCommand(new PrivateMessageCommand(logger, serverData));
        addCommand(new ListCommand(logger, serverData.socketOut));
        addCommand(new ChangeUsernameCommand(logger, serverData.socketOut));
    }

    private void addCommand(ClientCommand command) {
        this.commands.put(command.getPrefix(), command);

        if(command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                this.commands.put(alias, command);
            }
        }
    }

    public ClientCommand getCommand(String firstArgument) {
        firstArgument = firstArgument.toLowerCase();
        // private message is a special case
        if(firstArgument.startsWith("@")) {
            return this.commands.get("@");
        }
        return commands.get(firstArgument);
    }

}
