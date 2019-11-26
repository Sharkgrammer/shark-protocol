package crypto;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptManager {

    private String cipherInstance = "RSA/ECB/PKCS1Padding";
    private KeyPair keys = null;

    public void run(){
        try {
            setKeys((PublicKey) null, null);

            devSaveKey();

            System.out.println(keys.getPrivate());
            System.out.println(keys.getPublic());

            System.out.println(Arrays.toString(keys.getPrivate().getEncoded()));
            System.out.println(Arrays.toString(keys.getPublic().getEncoded())); //*/

            String message = "Pizza boop shark you motherfuckers";
            System.out.println(message);
            byte[] msg = encryptMessage(message);

            System.out.println(msg);
            System.out.println(Arrays.toString(msg));

            String ans =  decryptMessage(msg, keys.getPublic());
            System.out.println(ans);

        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }

    public void setKeys(byte[] pub, byte[] priv){
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
            if (priv != null) privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priv));
            if (pub != null) publicKey = kf.generatePublic(new X509EncodedKeySpec(pub));

        }catch (Exception e){
            System.out.println(e.toString());
        }

        setKeys(publicKey, privateKey);
    }

    public void setKeys(PublicKey pub, PrivateKey priv){

        if (pub == null && priv == null){
            CryptGenerate gen = new CryptGenerate();
            keys = gen.genKeys(null, null);
        }else{
            keys = new KeyPair(pub, priv);
        }

    }

    public boolean doKeysExist(){
        return keys != null;
    }

    public PrivateKey getPrivateKey(){
        if (keys != null){
            return keys.getPrivate();
        }else{
            return null;
        }
    }

    public PublicKey getPublicKey(){
        if (keys != null){
            return keys.getPublic();
        }else{
            return null;
        }
    }


    public String decryptMessage(byte[] msg, byte[] pub){

        try{

            KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
            return decryptMessage(msg, kf.generatePublic(new X509EncodedKeySpec(pub)));

        }catch (Exception e){
            System.out.println(e.toString());
            return null;
        }
    }

    public String decryptMessage(byte[] msg, PublicKey pub){
        String result = null;

        try{
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.DECRYPT_MODE, pub);
            result = new String(cipher.doFinal(msg), "utf-8");
        }catch (Exception e){
            System.out.println(e.toString());
        }

        return result;
    }

    public byte[] encryptMessage(String msg){
        byte[] result = null;

        try{
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
            result = cipher.doFinal(msg.getBytes());
        }catch (Exception e){
            System.out.println(e.toString());
        }

        return result;
    }

    private void devSaveKey(){
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
