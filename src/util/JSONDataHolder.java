package util;

import com.google.gson.annotations.SerializedName;

public class JSONDataHolder {

    @SerializedName("id")
    private String id;

    @SerializedName("ip")
    private String ip;

    @SerializedName("key")
    private String key;

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
        return handler.fromBase64(key);
    }

    public void setKey(String key) {
        this.key = key;
    }
}
