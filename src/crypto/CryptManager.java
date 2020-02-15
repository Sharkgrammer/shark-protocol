package crypto;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CryptManager {

    private String cipherInstance = "RSA/ECB/PKCS1Padding";
    private String keyInstance = "RSA";
    private KeyPair keys = null;

    public void setKeys(byte[] pub, byte[] priv) {
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            if (priv != null) privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priv));
            if (pub != null) publicKey = kf.generatePublic(new X509EncodedKeySpec(pub));

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        setKeys(publicKey, privateKey);
    }

    public void setKeys(PublicKey pub, PrivateKey priv) {

        if (pub == null && priv == null) {
            CryptGenerate gen = new CryptGenerate();
            keys = gen.genKeys(null, null);
        } else {
            keys = new KeyPair(pub, priv);
        }

    }

    public boolean doKeysExist() {
        return keys != null;
    }

    public PrivateKey getPrivateKey() {
        if (keys != null) {
            return keys.getPrivate();
        } else {
            return null;
        }
    }

    public PublicKey getPublicKey() {
        if (keys != null) {
            return keys.getPublic();
        } else {
            return null;
        }
    }

    public byte[] decryptMessagePub(byte[] msg, byte[] pub) {

        try {
            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(pub));;

            return decryptMessagePub(msg, pubKey);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public byte[] decryptMessagePub(byte[] msg, PublicKey pubKey) {
        int lenBytes = returnMaxBytes((RSAKey) pubKey, false);

        System.out.println("lenBytes: " + lenBytes);

        return decryptMessagePub(msg, pubKey, lenBytes);
    }

    public byte[] decryptMessagePub(byte[] msg, PublicKey pub, int maxLen) {
        int len = msg.length;
        List<byte[]> msgList = getMsgList(msg, len, maxLen);
        return passThroughCipher(msgList, pub, len, Cipher.DECRYPT_MODE);
    }

    public byte[] decryptMessagePriv(byte[] msg, byte[] priv) {

        try {
            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priv));;

            return decryptMessagePriv(msg, privKey);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public byte[] decryptMessagePriv(byte[] msg, PrivateKey privKey) {
        int lenBytes = returnMaxBytes((RSAKey) privKey, false);

        System.out.println("lenBytes: " + lenBytes);

        return decryptMessagePriv(msg, privKey, lenBytes);
    }


    public byte[] decryptMessagePriv(byte[] msg, PrivateKey priv, int maxLen) {
        int len = msg.length;
        List<byte[]> msgList = getMsgList(msg, len, maxLen);
        return passThroughCipher(msgList, priv, len, Cipher.DECRYPT_MODE);
    }

    public byte[] encryptMessagePub(String msg, byte[] pub) {
        return encryptMessagePub(msg.getBytes(), pub);
    }

    public byte[] encryptMessagePub(byte[] msg, byte[] pub) {
        try {

            System.out.println("encryptMessagePub/bytes: " + msg.length);

            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(pub));

            return encryptMessagePub(msg, pubKey);

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public byte[] encryptMessagePub(byte[] msg, PublicKey pub) {
        try {

            System.out.println("encryptMessagePub/bytes: " + msg.length);

            //REF https://stackoverflow.com/a/16268737/11480852
            int lenBytes = returnMaxBytes((RSAKey) pub, true);
            System.out.println("lenBytes: " + lenBytes);

            return encryptMessagePub(msg, pub, lenBytes);

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private byte[] encryptMessagePub(byte[] msg, PublicKey pub, int maxLen) {
        int len = msg.length;
        List<byte[]> msgList = getMsgList(msg, len, maxLen);
        return passThroughCipher(msgList, pub, len, Cipher.ENCRYPT_MODE);
    }

    public byte[] encryptMessagePriv(String msg, byte[] priv) {
        return encryptMessagePriv(msg.getBytes(), priv);
    }

    public byte[] encryptMessagePriv(byte[] msg, byte[] priv) {
        try {

            System.out.println("encryptMessagePriv/bytes: " + msg.length);

            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priv));

            return encryptMessagePriv(msg, privKey);

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public byte[] encryptMessagePriv(byte[] msg, PrivateKey priv) {
        try {

            System.out.println("encryptMessagePriv/bytes: " + msg.length);

            //REF https://stackoverflow.com/a/16268737/11480852
            int lenBytes = returnMaxBytes((RSAKey) priv, true);
            System.out.println("lenBytes: " + lenBytes);

            return encryptMessagePriv(msg, priv, lenBytes);

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private byte[] encryptMessagePriv(byte[] msg, PrivateKey priv, int maxLen) {
        int len = msg.length;
        List<byte[]> msgList = getMsgList(msg, len, maxLen);
        return passThroughCipher(msgList, priv, len, Cipher.ENCRYPT_MODE);
    }

    private int returnMaxBytes(RSAKey key, boolean encrypt){

        return ((key.getModulus().bitLength() + 7) / 8) - (encrypt ? 11 : 0);
    }

    public String getUserKey() {
        return getUserKey(256);
    }

    public String getUserKey(int size){
        CryptGenerate gen = new CryptGenerate();
        return gen.getUserKey(size);
    }

    private List<byte[]> getMsgList(byte[] msg, int len, int maxLen){
        List<byte[]> msgList = new ArrayList<>();
        int tempSize = 0;

        for (int lenCounter = 0; len > lenCounter; lenCounter += maxLen){
            if (lenCounter + maxLen > len) {
                tempSize += len - lenCounter;
            }else{
                tempSize += maxLen;
            }

            byte[] tempArr = Arrays.copyOfRange(msg, lenCounter, tempSize);
            msgList.add(tempArr);
        }

        return msgList;
    }

    private byte[] passThroughCipher(List<byte[]> msgList, Key key, int len, int mode){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(len);
        for (byte[] inMsg : msgList){
            System.out.println("inMsg/outputstream: " + inMsg.length);
            try {
                Cipher cipher = Cipher.getInstance(cipherInstance);
                cipher.init(mode, key);
                System.out.println("CIPHER: " + cipher.doFinal(inMsg).length);
                byteStream.write(cipher.doFinal(inMsg));
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        byte[] resultBytes = byteStream.toByteArray();
        System.out.println("resultBytes: " + resultBytes.length);

        return resultBytes;
    }

}
