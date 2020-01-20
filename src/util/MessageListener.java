package util;

import recieve.ConnectionHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;

public class MessageListener implements Runnable {
    private Thread t;
    private DataHolder server;
    private ResultHandler listener;
    private String name;
    private boolean clientRunning;
    private int pos;

    public MessageListener(String name, DataHolder server, ResultHandler listener, boolean clientRunning, int pos) {
        this.server = server;
        this.listener = listener;
        this.clientRunning = clientRunning;
        this.name = name;
        this.pos = pos;
    }

    public Socket returnListenerSocket() {
        return server.getClientSocket(pos);
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

        Socket socket = server.getClientSocket(pos);

        System.out.println("Listener started");
        try {
            BufferedReader readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (clientRunning) {

                boolean auth = false, user = false;
                message = readIn.readLine();

                if (message.length() >= 5) {
                    if (message.substring(0, 5).equals("auth:")) {
                        //TODO auth
                        server.setUserID(message.substring(5).getBytes(), pos);
                        System.out.println("User " + message.substring(5) + " has authenticated");
                        auth = true;
                    }

                    if (message.substring(0, 5).equals("user:")) {
                        //TODO user search
                        String newUserID = message.substring(5);
                        System.out.println("User " + newUserID + " searched for");

                        user = server.isUserHere(newUserID.getBytes());
                        ConnectionHandler handler = new ConnectionHandler(server, listener);

                        if (user){
                            System.out.println("User " + newUserID + " found");
                            handler.sendMessage("user:found", server.getClientSocket(pos));
                        }else{
                            System.out.println("User " + newUserID + " failed");
                            handler.sendMessage("user:failed", server.getClientSocket(pos));
                        }
                    }
                }

                if (!auth && !user) {

                    if (!message.equals("") && message.length() > 0) {


                        listener.messageReceived(message, socket, server);
                    }

                }

            }

        } catch (Exception e) {
            System.out.println("Error in listen in MessageListener: " + e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

}
