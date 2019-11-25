import crypto.CryptManager;
import recieve.ServerHandler;
import send.MessageHandler;
import util.Base64Util;
import util.ResultHandler;
import util.DataHolder;
import util.UserHolder;

import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.IdentityHashMap;

//REF based on https://guides.codepath.com/android/Sending-and-Receiving-Data-with-Sockets#tcpclient for socket code
//REF based on https://stackoverflow.com/a/40100207/11480852 as well

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder data = new DataHolder();
        data.setPort(6000);
        data.setIP("35.235.49.238");

        //new Server().run(data);
        new Client().run(data);

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

        temp tempkey = new temp();

        String ID = "d1";
        UserHolder user = new UserHolder(ID.getBytes(), tempkey.pukey1, tempkey.prkey1);
        String ToID = "d2";

        System.out.println("I am " + new String(user.getUserID()));

        MessageHandler client = new MessageHandler(s, this, user);

        client.start();

        client.auth();

        client.send("shark", ToID.getBytes());

        //client.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket, DataHolder data) {
        System.out.println("Raw from server: " + message);

        temp tempkey = new temp();

        byte[] base = (new Base64Util()).fromBase64(message);
        System.out.println("Decoded from server: " + new String(base));

        CryptManager man = data.getCurrentUser().getManager();
        String hmm = man.decryptMessage(base, tempkey.pukey1);
        System.out.println("Unencrypted from server: " + hmm);

    }
}