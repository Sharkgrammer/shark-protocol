package send;

import util.ResultHandler;
import util.ServerHolder;

import java.net.Socket;

public class MessageHandler implements ResultHandler {

    private ClientHandler con;

    public MessageHandler(ServerHolder server){
        con = new ClientHandler(server, this);
    }

    public MessageHandler(String IP, int Port){
        ServerHolder server = new ServerHolder();
        server.setPort(Port);
        server.setIP(IP);

        con = new ClientHandler(server, this);
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

    @Override
    public void messageReceived(String message) {
        System.out.println("Message from server: " + message);
    }

    @Override
    public void socketReceived(Socket socket) {
        System.out.println("Message from server, you are: " + socket.toString());
    }
}
