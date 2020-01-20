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


    public String decryptMessagePub(byte[] msg, byte[] pub) {
        try {

            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            return decryptMessagePub(msg, kf.generatePublic(new X509EncodedKeySpec(pub)));

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public String decryptMessagePub(byte[] msg, PublicKey pub) {
        String result = null;

        try {
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.DECRYPT_MODE, pub);
            result = new String(cipher.doFinal(msg), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return result;
    }

    public byte[] decryptMessagePriv(byte[] msg, byte[] priv) {

        try {

            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priv));

            //REF https://stackoverflow.com/a/16268737/11480852
            int lenBytes = returnMaxBytes((RSAKey) privKey);
            System.out.println("lenBytes: " + lenBytes);

            return decryptMessagePriv(msg, privKey);

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public byte[] decryptMessagePriv(byte[] msg, PrivateKey privKey) {
        int lenBytes = returnMaxBytes((RSAKey) privKey);

        System.out.println("lenBytes: " + lenBytes);

        return decryptMessagePriv(msg, privKey, lenBytes);
    }


    public byte[] decryptMessagePriv(byte[] msg, PrivateKey priv, int maxLen) {
        String result = null;

        int len = msg.length, tempSize = 0;
        List<byte[]> msgList = new ArrayList<>();

        for (int lenCounter = 0; len > lenCounter; lenCounter += maxLen){
            if (lenCounter + maxLen > len) {
                tempSize += len - lenCounter;
            }else{
                tempSize += maxLen;
            }

            byte[] tempArr = Arrays.copyOfRange(msg, lenCounter, tempSize);
            msgList.add(tempArr);
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(len);
        for (byte[] inMsg : msgList){
            System.out.println("inMsg: " + inMsg.length);
            System.out.println("inMsg: " + Arrays.toString(inMsg));
            try {
                Cipher cipher = Cipher.getInstance(cipherInstance);
                cipher.init(Cipher.DECRYPT_MODE, priv);
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

    public byte[] encryptMessagePriv(String msg) {
        byte[] result = null;

        System.out.println("encryptMessagePub/string: " + msg.getBytes().length);

        try {
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
            result = cipher.doFinal(msg.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        System.out.println("encryptMessagePub/final/string: " + result.length);

        return result;
    }

    public byte[] encryptMessagePub(String msg, byte[] pub) {
        return encryptMessagePub(msg.getBytes(), pub);
    }

    public byte[] encryptMessagePub(byte[] msg, byte[] pub) {
        try {

            System.out.println("encryptMessagePub/bytes: " + msg.length);

            KeyFactory kf = KeyFactory.getInstance(keyInstance);
            PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(pub));

            //REF https://stackoverflow.com/a/16268737/11480852
            int lenBytes = returnMaxBytes((RSAKey) pubKey);
            System.out.println("lenBytes: " + lenBytes);

            return encryptMessagePub(msg, pubKey, lenBytes);

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private byte[] encryptMessagePub(byte[] msg, PublicKey pub, int maxLen) {
        int len = msg.length, tempSize = 0;
        List<byte[]> msgList = new ArrayList<>();

        for (int lenCounter = 0; len > lenCounter; lenCounter += maxLen){
            if (lenCounter + maxLen > len) {
                tempSize += len - lenCounter;
            }else{
                tempSize += maxLen;
            }

            byte[] tempArr = Arrays.copyOfRange(msg, lenCounter, tempSize);
            msgList.add(tempArr);
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(len);
        for (byte[] inMsg : msgList){
            System.out.println("inMsg: " + inMsg.length);
            System.out.println("inMsg: " + Arrays.toString(inMsg));
            try {
                Cipher cipher = Cipher.getInstance(cipherInstance);
                cipher.init(Cipher.ENCRYPT_MODE, pub);
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

    private int returnMaxBytes(RSAKey key){
        return ((key.getModulus().bitLength() + 7) / 8) - 11;
    }

    public void run() {
        try {
            setKeys((PublicKey) null, null);

            //devSaveKey();

            System.out.println(keys.getPrivate());
            System.out.println(keys.getPublic());

            System.out.println(Arrays.toString(keys.getPrivate().getEncoded()));
            System.out.println(Arrays.toString(keys.getPublic().getEncoded())); //*/

            String message = "Pizza boop shark";
            System.out.println(message);
            byte[] msg = encryptMessagePriv(message);

            System.out.println(msg);
            System.out.println(Arrays.toString(msg));

            String ans = decryptMessagePub(msg, keys.getPublic());
            System.out.println(ans);

        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }

    private void devSaveKey() {
        byte[] key = keys.getPublic().getEncoded();
        FileOutputStream keyfos = null;

        try {
            keyfos = new FileOutputStream("public");
            keyfos.write(key);
            keyfos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        key = keys.getPrivate().getEncoded();

        try {
            keyfos = new FileOutputStream("private");
            keyfos.write(key);
            keyfos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
