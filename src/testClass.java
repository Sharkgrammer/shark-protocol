import crypto.CryptManager;
import recieve.ServerHandler;
import send.MessageHandler;
import util.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        DataHolder data = new DataHolder(null, null);
        data.setPort(0);
        data.setIP(null);

        //new Server().run(data);
        new Client().run(data);

        //new CryptManager().run();
        //new ServerListHandler(data, 0).run();

        //base64test();

        System.out.println("shark test end");

    }

}

class Server implements ResultHandler{

    private ServerHandler server ;

    public void run(DataHolder s){

       // temp tempkey = new temp();

        CryptManager manager = new CryptManager();
        manager.setKeys((PublicKey) null, null);
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

        s.setAuthServer();

       // temp tempkey = new temp();

        String ID = "SzXLbr6wLXwdZeJ8dscvSSaeJDU3iumpJy5akAofRBJlDg5yC8IJSNbpQFrmG760my0JRf3yUJiA1b0Y6XwKYuKmWD8ntc9t5gq4aLC62aHZnTXg1SQlJ0PIuQem9nKyLeYIv6NlSwrPH84MePjUi6AM89WWw3jrFCfT5gXyvVnJZRCQqdNl8OopHV1f55zUqfCvHN5ZwfFbxhMDowjxWFuRwVnzrkUeA8cCUCvrM35w2tWXWfUnUpUZY7KlR8Px";

        ID = "d2";

        UserHolder user = new UserHolder(ID.getBytes(), null, null);
        String ToID = "28JuJNLL93lSWr3xjyPnbVF94hllgtsSClHDIqXZuoYMlkZ1sqpd0uXUEXbfHtU1EK1zjxHwNuvVhHxh31F4RleuIW9gLv2poRScCuvgrIFDEGcxudLz2mUn9i17PnGja8UPIxgqIAOhgTF2VDYlrh5vdvHH9kmD0Put8z0kOdkesoKtUh64StUcgxdkrLtgMMLXO5jf5wYw3kZD9BUzx1OBiiW1GC2TamIFd8CsUenjPRnMwecb8eFESgBUocbX";

        ToID = "d1";

        Base64Util b = new Base64Util();
        s.setBase64(b);
        s.setServer(false);
        s.setManager(user.getManager());

        System.out.println("I am " + new String(user.getUserID()));

        MessageHandler client = new MessageHandler(s, this, user);

        client.start();

        client.auth();

        //client.send("pizza boops?", ToID.getBytes());

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