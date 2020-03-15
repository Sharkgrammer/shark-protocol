package util;

import crypto.CryptManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataHolder {

    private String IP;
    private int port;
    private List<SocketHolder> sockets = new ArrayList<>();
    private UserHolder currentUser;
    private UserHolder userTo;
    private Base64Handler base64;
    private URLHandler url;
    private CryptManager manager;
    private ServerListHandler ServerList;
    private JSONDataHolder authServer;
    private boolean isServer;

    public DataHolder(byte[] publicKey, byte[] privateKey) {
        manager = new CryptManager();
        if (publicKey == null) {
            manager.setKeys((PublicKey) null, null);
        } else {
            manager.setKeys(publicKey, privateKey);
        }
    }

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

    public void addClientSocket() {
        try {
            Socket socket = new Socket(getIP(), getPort());
            addClientSocket(socket, 1);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void addClientSocket(Socket socket, int ID) {
        SocketHolder s = new SocketHolder();
        s.setClient(socket);
        s.setSocketID(ID);

        sockets.add(s);
    }

    public Socket getClientSocket() {
        return getClientSocket(0);
    }

    public Socket getClientSocket(int pos) {
        int inPos = getInternalPos(pos);

        if (sockets.isEmpty() || sockets.get(inPos).getClient() == null) {
            addClientSocket();
        }

        return sockets.get(inPos).getClient();
    }

    public Socket getClientSocket(byte[] userID) {
        return sockets.get(findPosByUserID(userID)).getClient();
    }

    public void setServerSocket() {
        setServerSocket(port);
    }

    public void setServerSocket(int port) {
        try {
            SocketHolder s = new SocketHolder();
            s.setServer(new ServerSocket(port));

            sockets.add(s);
        } catch (Exception e) {
            System.out.println("Error in setServerSocket: " + e.toString());
        }
    }

    public ServerSocket getServerSocket() {
        return getServerSocket(0);
    }

    public ServerSocket getServerSocket(int pos) {
        if (sockets.isEmpty()) {
            setServerSocket();
            pos = 0;
        }

        return sockets.get(pos).getServer();
    }

    public int noOfSockets() {
        return sockets.size() - 1;
    }

    public byte[] getUserID(int pos) {

        int inPos = getInternalPos(pos);

        return sockets.get(inPos).getUserID();
    }

    public byte[] getUserID(Socket s) {
        return getUserID(findPosBySocket(s));
    }

    public boolean isUserHere(byte[] ID) {
        return findPosByUserID(ID) != null;
    }

    public boolean isUserHere(Socket s) {
        return findPosBySocket(s) != null;
    }

    public void setUserID(byte[] ID, int pos) {
        int inPos = getInternalPos(pos);

        sockets.get(inPos).setUserID(ID);
    }

    public void setUserID(byte[] ID, Socket s) {
        sockets.get(findPosBySocket(s)).setUserID(ID);
    }

    private Integer findPosBySocket(Socket socket) {

        for (int x = 0; x < sockets.size(); x++) {

            Socket sock = sockets.get(x).getClient();

            if (sock != null) {
                if (socket.equals(sock)) {
                    return x;
                }
            }

        }

        return null;
    }

    private Integer findPosByUserID(byte[] ID) {

        for (int x = sockets.size() - 1; x > 0; x--) {

            byte[] user = sockets.get(x).getUserID();

            if (user != null) {
                if (Arrays.equals(user, ID)) {
                    return x;
                }
            }

        }

        return null;
    }


    public UserHolder getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserHolder currentUser) {
        this.currentUser = currentUser;
    }

    public Base64Handler getBase64() {
        if (base64 == null) {
            base64 = new Base64Util();
        }
        return base64;
    }

    public void setBase64(Base64Handler base64) {
        this.base64 = base64;
    }

    public CryptManager getManager() {
        return manager;
    }

    public void setManager(CryptManager manager) {
        this.manager = manager;
    }

    public URLHandler getUrl() {
        if (url == null) {
            url = new URLUtil();
        }
        return url;
    }

    public void setUrl(URLHandler url) {
        this.url = url;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    private int getInternalPos(int pos) {
        for (SocketHolder sock : sockets) {
            if (sock.getSocketID() == pos) {
                return sockets.indexOf(sock);
            }
        }

        return 0;
    }

    public ServerListHandler getServerList() {

        if (ServerList == null) {
            ServerList = new ServerListHandler(this, 0);
        }

        return ServerList;
    }

    public String getRandomIP() {
        return getAuthServer().getIp().split(":")[0];
    }

    public int getRandomPort() {
        return Integer.parseInt(getAuthServer().getIp().split(":")[1]);
    }

    public void setAuthServer(){

        if (this.getIP() == null){
            authServer = getServerList().getSingleServer();
            this.setPort(getRandomPort());
            this.setIP(getRandomIP());
        }else{
            authServer = getServerList().findServerByIP(this.getIP() + ":" + this.getPort());
        }

    }

    public JSONDataHolder getAuthServer() {
        if (authServer == null) {
            setAuthServer();
        }

        return authServer;
    }

    public UserHolder getUserTo() {
        return userTo;
    }

    public void setUserTo(UserHolder userTo) {
        this.userTo = userTo;
    }
}
