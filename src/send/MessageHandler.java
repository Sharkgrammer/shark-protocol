package send;

import util.ResultHandler;
import util.DataHolder;
import util.UserHolder;

public class MessageHandler {

    private ClientHandler con;

    public MessageHandler(DataHolder server, ResultHandler listener, UserHolder user){
        server.setCurrentUser(user);

        con = new ClientHandler(server, listener);
    }

    public void auth(){
        con.sendAuthMessage();
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
