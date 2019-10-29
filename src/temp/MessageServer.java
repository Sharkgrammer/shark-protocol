package temp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageServer {

    private BufferedReader in;
    private PrintWriter out;
    ServerSocket sSocket = null;
    Socket cSocket = null;
    private MessageServer.MessageCallback listener = null;
    private boolean mRun = true;
    private int port;

    public MessageServer(int port, MessageServer.MessageCallback listener) {
        this.listener = listener;
        this.port = port;
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
            System.out.print("Sent Message: " + message);

        }
    }

    public void stopServer() {
        System.out.print("Client stopped!");
        mRun = false;
        System.exit(0);
    }


    public boolean startServer() {

        try {
            sSocket = new ServerSocket(port);
            System.out.println("Turning on server...");

            while (mRun) {
                try {
                    cSocket = sSocket.accept();
                    ConnectionHandler con = new ConnectionHandler(cSocket, this);
                    con.run();
                } catch (IOException e) {
                    System.out.println(e);
                }

            }
        } catch (Exception e) {
            System.out.print("Error " + e.toString());
        }


        return false;
    }


    public interface MessageCallback {
        void callbackMessageReceiver(String message);

        void callbackSocketReceiver(Socket socket);
    }


}

