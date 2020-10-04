package client.commands;

import logs.Logger;

import java.util.List;

public interface ClientCommand {

    String getPrefix();
    void execute(List<String> args);

}
