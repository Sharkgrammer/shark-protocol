package send;

import util.ResultHandler;
import util.DataHolder;

public class MessageHandler {

    private ClientHandler con;

    public MessageHandler(DataHolder server, ResultHandler listener){
        con = new ClientHandler(server, listener);
    }

    public MessageHandler(String IP, int Port, ResultHandler listener){
        DataHolder server = new DataHolder();
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
