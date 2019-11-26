package util;

import java.util.Base64;

public class Base64Util implements Base64Handler {

    public byte[] toBase64(String str){
        return Base64.getEncoder().encode(str.getBytes());
    }

    public byte[] toBase64(byte[] str){
        return Base64.getEncoder().encode(str);
    }

    public byte[] fromBase64(String str){
        return Base64.getDecoder().decode(str.getBytes());
    }

    public byte[] fromBase64(byte[] str){
        return Base64.getMimeDecoder().decode(str);
    }

}
