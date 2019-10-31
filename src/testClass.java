import recieve.ServerHandler;
import send.MessageHandler;
import util.ResultHandler;
import util.ServerHolder;

import java.net.Socket;

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        ServerHolder server = new ServerHolder();
        server.setPort(6000);
        server.setIP("35.234.148.116");

        //new Server().run(server);
        new Client().run(server);

        System.out.println("shark test end");
    }

}

class Server implements ResultHandler{

    ServerHandler server;

    public void run(ServerHolder s){

        server = new ServerHandler(s, this);
        server.start();
        server.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket) {
        System.out.println("Message from client: " + message);

        server.sendMessage("Received, thank you for: " + message, socket);
    }


}

class Client implements ResultHandler{

    MessageHandler client;

    public void run(ServerHolder s){

        client = new MessageHandler(s, this);

        client.start();

        client.send("Hello");
        client.send("How are you");
        client.send("You cool?");
        client.send("Fu");
        client.send("I'm a shark");

        //client.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket) {
        System.out.println("Message from server: " + message);
        //System.out.println("Message from server: I am: " + socket.toString());
    }
}