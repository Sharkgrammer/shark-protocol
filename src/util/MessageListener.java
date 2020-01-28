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
    private boolean clientRunning;
    private int pos;

    public MessageListener(String name, DataHolder data, ResultHandler listener, boolean clientRunning, int pos) {
        this.data = data;
        this.listener = listener;
        this.clientRunning = clientRunning;
        this.name = name;
        this.pos = pos;
    }

    public Socket returnListenerSocket() {
        return data.getClientSocket(pos);
    }

    //REF https://www.tutorialspoint.com/java/java_multithreading.htm

    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    public void finish() {
        clientRunning = false;
        System.out.println("Listener finished");
    }

    @Override
    public void run() {
        String message;

        Socket socket = data.getClientSocket(pos);

        System.out.println("Listener started");
        try {
            BufferedReader readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            boolean auth = false, user = false, toUser = false;
            message = readIn.readLine();

            if (message != null) {
                ConnectionHandler handler = new ConnectionHandler(data, listener);

                if (message.length() >= 5) {
                    if (message.substring(0, 5).equals("auth:")) {
                        //TODO auth
                        data.setUserID(message.substring(5).getBytes(), pos);
                        System.out.println("User " + message.substring(5) + " has authenticated");
                        auth = true;
                        finish();
                    }

                    if (message.substring(0, 5).equals("user:")) {
                        String newUserID = message.substring(5);
                        System.out.println("User " + newUserID + " searched for");

                        try {
                            user = data.isUserHere(newUserID.getBytes());
                        } catch (Exception e) {
                            user = false;
                        }

                        Socket tempSocket = data.getClientSocket(pos);

                        if (user) {
                            System.out.println("User " + newUserID + " found");
                            handler.sendMessage("user:found", tempSocket);
                        } else {
                            System.out.println("User " + newUserID + " failed");
                            handler.sendMessage("user:failed", tempSocket);
                        }

                        user = true;
                        //tempSocket.close();
                        finish();
                    }
                }

                if (!auth && !user) {

                    if (!message.equals("") && message.length() > 0) {

                        CryptManager manager = data.getManager();
                        PrivateKey key = manager.getPrivateKey();
                        System.out.println(message);

                        Base64Handler base64 = data.getBase64();
                        byte[] base = base64.fromBase64(message);

                        System.out.println(Arrays.toString(base));
                        System.out.println(new String(base, StandardCharsets.UTF_8));

                        System.out.println("Message Decryption started");
                        byte[] msg = manager.decryptMessagePriv(base, key);
                        String msgStr = new String(msg, StandardCharsets.UTF_8);
                        System.out.println("Message Decryption finished");

                        System.out.println(msgStr);

                        if (!data.isServer()) {
                            listener.messageReceived(msgStr, socket, data);
                        } else {

                            try {
                                String spaceDel = "&space&";
                                String type = msgStr.split(spaceDel)[0];

                                System.out.println(type);

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

                                byte[] reformatedBytes = Arrays.copyOfRange(msg, tempBytesLen, msg.length);

                                String finalStr = new String(base64.toBase64(reformatedBytes), StandardCharsets.UTF_8);

                                System.out.println("Sending " + finalStr);

                                handler.sendMessage(finalStr, socketInternal);

                                if (!toUser){
                                    socketInternal.close();
                                    finish();
                                }

                            } catch (Exception e) {
                                System.out.println(Arrays.toString(e.getStackTrace()));
                            }

                        }

                    }

                }

            }

        } catch (Exception e) {
            System.out.println("Error in listen in MessageListener: " + e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

}
