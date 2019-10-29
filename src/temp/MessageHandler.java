package temp;

public class MessageHandler implements MessageClient.MessageCallback {

    private ServerHandler server;

    public ServerHandler getServer() {
        return server;
    }

    public void setServer(ServerHandler server) {
        this.server = server;
    }

    public boolean sendMessage(){

        MessageClient client = new MessageClient(server, this);
        client.send("Hello, world of sharks");

        return false;
    }


    @Override
    public void callbackMessageReceiver(String message) {
        System.out.println("Message from server: " + message);
    }
}
