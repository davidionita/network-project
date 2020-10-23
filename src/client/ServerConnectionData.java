package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerConnectionData {

    public final ObjectInputStream socketIn;
    public final ObjectOutputStream socketOut;

    ServerConnectionData(ObjectInputStream socketIn, ObjectOutputStream socketOut) {
        this.socketIn = socketIn;
        this.socketOut = socketOut;
    }
}
