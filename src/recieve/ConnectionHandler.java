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
            lis.finish(true);
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
            int socketCounter = 1;

            while (serverRunning) {
                try {
                    System.out.println("Running...");

                    System.out.println("OPEN THREADS: " + Thread.activeCount());
                    int temp = 1;
                    for (MessageListener lis : receivers){
                        System.out.println((temp++) + " : " + lis.getName() + " : " + lis.isSocketAlive());
                    }

                    receivers.removeIf(lis -> !lis.isSocketAlive());

                    cSocket = sSocket.accept();
                    server.addClientSocket(cSocket, socketCounter);

                    MessageListener receiver = new MessageListener("ServerClient" + socketCounter, server, listener, true, socketCounter);
                    receiver.start();
                    receivers.add(receiver);
                    socketCounter++;
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

