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
    }

    @Override
    public void run() {
        String message;

        Socket socket = data.getClientSocket(pos);

        System.out.println("Listener started");
        try {
            BufferedReader readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (clientRunning) {

                boolean auth = false, user = false;
                message = readIn.readLine();

                ConnectionHandler handler = new ConnectionHandler(data, listener);

                if (message.length() >= 5) {
                    if (message.substring(0, 5).equals("auth:")) {
                        //TODO auth
                        data.setUserID(message.substring(5).getBytes(), pos);
                        System.out.println("User " + message.substring(5) + " has authenticated");
                        auth = true;
                    }

                    if (message.substring(0, 5).equals("user:")) {
                        String newUserID = message.substring(5);
                        System.out.println("User " + newUserID + " searched for");

                        try{
                            user = data.isUserHere(newUserID.getBytes());
                        }catch (Exception e){
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

                        tempSocket.close();
                    }
                }

                if (!auth && !user) {

                    if (!message.equals("") && message.length() > 0) {
                        CryptManager manager = data.getManager();
                        PrivateKey key = manager.getPrivateKey();

                        if (key == null) {
                            listener.messageReceived(message, socket, data);
                        } else {
                            Base64Handler base64 = data.getBase64();
                            byte[] base = base64.fromBase64(message);
                            byte[] msg = manager.decryptMessagePriv(base, key);
                            String msgStr = new String(msg, StandardCharsets.UTF_8);

                            try {
                                String spaceDel = "&space&";
                                String type = msgStr.split(spaceDel)[0];

                                Socket socketInternal = data.getClientSocket(type.getBytes());

                                if (socketInternal == null) {
                                    String IP = type.split(";")[0];
                                    String Port = type.split(";")[1];

                                    socketInternal = new Socket(IP, Integer.parseInt(Port));

                                }

                                String tempStr = msgStr.split(spaceDel)[1];
                                handler.sendMessage(new String(base64.toBase64(tempStr)), socketInternal);

                                socketInternal.close();
                            } catch (Exception e) {
                                System.out.println(e.toString());
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
