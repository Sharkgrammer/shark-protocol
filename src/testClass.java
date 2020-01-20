import crypto.CryptManager;
import recieve.ServerHandler;
import send.MessageHandler;
import util.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

//REF based on https://guides.codepath.com/android/Sending-and-Receiving-Data-with-Sockets#tcpclient for socket code
//REF based on https://stackoverflow.com/a/40100207/11480852 as well

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder data = new DataHolder(null, null);
        data.setPort(6000);
        data.setIP("localhost");

        //new Server().run(data);
        new Client().run(data);

        //new CryptManager().run();
        //new ServerListHandler(data, 0).run();

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

        System.out.println(new String(s.getBase64().toBase64(tempkey.pukey1), StandardCharsets.UTF_8));

        String ID = "d3";
        UserHolder user = new UserHolder(ID.getBytes(), tempkey.pukey1, tempkey.prkey1);
        String ToID = "d2";

        Base64Util b = new Base64Util();
        s.setBase64(b);

        System.out.println("I am " + new String(user.getUserID()));

        MessageHandler client = new MessageHandler(s, this, user);

        client.start();

        client.auth();

        client.send("pizza boop shark", ToID.getBytes());

        //client.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket, DataHolder data) {
        System.out.println("Raw from server: " + message);

        temp tempkey = new temp();

        byte[] base = (new Base64Util()).fromBase64(message);
        System.out.println("Decoded from server: " + new String(base));

        CryptManager man = data.getCurrentUser().getManager();
        String hmm = man.decryptMessagePub(base, tempkey.pukey1);
        System.out.println("Unencrypted from server: " + hmm);

    }
}