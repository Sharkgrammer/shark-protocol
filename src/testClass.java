import recieve.ServerHandler;
import send.MessageHandler;
import util.ServerHolder;

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        ServerHolder server = new ServerHolder();
        server.setPort(139);
        server.setIP("35.234.148.116");
        //server(server);
        client(server);

        System.out.println("shark test end");
    }

    static void server(ServerHolder s){
        ServerHandler server = new ServerHandler(s);
        server.start();
        server.stop();
    }

    static void client(ServerHolder s){
        MessageHandler client = new MessageHandler(s);

        client.start();

        client.send("Hello");
        client.send("How are you");
        client.send("You cool?");
        client.send("Fu");
        client.send("I'm a shark");

        client.stop();

    }
}
