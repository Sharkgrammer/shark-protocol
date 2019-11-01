package send;

import util.ResultHandler;
import util.DataHolder;

public class MessageHandler {

    private ClientHandler con;

    public MessageHandler(DataHolder server, ResultHandler listener, byte[] userID){
        con = new ClientHandler(server, listener, userID);
    }

    public MessageHandler(String IP, int Port, ResultHandler listener, byte[] userID){
        DataHolder server = new DataHolder();
        server.setPort(Port);
        server.setIP(IP);

        con = new ClientHandler(server, listener, userID);
    }

    public void start(){
        con.startClient();
    }

    public void stop(){
        con.stopClient();
    }

    public void send(String message, byte[] toID){
        con.sendMessage(message, toID);
    }

}
