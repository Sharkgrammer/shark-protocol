package send;

import crypto.CryptManager;
import util.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MessageCompiler {

    private String message, to;
    private DataHolder data;
    private Socket socket;
    private CryptManager manager;
    private String spaceDel = "&space&";
    private ServerListHandler serverHandler;


    public MessageCompiler(String message, byte[] to, DataHolder data, Socket socket) {
        this.message = message;
        this.to = byteToString(to);
        this.data = data;
        this.socket = socket;
        manager = data.getCurrentUser().getManager();
    }

    private JSONDataHolder findUserOnNetwork() {
        List<JSONDataHolder> list = serverHandler.getServerListFull();
        JSONDataHolder result = null;
        PrintWriter sendOut;
        BufferedReader readIn;

        for (JSONDataHolder data : list) {
            Socket socketInternal = data.getSocket();

            try {
                System.out.println("Searching: " + to);
                sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketInternal.getOutputStream())), true);

                sendOut.println("user:" + to);
                sendOut.flush();

                System.out.println("search sent to " + data.getIp());

                readIn = new BufferedReader(new InputStreamReader(socketInternal.getInputStream()));
                String serverResponse = readIn.readLine();
                if (serverResponse.equals("user:found")) {
                    result = data;

                    System.out.println("search finished");
                    break;
                }

            } catch (Exception e) {
                System.out.println("Error in findUserOnNetwork: " + e.toString());
            }
        }

        return result;
    }

    private byte[] compileDataPackage(byte[] encryptedMsg) {
        String msg = byteToString(encryptedMsg), tempMessage = "";
        JSONDataHolder lastHolder;
        byte[] msgBytes, tempBytes;

        System.out.println(byteToString(encryptedMsg));

        System.out.println("encryptedMsg: " + encryptedMsg.length);

        Base64Handler base64 = data.getBase64();
        serverHandler = new ServerListHandler(data, 0);
        List<JSONDataHolder> list = serverHandler.getServerList();

        JSONDataHolder userServer = findUserOnNetwork();

        tempMessage = to + spaceDel + msg;

        System.out.println(tempMessage);

        msgBytes = manager.encryptMessagePub(tempMessage, userServer.getKey(base64));

        lastHolder = userServer;

        System.out.println(msgBytes.length);

        for (JSONDataHolder holder : list) {
            tempBytes = addByteArrays((lastHolder.getIp() + spaceDel).getBytes(), msgBytes);

            System.out.println(byteToString(tempBytes));

            msgBytes = manager.encryptMessagePub(tempBytes, holder.getKey(base64));

            System.out.println(msg.getBytes().length);

            lastHolder = holder;
        }

        socket = lastHolder.getSocket();

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

        temp temp = new temp();
        System.out.println("FINAL MESSAGE: DECRYPT TEST");

        byte[] decodedmsg = data.getBase64().fromBase64(encodedMsg);

        System.out.println(Arrays.equals(compiledMsg, decodedmsg));

        byte[] msg2 = manager.decryptMessagePriv(decodedmsg, temp.prkey1);

        try{

            System.out.println("Client test for decoded IP");
            String str = byteToString(msg2);
            System.out.println(str.split("&space&")[0]);
            System.out.println(str);
            System.out.println("Client test for decoded IP end");


        }catch (Exception e){

        }




        System.out.println(byteToString(msg2));

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
        return new String(array, StandardCharsets.UTF_8);
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
