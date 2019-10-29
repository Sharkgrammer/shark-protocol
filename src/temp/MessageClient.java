package temp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

class MessageClient {

    private String incomingMessage;
    private BufferedReader in;
    private PrintWriter out;
    private MessageCallback listener = null;
    private boolean mRun = true;
    private ServerHandler server;

    public MessageClient(ServerHandler server, MessageCallback listener) {
        this.listener = listener;
        this.server = server;
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
            System.out.println("Sent Message: " + message);

        }
    }

    public void stopClient() {
        System.out.print("Client stopped!");
        mRun = false;
    }


    public boolean send(String outgoingMessage) {

        try {
            InetAddress serverAddress = InetAddress.getByName(server.getIP());

            System.out.println("Connecting...");

            Socket socket = new Socket(serverAddress, server.getPort());


            try {

                // Create PrintWriter object for sending messages to server.
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //Create BufferedReader object for receiving messages from server.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.println("In / Out created");

                //Sending message with command specified by AsyncTask
                this.sendMessage(outgoingMessage);


                //Listen for the incoming messages while mRun = true
                while (mRun) {
                    incomingMessage = in.readLine();
                    if (incomingMessage != null && listener != null) {

                        listener.callbackMessageReceiver(incomingMessage);

                        stopClient();

                    }
                    incomingMessage = null;

                }

                System.out.println("Received Message: " + incomingMessage);

            } catch (Exception e) {

                System.out.println("Error " + e.toString());

            } finally {

                out.flush();
                out.close();
                in.close();
                socket.close();
                System.out.println("Socket Closed");
            }

        } catch (Exception e) {

            System.out.println("Error " + e.toString());
        }


        return false;
    }


    public interface MessageCallback {
        void callbackMessageReceiver(String message);
    }

}
