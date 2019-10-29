import recieve.ServerHandler;
import send.MessageHandler;
import util.ServerHolder;

public class testClass {

    public static void main(String[] args) {
        System.out.println("shark test start");

        ServerHolder server = new ServerHolder();
        server.setPort(6000);
        server.setIP("localhost");

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
        MessageHandler message = new MessageHandler(s);

        message.start();

        message.send("Hello");
        message.send("How are you");
        message.send("You cool?");
        message.send("Fu");

        message.stop();

    }
}
