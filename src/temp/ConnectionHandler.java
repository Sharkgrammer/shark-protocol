package temp;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler {

    BufferedReader is;
    PrintStream os;
    Socket clientSocket;
    MessageServer server;

    public ConnectionHandler(Socket clientSocket, MessageServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        System.out.println("Connection established with: " + clientSocket);
        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void run() {
        String line = null;
        try {
            boolean serverStop = false;

            while (true) {
                line = is.readLine();
                System.out.println("Received " + line);

                os.println("Sharks, recieved loud and clear");

                if (line != null){
                    serverStop = true;
                    break;
                }
            }

            System.out.println("Connection closed.");
            is.close();
            os.close();
            clientSocket.close();

            if (serverStop) server.stopServer();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
