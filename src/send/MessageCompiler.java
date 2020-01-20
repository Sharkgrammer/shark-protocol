package send;

import crypto.CryptManager;
import util.Base64Handler;
import util.DataHolder;
import util.JSONDataHolder;
import util.ServerListHandler;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class MessageCompiler {

    private String message;
    private byte[] to;
    private DataHolder data;
    private Socket socket;
    private CryptManager manager;
    private String spaceDel = "&space&";
    private ServerListHandler serverHandler;


    public MessageCompiler(String message, byte[] to, DataHolder data, Socket socket) {
        this.message = message;
        this.to = to;
        this.data = data;
        this.socket = socket;
        manager = data.getCurrentUser().getManager();
    }

    private JSONDataHolder findUserOnNetwork() {
        List<JSONDataHolder> list = serverHandler.getServerListFull();
        JSONDataHolder result = list.get(0);

        //TODO
        //send a message to each server, asking about a user
        //return its JSON object

        return result;
    }

    private byte[] compileDataPackage(byte[] encryptedMsg) {
        String msg = byteToString(encryptedMsg), tempMessage = "";
        JSONDataHolder lastHolder;
        byte[] msgBytes, tempBytes;

        System.out.println("encryptedMsg: " + encryptedMsg.length);
        System.out.println("MSG: " + msg.getBytes().length);

        Base64Handler base64 = data.getBase64();
        serverHandler = new ServerListHandler(data, 0);
        List<JSONDataHolder> list = serverHandler.getServerList();

        JSONDataHolder userServer = findUserOnNetwork();

        tempMessage = byteToString(to) + spaceDel + msg;

        msgBytes = manager.encryptMessagePub(tempMessage, userServer.getKey(base64));

        lastHolder = userServer;

        System.out.println(msgBytes.length);

        for (JSONDataHolder holder : list) {
            tempBytes = addByteArrays((lastHolder.getIp() + spaceDel).getBytes(),  msgBytes);

            System.out.println(holder.getIp());
            msgBytes = manager.encryptMessagePub(tempBytes, holder.getKey(base64));

            System.out.println(msg.getBytes().length);

            lastHolder = holder;
        }

        String[] address = lastHolder.getIp().split(":");

        try {
            int port = Integer.parseInt(address[address.length - 1]);
            String lastIP = lastHolder.getIp().replace(":" + port, "");

            socket = new Socket(lastIP, port);
            data.setClientSocket(socket);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        System.out.println("msgBytes: " + msgBytes.length);
        System.out.println("msgBytes: " + Arrays.toString(msgBytes));
        return msgBytes;
    }

    public String returnMessage() {
        System.out.println("Sending message " + message);

        String fromID = byteToString(data.getCurrentUser().getUserID());
        String baseMessage = fromID + spaceDel + message;

        System.out.println("Base message sting: " + baseMessage.getBytes().length);

        byte[] msg = manager.encryptMessagePriv(baseMessage);

        byte[] compiledMsg = compileDataPackage(msg);
        byte[] encodedMsg = data.getBase64().toBase64(compiledMsg);

        String finalMsg = byteToString(encodedMsg);
        System.out.println("FINAL MESSAGE: " + finalMsg);

        return finalMsg;
    }

    public PrintWriter returnWriter() {
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

    private byte[] addByteArrays(byte[] one, byte[] two) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(one.length + two.length);

        try {
            byteStream.write(one);
            byteStream.write(two);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteStream.toByteArray();
    }
}
