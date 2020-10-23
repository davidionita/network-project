package server;

import java.io.*;
import java.net.Socket;

public class ClientConnectionData {

    public final Socket socket;
    public final ObjectInputStream in;
    public final ObjectOutputStream out;
    public final String name;

    private String username;

    ClientConnectionData(Socket socket, ObjectInputStream in, ObjectOutputStream out, String name) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
