package recieve;

import util.MessageListener;
import util.ResultHandler;
import util.DataHolder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ConnectionHandler {
    private DataHolder server;
    private ResultHandler listener;
    private boolean serverRunning;
    private List<MessageListener> receivers;


    public ConnectionHandler(DataHolder server, ResultHandler listener) {
        this.listener = listener;
        this.server = server;
    }

    public void stopServer() {
        System.out.println("Server stopped!");
        serverRunning = false;
        for (MessageListener lis : receivers) {
            lis.finish();
        }
    }

    public void startServer() {
        System.out.println("Server started!");
        serverRunning = true;
        receivers = new ArrayList<>();

        try {
            ServerSocket sSocket = server.getServerSocket();
            System.out.println("Turning on server...");
            Socket cSocket;

            while (serverRunning) {
                try {
                    System.out.println("Running...");

                    cSocket = sSocket.accept();
                    server.setClientSocket(cSocket);
                    int len = server.noOfSockets();

                    MessageListener receiver = new MessageListener("ServerClient" + len, server, listener, true, len);
                    receiver.start();
                    receivers.add(receiver);

                } catch (Exception e) {
                    System.out.println("Error in setServerListening: " + e.toString());
                }

            }
        } catch (Exception e) {
            System.out.print("Error in setServerListening" + e.toString());
        }
    }

    public void sendMessage(String message, boolean ToAll, int pos) {
        if (ToAll) {

            for (int x = 0; x < server.noOfSockets(); x++) {
                sendMessage(message, false, x);
            }

            return;
        }

        sendMessage(message, server.getClientSocket(pos));
    }

    public void sendMessage(String message, byte[] userID) {
        sendMessage(message, server.getClientSocket(userID));
    }

    public void sendMessage(String message, Socket socket) {
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

