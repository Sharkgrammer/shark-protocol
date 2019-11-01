package util;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketHolder {

    private Socket client = null;
    private ServerSocket server = null;
    private UserHolder user = new UserHolder();

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

    public String getUserName() {
        return user.getName();
    }

    public byte[] getUserID(){
        return user.getUserID();
    }

    public void setUserName(String name) {
        user.setName(name);
    }

    public void setUserID(byte[] ID){
        user.setUserID(ID);
    }
}
