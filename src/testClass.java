import recieve.ServerHandler;
import send.MessageHandler;
import util.ResultHandler;
import util.DataHolder;

import javax.xml.crypto.Data;
import java.net.Socket;

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder server = new DataHolder();
        server.setPort(6000);
        server.setIP("35.234.148.116");

        //new Server().run(server);
        new Client().run(server);

        System.out.println("shark test end");
    }

}

class Server implements ResultHandler{

    ServerHandler server;

    public void run(DataHolder s){

        server = new ServerHandler(s, this);
        server.start();

    }

    @Override
    public void messageReceived(String message, DataHolder data) {

        System.out.println("Message from client: " + message);
        server.sendMessage("Received: " + message, data.getClientSocket());

    }


}

class Client implements ResultHandler{

    MessageHandler client;

    public void run(DataHolder s){

        String ID = "";

        client = new MessageHandler(s, this, ID.getBytes());

        client.start();

        client.send("I am a shark");

        //client.stop();

    }

    @Override
    public void messageReceived(String message, DataHolder data) {
        System.out.println("Message from server: " + message);
        //System.out.println("Message from server: I am: " + socket.toString());
    }
}