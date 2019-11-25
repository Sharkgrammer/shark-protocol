package util;

import crypto.CryptManager;

import java.security.PublicKey;

public class UserHolder {

    private byte[] userID;
    private CryptManager manager;

    public UserHolder(byte[] ID, byte[] publicKey, byte[] privateKey){
        this.userID = ID;

        manager  = new CryptManager();
        if (publicKey == null){
            manager.setKeys((PublicKey) null, null);
        }else{
            manager.setKeys(publicKey, privateKey);
        }
    }

    public byte[] getUserID() {
        return userID;
    }

    public void setUserID(byte[] userID) {
        this.userID = userID;
    }

    public CryptManager getManager() {
        return manager;
    }

    public void setManager(CryptManager manager) {
        this.manager = manager;
    }
}
