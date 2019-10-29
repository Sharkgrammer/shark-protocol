package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageListener implements Runnable {
    private Thread t;
    private Socket socket;
    private ResultHandler listener;
    private String name;
    private boolean clientRunning;

    public MessageListener(String name, Socket socket, ResultHandler listener, boolean clientRunning){
        this.socket = socket;
        this.listener = listener;
        this.clientRunning = clientRunning;
        this.name = name;
    }

    public Socket returnListenerSocket(){
        return socket;
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
        String message = "";
        int c;
        System.out.println("Listener started");
        try {
            BufferedReader readIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (clientRunning) {

                message = readIn.readLine();

                if (!message.equals("")) {
                    listener.messageReceived(message);
                    listener.socketReceived(socket);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in sendMessage: " + e.toString());
        }
    }

}
