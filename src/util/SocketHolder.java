package util;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketHolder {

    private Socket client = null;
    private ServerSocket server = null;
    private UserHolder user;

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

    public UserHolder getUser() {
        return user;
    }

    public void setUser(UserHolder user) {
        this.user = user;
    }

    public void setUserID(byte[] ID){
        if (user == null){
            user = new UserHolder(ID, null, null);
        }else{
            user.setUserID(ID);
        }
    }

    public byte[] getUserID(){
        return user.getUserID();
    }
}
