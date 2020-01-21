package send;

import crypto.CryptManager;
import util.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler {
    private ResultHandler listener;
    private DataHolder data;
    private Socket socket;
    private MessageListener receiver;
    private boolean clientAlive = false;

    public ClientHandler(DataHolder data, ResultHandler listener) {
        this.listener = listener;
        this.data = data;
    }

    public void startClient() {
        System.out.println("Client started!");

        System.out.println("Connecting to server...");
        socket = data.getClientSocket();

        if (socket == null) {
            System.out.println("Failure to connect");
        } else {
            clientAlive = true;
            System.out.println("Connected!");
        }

        receiver = new MessageListener("ClientListener", data, listener, clientAlive, 0);
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
                MessageCompiler compiler = new MessageCompiler(message, to, data, socket);
                String finalMsg = compiler.returnMessage();
                PrintWriter sendOut = compiler.returnWriter();

                sendOut.println(finalMsg);
                sendOut.flush();

                System.out.println("Message sent");
            } catch (Exception e) {
                System.out.println("Error in sendMessage: " + e.toString());
            }
        }
    }

    public void sendAuthMessage() {
        try {
            String ID = byteToString(data.getCurrentUser().getUserID());

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
        return new String(array);
    }

}