package client.commands;

import java.util.List;

public interface ClientCommand {

    String getPrefix();
    List<String> getAliases();

    void execute(List<String> input);

}
