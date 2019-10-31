package send;

import util.ResultHandler;
import util.ServerHolder;

import java.net.Socket;

public class MessageHandler {

    private ClientHandler con;

    public MessageHandler(ServerHolder server, ResultHandler listener){
        con = new ClientHandler(server, listener);
    }

    public MessageHandler(String IP, int Port, ResultHandler listener){
        ServerHolder server = new ServerHolder();
        server.setPort(Port);
        server.setIP(IP);

        con = new ClientHandler(server, listener);
    }

    public void start(){
        con.startClient();
    }

    public void stop(){
        con.stopClient();
    }

    public void send(String message){
        con.sendMessage(message);
    }

}
