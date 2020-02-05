package crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class CryptGenerate {

    public KeyPair genKeys(Integer size, String mode){
        KeyPair pair = null;
        if (size == null){
            size = 2048;
        }

        if (mode == null){
            mode = "RSA";
        }

        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(mode);

            SecureRandom random = new SecureRandom();
            keyGen.initialize(size, random);

            pair = keyGen.generateKeyPair();
        }catch(Exception e){
            System.out.println(e.toString());
        }

        return pair;
    }

    public String getUserKey(int size){
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase(Locale.ROOT);
        final String digits = "0123456789";
        final String complete = upper + lower + digits;
        Random rand = new SecureRandom();
        char[] symbolArray = complete.toCharArray();
        char[] buf = new char[size];

        for (int i = 0; i < buf.length; i++){
            buf[i] = symbolArray[rand.nextInt(symbolArray.length)];
        }

        return new String(buf);

    }


}
