package recieve;

import util.ResultHandler;
import util.DataHolder;

import java.net.Socket;

public class ServerHandler {

    private ConnectionHandler con;

    public ServerHandler(DataHolder server, ResultHandler listener){
        con = new ConnectionHandler(server, listener);
    }

    public ServerHandler(int port, ResultHandler listener){
        DataHolder server = new DataHolder();
        server.setPort(port);

        con = new ConnectionHandler(server, listener);
    }

    public void start(){
        con.startServer();
    }

    public void stop(){
        con.sendMessage("Server closed", true, 0);

        con.stopServer();
    }

    public void sendMessage(String message, boolean ToAll, int Pos){
        con.sendMessage(message, ToAll, Pos);
    }

    public void sendMessage(String message, byte[] userID){
        con.sendMessage(message, userID);
    }

    public void sendMessage(String message, Socket socket){
        con.sendMessage(message, socket);
    }
}
