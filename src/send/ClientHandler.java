package send;

import util.MessageListener;
import util.ResultHandler;
import util.DataHolder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler {
    private ResultHandler listener;
    private DataHolder server;
    private Socket socket;
    private MessageListener receiver;
    private boolean clientAlive = false;
    private byte[] userID;

    public ClientHandler(DataHolder server, ResultHandler listener, byte[] userID) {
        this.listener = listener;
        this.server = server;
        this.userID = userID;
    }

    public void startClient() {
        System.out.println("Client started!");

        System.out.println("Connecting to server...");
        socket = server.getClientSocket();

        if (socket == null) {
            System.out.println("Failure to connect");
        } else {
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

    public void sendMessage(String message, byte[] to) {
        if (clientAlive) {
            try {
                String toID = byteToString(to);
                String fromID = byteToString(userID);

                System.out.println("Sending message " + message);
                PrintWriter sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                sendOut.println(fromID + "&space&" + message + "&space&" + toID);

                sendOut.flush();

                System.out.println("Message sent");

            } catch (Exception e) {
                System.out.println("Error in sendMessage: " + e.toString());
            }
        }
    }

    public void sendAuthMessage() {
        try {
            String ID = byteToString(userID);

            System.out.println("Sending auth:" + ID);
            PrintWriter sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            sendOut.println("auth:" + ID);
            sendOut.flush();

            System.out.println("auth sent");

        } catch (Exception e) {
            System.out.println("Error in sendAuthMessage: " + e.toString());
        }
    }

    private String byteToString(byte[] array) {
        return new String(array, StandardCharsets.UTF_8);
    }

}