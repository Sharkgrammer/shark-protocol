package util;

import crypto.CryptManager;
import recieve.ConnectionHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Arrays;

public class MessageListener implements Runnable {
    private Thread t;
    private DataHolder data;
    private ResultHandler listener;
    private String name;
    private boolean clientRunning, socketAlive;
    private int pos;
    private long dataStartTime;

    public MessageListener(String name, DataHolder data, ResultHandler listener, boolean clientRunning, int pos) {
        this.data = data;
        this.listener = listener;
        this.clientRunning = clientRunning;
        this.name = name;
        this.pos = pos;
        this.socketAlive = true;
    }

    //REF https://www.tutorialspoint.com/java/java_multithreading.htm
    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    public void finish(boolean killSocket) {
        if (killSocket) {
            finishSocket();
        }
        clientRunning = false;

        if (dataStartTime != 0){
            long dataOverallTime = System.currentTimeMillis() - dataStartTime;
            System.out.println("Data parsed in: " + dataOverallTime + " milliseconds");

            dataStartTime = 0;
        }

    }

    public void finishSocket() {
        socketAlive = false;
    }

    @Override
    public void run() {
        String message;
        Base64Handler base64 = data.getBase64();

        Socket socket = data.getClientSocket(pos);

        System.out.println("Listener started");
        try {
            BufferedReader readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int loopCount = 0;

            while (clientRunning) {

                dataStartTime = System.currentTimeMillis();
                boolean auth = false, user = false;
                message = readIn.readLine();

                if (message != null) {
                    ConnectionHandler handler = new ConnectionHandler(data, listener);

                    System.out.println("Data received");

                    if (!message.equals("") && message.length() > 0) {

                        CryptManager manager = data.getManager();
                        PrivateKey key = manager.getPrivateKey();
                        //System.out.println(message);

                        byte[] base = base64.fromBase64(message);

                        //System.out.println(Arrays.toString(base));
                        //System.out.println(new String(base, StandardCharsets.UTF_8));

                        long startTime = System.currentTimeMillis();

                        byte[] msg = manager.decryptMessagePriv(base, key);
                        String msgStr = new String(msg, StandardCharsets.UTF_8);

                        long duration = (System.currentTimeMillis() - startTime);
                        System.out.println("Message Deception took: " + duration + " milliseconds");

                        if (!data.isServer()) {
                            listener.messageReceived(msgStr, socket, data);
                        } else {

                            if (msgStr.length() >= 5) {
                                if (msgStr.substring(0, 5).equals("auth:")) {
                                    data.setUserID(msgStr.substring(5).getBytes(), pos);
                                    System.out.println("User " + msgStr.substring(5) + " has authenticated");
                                    auth = true;
                                    finish(false);

                                } else if (msgStr.substring(0, 5).equals("user:")) {

                                    String newUserID = msgStr.split(":")[1];
                                    String oldUserID = msgStr.split(":")[2];

                                    System.out.println("User " + newUserID + " searched for");

                                    try {
                                        user = data.isUserHere(newUserID.getBytes());
                                    } catch (Exception e) {
                                        user = false;
                                    }

                                    String userMessage = "";
                                    if (user) {
                                        System.out.println("User " + newUserID + " found");
                                        userMessage = "user:found";
                                    } else {
                                        System.out.println("User " + newUserID + " failed");
                                        userMessage = "user:failed";
                                    }

                                    byte[] userBytes = manager.encryptMessagePriv(userMessage.getBytes(), key);
                                    String userFinal = new String(base64.toBase64(userBytes));
                                    handler.sendMessage(userFinal, socket);

                                    System.out.println("Data sent");

                                    user = true;

                                    //Find out if that user is connected to this server
                                    //if so we don't want to close their socket
                                   // System.out.println("Checking if user is on server");
                                    boolean onServer;
                                    try {
                                        onServer = data.isUserHere(oldUserID.getBytes());
                                    } catch (Exception e) {
                                        onServer = false;
                                    }

                                    //if (!onServer) finish(true);

                                    //tempSocket.close();
                                }
                            }

                            if (!auth && !user) {

                                try {
                                    boolean toUser = false;
                                    String spaceDel = "&space&";
                                    String type = msgStr.split(spaceDel)[0];

                                    //System.out.println(type);

                                    Socket socketInternal;
                                    try {
                                        socketInternal = data.getClientSocket(type.getBytes());
                                        toUser = true;
                                    } catch (Exception e) {
                                        socketInternal = null;
                                    }

                                    if (socketInternal == null) {
                                        String IP = type.split(":")[0];
                                        String Port = type.split(":")[1];

                                        socketInternal = new Socket(IP, Integer.parseInt(Port));
                                    }

                                    byte[] tempBytes = (type + spaceDel).getBytes();
                                    int tempBytesLen = tempBytes.length;

                                    byte[] reformattedBytes = Arrays.copyOfRange(msg, tempBytesLen, msg.length);

                                    String finalStr = new String(base64.toBase64(reformattedBytes), StandardCharsets.UTF_8);

                                    //System.out.println("Sending " + finalStr);

                                    handler.sendMessage(finalStr, socketInternal);

                                    System.out.println("Data sent");
                                    if (!toUser) {
                                        socketInternal.close();
                                        finish(true);
                                    }
                                } catch (Exception e) {
                                    System.out.println(Arrays.toString(e.getStackTrace()));
                                }

                            }

                        }

                    }

                }

                loopCount++;

                if (loopCount > 50) {
                    finish(true);
                }

            }

            finish(false);

        } catch (Exception e) {
            System.out.println("Error in listen in MessageListener: " + e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
            finish(true);
        }
    }

    public boolean isSocketAlive() {
        return socketAlive;
    }

    public String getName() {
        return name;
    }
}
