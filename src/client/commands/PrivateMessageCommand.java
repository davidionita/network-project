package client.commands;

import client.ServerConnectionData;
import logs.Logger;

import java.util.List;

public class PrivateMessageCommand implements ClientCommand {

    private Logger logger;
    private ServerConnectionData serverData;

    public PrivateMessageCommand(Logger logger, ServerConnectionData serverData) {
        this.logger = logger;
        this.serverData = serverData;
    }

    @Override
    public void execute(List<String> args) {
        logger.log("Hi there!");
    }

    @Override
    public String getPrefix() {
        return "pm";
    }
}
