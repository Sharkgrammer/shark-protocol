package send;

import crypto.CryptManager;
import util.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MessageCompiler {

    private String message, IDStr;
    private DataHolder data;
    private Socket socket;
    private CryptManager manager;
    private String spaceDel = "&space&";
    private ServerListHandler serverHandler;

    public MessageCompiler(String message, byte[] IDStr, DataHolder data, Socket socket) {
        this.message = message;
        this.IDStr = byteToString(IDStr);
        this.data = data;
        this.socket = socket;
        this.manager = data.getCurrentUser().getManager();
        this.serverHandler = new ServerListHandler(data, 0);
    }

    public MessageCompiler(byte[] ID, DataHolder data) {
        this.IDStr = byteToString(ID);
        this.data = data;
        this.manager = data.getCurrentUser().getManager();
        this.serverHandler = new ServerListHandler(data, 0);
    }

    public JSONDataHolder findUserOnNetwork() {
        List<JSONDataHolder> list = serverHandler.getServerListFull();
        JSONDataHolder result = null;
        PrintWriter sendOut;
        BufferedReader readIn;

        for (JSONDataHolder JSONData : list) {
            Socket socketInternal = JSONData.getSocket();

            try {
                System.out.print("Searching: " + IDStr);
                System.out.println(JSONData.getIp() + " " + JSONData.getId());
                System.out.println(": " + socketInternal.getInetAddress() + " " + socketInternal.getPort());

                sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketInternal.getOutputStream())), true);

                String message = "user:" + IDStr + ":" + new String(this.data.getCurrentUser().getUserID());
                Base64Handler base64 = data.getBase64();
                byte[] msgBytes = manager.encryptMessagePub(message.getBytes(), JSONData.getKey(base64));

                sendOut.println(new String(base64.toBase64(msgBytes)));
                sendOut.flush();

                System.out.println("search sent to " + JSONData.getIp());

                readIn = new BufferedReader(new InputStreamReader(socketInternal.getInputStream()));
                String serverResponse = readIn.readLine();

                msgBytes = base64.fromBase64(serverResponse);
                byte[] decryptedMsgBytes = manager.decryptMessagePub(msgBytes, JSONData.getKey(base64));
                serverResponse = new String(decryptedMsgBytes);

                if (serverResponse.equals("user:found")) {
                    result = JSONData;

                    System.out.println("search finished");
                    break;
                }

            } catch (Exception e) {
                System.out.println("Error in findUserOnNetwork: " + e.toString() + " " + Arrays.toString(e.getStackTrace()));
            }
        }

        return result;
    }

    private byte[] compileDataPackage(byte[] encryptedMsg) {
        JSONDataHolder lastHolder;
        byte[] msgBytes, tempBytes;

        System.out.println("encryptedMsg: " + encryptedMsg.length);

        Base64Handler base64 = data.getBase64();
        List<JSONDataHolder> list = serverHandler.getServerList();

        JSONDataHolder userServer = findUserOnNetwork();

        tempBytes = addByteArrays((IDStr + spaceDel).getBytes(), encryptedMsg);
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

        //temp keys = new temp();
        byte[] msg = manager.encryptMessagePub(baseMessage.getBytes(), data.getUserTo().getManager().getPublicKey());

        //msg = baseMessage.getBytes();

        byte[] compiledMsg = compileDataPackage(msg);

        byte[] encodedMsg = data.getBase64().toBase64(compiledMsg);

        String finalMsg = byteToString(encodedMsg);
        System.out.println("FINAL MESSAGE: " + finalMsg);

        return finalMsg;
    }

    public String returnAuthMessage() {
        Base64Handler base64 = data.getBase64();

        JSONDataHolder serverData = data.getAuthServer();

        String baseMessage = "auth:" + IDStr;

        byte[] msg = manager.encryptMessagePub(baseMessage.getBytes(), serverData.getKey(base64));
        byte[] encodedMsg = data.getBase64().toBase64(msg);

        return new String(encodedMsg);
    }

    public PrintWriter returnWriter() {
        try {
            System.out.println(socket.getInetAddress() + ":" + socket.getPort());
            return new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private String byteToString(byte[] array) {
        return new String(array, StandardCharsets.UTF_8);
    }

    //REF https://www.tutorialspoint.com/java/java_bytearrayoutputstream.htm
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
