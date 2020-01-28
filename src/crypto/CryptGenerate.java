package crypto;

import java.security.*;

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


}
