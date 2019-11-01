package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageListener implements Runnable {
    private Thread t;
    private DataHolder server;
    private ResultHandler listener;
    private String name;
    private boolean clientRunning;
    private int pos;

    public MessageListener(String name, DataHolder server, ResultHandler listener, boolean clientRunning, int pos){
        this.server = server;
        this.listener = listener;
        this.clientRunning = clientRunning;
        this.name = name;
        this.pos = pos;
    }

    public Socket returnListenerSocket(){
        return server.getClientSocket(pos);
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    public void finish(){
        clientRunning = false;
    }

    @Override
    public void run() {
        String message;

        Socket socket = server.getClientSocket();

        System.out.println("Listener started");
        try {
            BufferedReader readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (clientRunning) {

                message = readIn.readLine();

                if (message.substring(0, 5).equals("auth:")){
                    //TODO auth
                    server.setUserID(message.substring(5).getBytes(), pos);
                }else{
                    listener.messageReceived(message, server);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in sendMessage in MessageListener: " + e.toString());
        }
    }

}
