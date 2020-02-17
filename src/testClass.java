import crypto.CryptManager;
import recieve.ServerHandler;
import send.MessageHandler;
import util.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

//REF based on https://guides.codepath.com/android/Sending-and-Receiving-Data-with-Sockets#tcpclient for socket code
//REF based on https://stackoverflow.com/a/40100207/11480852 as well

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder data = new DataHolder(null, null);
        data.setPort(6002);
        data.setIP("localhost");

        //new Server().run(data);
         new Client().run(data);

        //new CryptManager().run();
        //new ServerListHandler(data, 0).run();

        //base64test();

        System.out.println("shark test end");

    }



    private static void base64test(){
        temp tempkey = new temp();

        Base64Handler handler = new Base64Util();

        byte[] pubkey = tempkey.pukey1;

        byte[] temp = handler.toBase64(pubkey);

        String tempString = new String(temp, StandardCharsets.UTF_8);
        System.out.println(tempString);

        //byte[] pubkey2 = handler.fromBase64(temp);
        byte[] pubkey2 = handler.fromBase64(tempString.getBytes());


        System.out.println(Arrays.equals(pubkey, pubkey2));

        System.out.println(Arrays.toString(pubkey));
        System.out.println(Arrays.toString(pubkey2));

    }

}

class Server implements ResultHandler{

    private ServerHandler server ;

    public void run(DataHolder s){

        temp tempkey = new temp();

        CryptManager manager = new CryptManager();
        manager.setKeys(tempkey.pukey1, tempkey.prkey1);
        s.setManager(manager);
        s.setServer(true);

        System.out.println("I am " + s.getIP() + ":" + s.getPort());

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

        server.sendMessage(message, IDTo.getBytes(), IDFrom);
    }

}

class Client implements ResultHandler{

    void run(DataHolder s){

        temp tempkey = new temp();

        String ID = "d2";
        UserHolder user = new UserHolder(ID.getBytes(), tempkey.pukey1, tempkey.prkey1);
        String ToID = "d1";

        Base64Util b = new Base64Util();
        s.setBase64(b);
        s.setServer(false);

        CryptManager manager = new CryptManager();
        manager.setKeys(tempkey.pukey1, tempkey.prkey1);
        s.setManager(manager);

        System.out.println("I am " + new String(user.getUserID()));

        MessageHandler client = new MessageHandler(s, this, user);

        client.start();

        client.auth();

        client.send("pizza boops loops", ToID.getBytes());

        //client.stop();

    }

    @Override
    public void messageReceived(String message, Socket socket, DataHolder data) {
        System.out.println("Unencrypted from server: " + message);

        try{
            String fromID = message.split("&space&")[0];
            String msg = message.split("&space&")[1];

            System.out.println(fromID + " says " + msg);
        }catch(Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }
}