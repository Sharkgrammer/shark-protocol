package util;

public interface Base64Handler{

    byte[] toBase64(String str);
    byte[] toBase64(byte[] str);
    byte[] fromBase64(String str);
    byte[] fromBase64(byte[] str);

}