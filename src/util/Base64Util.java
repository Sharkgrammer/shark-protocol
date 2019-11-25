package util;

import java.util.Base64;

public class Base64Util {

    public byte[] toBase64(String str){
        return Base64.getMimeEncoder().encode(str.getBytes());
    }

    public byte[] toBase64(byte[] str){
        return Base64.getMimeEncoder().encode(str);
    }

    public byte[] fromBase64(String str){
        return Base64.getMimeDecoder().decode(str.getBytes());
    }

    public byte[] fromBase64(byte[] str){
        return Base64.getMimeDecoder().decode(str);
    }

}
