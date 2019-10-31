package recieve;

import util.ResultHandler;
import util.ServerHolder;

import java.net.Socket;

public class ServerHandler implements ResultHandler {

    ConnectionHandler con;

    public ServerHandler(ServerHolder server){
        con = new ConnectionHandler(server, this);
    }

    public ServerHandler(int port){
        ServerHolder server = new ServerHolder();
        server.setPort(port);

        con = new ConnectionHandler(server, this);
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



    @Override
    public void messageReceived(String message) {
        System.out.println("Message from client: " + message);
    }

    @Override
    public void socketReceived(Socket socket) {
        System.out.println("Message from client from: " + socket.toString());
    }
}
