package util;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketHolder {

    private Socket client = null;
    private ServerSocket server = null;

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }
}
