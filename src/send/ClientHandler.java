package send;

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
        receiver.finish(true);
    }

    public void sendMessage(String message, byte[] to) {
        if (clientAlive) {
            try {
                MessageCompiler compiler = new MessageCompiler(message, to, data, socket);
                System.out.println("Compiler started");

                String finalMsg = compiler.returnMessage();
                PrintWriter sendOut = compiler.returnWriter();
                System.out.println("Compiler finished");

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
            String authMessage;
            byte[] ID = data.getCurrentUser().getUserID();
            System.out.println("Sending auth:" + new String(ID));

            MessageCompiler compiler = new MessageCompiler(ID, data);
            authMessage = compiler.returnAuthMessage();

            //REF  https://guides.codepath.com/android/Sending-and-Receiving-Data-with-Sockets#tcpclient
            PrintWriter sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            sendOut.println(authMessage);
            sendOut.flush();

            System.out.println("auth sent");

        } catch (Exception e) {
            System.out.println("Error in sendAuthMessage: " + e.toString() + " " + Arrays.toString(e.getStackTrace()));
        }
    }

    private String byteToString(byte[] array) {
        return new String(array);
    }

    public boolean isUserOnline(byte[] ID){
        MessageCompiler tempCompiler = new MessageCompiler(ID, data);

        JSONDataHolder dataObj = tempCompiler.findUserOnNetwork();

        return (dataObj != null);
    }

}