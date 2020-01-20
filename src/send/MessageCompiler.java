package send;

import crypto.CryptManager;
import util.DataHolder;
import util.ServerListHandler;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageCompiler {

    private String message;
    private byte[] to;
    private DataHolder data;
    private Socket socket;
    private ServerListHandler handler;

    public MessageCompiler(String message, byte[] to, DataHolder data, Socket socket){
        this.message = message;
        this.to = to;
        this.data = data;
        this.socket = socket;
    }

    public String returnMessage(){
        String toID = byteToString(to);
        String fromID = byteToString(data.getCurrentUser().getUserID());
        String spaceDel = "&space&";

        CryptManager manager = data.getCurrentUser().getManager();
        byte[] msg = manager.encryptMessagePriv(message);
        byte[] encodedMsg = data.getBase64().toBase64(msg);

        System.out.println("Sending message " + message);
        String finalMsg = fromID + spaceDel + new String(encodedMsg)+ spaceDel + toID;
        System.out.println("FINAL MESSAGE: " + finalMsg);

        return finalMsg;
    }

    public PrintWriter returnWriter(){
        try {
            return new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private String byteToString(byte[] array) {
        return new String(array);
    }
}
