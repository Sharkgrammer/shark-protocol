package temp;

import java.net.Socket;

public class SocketHandler implements MessageServer.MessageCallback {

    private int port;
    private MessageServer server;

    public SocketHandler(int port){
        this.port = port;

        MessageServer s = new MessageServer(port, this);
        server = s;
    }

    public void get(){

        server.startServer();

    }

    @Override
    public void callbackMessageReceiver(String message) {

    }

    @Override
    public void callbackSocketReceiver(Socket socket) {

    }
}
