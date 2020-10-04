package client;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ServerConnectionData {

    public final BufferedReader socketIn;
    public final PrintWriter socketOut;

    public ServerConnectionData(BufferedReader socketIn, PrintWriter socketOut) {
        this.socketIn = socketIn;
        this.socketOut = socketOut;
    }
}
