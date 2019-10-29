package util;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerHolder {

    private String IP;
    private int port;
    private SocketHolder sockets = new SocketHolder();

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setClientSocket() {
        try {
            Socket socket = new Socket(getIP(), getPort());
            setClientSocket(socket);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void setClientSocket(String ip, int port) {
        try {
            setIP(ip);
            setPort(port);

            Socket socket = new Socket(getIP(), getPort());
            setClientSocket(socket);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void setClientSocket(Socket socket) {
        if (sockets == null) {
            sockets = new SocketHolder();
        }
        sockets.setClient(socket);
    }

    public Socket getClientSocket() {

        if (sockets.getClient() == null){
            setClientSocket();
        }

        return sockets.getClient();
    }

    public void setServerSocket(int port){
        try{
            this.sockets.setServer(new ServerSocket(port));
        }catch(Exception e){
            System.out.println("Error in setServerSocket: " + e.toString());
            this.sockets.setServer(null);
        }
    }

    public void setServerSocket(){
        try{
            sockets.setServer(new ServerSocket(port));
        }catch(Exception e){
            System.out.println("Error in setServerSocket: " + e.toString());
            sockets.setServer(null);
        }
    }

    public ServerSocket getServerSocket(){
        if (sockets.getServer() == null){
            setServerSocket();
        }

        return sockets.getServer();
    }

}
