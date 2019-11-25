import crypto.CryptManager;
import recieve.ServerHandler;
import send.MessageHandler;
import util.ResultHandler;
import util.DataHolder;
import util.UserHolder;

import java.net.Socket;
import java.util.Arrays;
import java.util.IdentityHashMap;

//REF based on https://guides.codepath.com/android/Sending-and-Receiving-Data-with-Sockets#tcpclient for socket code
//REF based on https://stackoverflow.com/a/40100207/11480852 as well

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder server = new DataHolder();
        server.setPort(6000);
        server.setIP("35.235.49.238");

        //new Server().run(server);
        new Client().run(server);

        //new CryptManager().run();

        System.out.println("shark test end");
    }

}

class Server implements ResultHandler{

    private ServerHandler server ;

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

    void run(DataHolder s){

        String ID = "d3";
        UserHolder user = new UserHolder(ID.getBytes(), null, null);
        String ToID = "d4";

        System.out.println("I am " + new String(user.getUserID()));

        MessageHandler client = new MessageHandler(s, this, user);

        client.start();

        client.auth();

        client.send("hey how you", ToID.getBytes());

        //client.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket, DataHolder data) {
        System.out.println("Message from server: " + message);
        //System.out.println("Message from server: I am: " + socket.toString());
    }
}