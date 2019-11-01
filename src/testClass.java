import recieve.ServerHandler;
import send.MessageHandler;
import util.ResultHandler;
import util.DataHolder;

import java.net.Socket;
import java.util.IdentityHashMap;

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder server = new DataHolder();
        server.setPort(6000);
        server.setIP("35.234.148.116");

        new Server().run(server);
        //new Client().run(server);

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
    public void messageReceived(String messageData, Socket socket, DataHolder data) {
        String[] messageArr = messageData.split("&space&");
        String IDFrom = messageArr[0];
        String message = messageArr[1];
        String IDTo = messageArr[2];

        data.setUserID(IDFrom.getBytes(), socket);

        System.out.println("Message from client " + IDFrom + " '" + message + "' sending to client "  + IDTo);

        server.sendMessage(message, IDTo.getBytes());
    }


}

class Client implements ResultHandler{

    MessageHandler client;

    public void run(DataHolder s){

        String ID = "shark1";
        String ToID = "shark2";

        client = new MessageHandler(s, this, ID.getBytes());

        client.start();

        client.send("I am a shark", ToID.getBytes());

        //client.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket, DataHolder data) {
        System.out.println("Message from server: " + message);
        //System.out.println("Message from server: I am: " + socket.toString());



    }
}