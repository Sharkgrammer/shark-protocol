package util;

import com.google.gson.annotations.SerializedName;

import java.net.Socket;
import java.util.Arrays;

public class JSONDataHolder {

    @SerializedName("id")
    private String id;

    @SerializedName("ip")
    private String ip;

    @SerializedName("key")
    private String key;

    private Socket socket;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public byte[] getKey(Base64Handler handler) {

        System.out.println(key);
        System.out.println(new String(handler.fromBase64(key)));
        System.out.println(Arrays.toString(handler.fromBase64(key)));


        return handler.fromBase64(key);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Socket getSocket() {
        return socket;
    }

    public void createSocket() {
        String[] address = ip.split(":");

        try {
            int port = Integer.parseInt(address[address.length - 1]);
            String lastIP = ip.replace(":" + port, "");

            socket = new Socket(lastIP, port);
        } catch (Exception e) {
            System.out.println(e.toString());
            socket = null;
        }
    }
}
