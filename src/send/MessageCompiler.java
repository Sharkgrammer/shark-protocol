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

                sendOut.println("user:" + to + ":" + new String(this.data.getCurrentUser().getUserID()));
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

        System.out.println("encryptedMsg: " + encryptedMsg.length);

        Base64Handler base64 = data.getBase64();
        serverHandler = new ServerListHandler(data, 0);
        List<JSONDataHolder> list = serverHandler.getServerList();

        JSONDataHolder userServer = findUserOnNetwork();

        tempBytes = addByteArrays((to + spaceDel).getBytes(), encryptedMsg);
        //tempMessage = to + spaceDel + msg;

        ///System.out.println(tempMessage);

        msgBytes = manager.encryptMessagePub(tempBytes, userServer.getKey(base64));

        lastHolder = userServer;

        System.out.println(msgBytes.length);

        for (JSONDataHolder holder : list) {
            tempBytes = addByteArrays((lastHolder.getIp() + spaceDel).getBytes(), msgBytes);

            msgBytes = manager.encryptMessagePub(tempBytes, holder.getKey(base64));

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

        temp keys = new temp();
        byte[] msg = manager.encryptMessagePub(baseMessage.getBytes(), keys.pukey1);

        //msg = baseMessage.getBytes();

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
