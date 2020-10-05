package client.commands;

import logs.Logger;

import java.util.List;

public interface ClientCommand {

    String getPrefix();
    List<String> getAliases();

    void execute(List<String> args);

}
