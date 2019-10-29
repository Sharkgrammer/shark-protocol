package recieve;

import util.MessageListener;
import util.ResultHandler;
import util.ServerHolder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler {
    private ServerHolder server;
    private ResultHandler listener;
    private boolean serverRunning;
    private List<MessageListener> receivers;


    public ConnectionHandler(ServerHolder server, ResultHandler listener) {
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

                    MessageListener receiver = new MessageListener("ServerClient" + String.valueOf(receivers.size()), cSocket, listener, true);
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

    public void sendMessage(String message, boolean ToAll, int connection) {
        if (ToAll) {

            for (int x = 0; x < receivers.size(); x++) {
                sendMessage(message, false, x);
            }

            return;
        }

        try {
            System.out.println("Sending message " + message);
            PrintWriter sendOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(receivers.get(connection).returnListenerSocket().getOutputStream())), true);

            sendOut.print(message);
            sendOut.flush();

            System.out.println("Message sent");

        } catch (Exception e) {
            System.out.println("Error in sendMessage: " + e.toString());
        }
    }

}

