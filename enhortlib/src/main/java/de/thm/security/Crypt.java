package de.thm.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Crypt {

    public static SealedObject encrypt(Serializable object, String secret) {

        try {
            Key key = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");

            c.init(Cipher.ENCRYPT_MODE, key);

            return new SealedObject(object, c);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | IOException | InvalidKeyException e) {
            System.err.println("Error encryption: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public static Object decrypt(SealedObject container, String secret) {

        try {
            Key key = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);

            return container.getObject(c);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException e) {
            System.err.println("Error in decryption: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
