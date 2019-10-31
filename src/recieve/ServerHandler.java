package recieve;

import util.ResultHandler;
import util.ServerHolder;

import java.net.Socket;

public class ServerHandler {

    private ConnectionHandler con;

    public ServerHandler(ServerHolder server, ResultHandler listener){
        con = new ConnectionHandler(server, listener);
    }

    public ServerHandler(int port, ResultHandler listener){
        ServerHolder server = new ServerHolder();
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

    public void sendMessage(String message, Socket socket){
        con.sendMessage(message, socket);
    }
}
