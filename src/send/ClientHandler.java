package send;

import util.MessageListener;
import util.ResultHandler;
import util.DataHolder;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private ResultHandler listener;
    private DataHolder server;
    private Socket socket;
    private MessageListener receiver;
    private boolean clientAlive = false;

    public ClientHandler(DataHolder server, ResultHandler listener) {
        this.listener = listener;
        this.server = server;
    }

    public void startClient() {
        System.out.println("Client started!");

        System.out.println("Connecting to server...");
        socket = server.getClientSocket();

        if (socket == null){
            System.out.println("Failure to connect");
        }else{
            clientAlive = true;
            System.out.println("Connected!");
        }

        receiver = new MessageListener("ClientListener", server, listener, clientAlive, 0);
        receiver.start();

    }

    public void stopClient() {
        clientAlive = false;
        System.out.println("Client stopped!");
        receiver.finish();
    }

    public void sendMessage(String message) {
        if (clientAlive){
            try {
                System.out.println("Sending message " + message);
                PrintWriter sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                sendOut.println(message);
                sendOut.flush();

                System.out.println("Message sent");

            } catch (Exception e) {
                System.out.println("Error in sendMessage: " + e.toString());
            }
        }
    }

}