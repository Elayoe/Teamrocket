/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * @author rao
 */
public class DESEncryptionHelper {

    
    private static final String Encryption_Algorithm = "DES";

    public static byte[] encryptMessage(byte[] key, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {


        DESKeySpec keySpec = new DESKeySpec(key);

        SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance(Encryption_Algorithm);

        Cipher cipher = Cipher.getInstance(Encryption_Algorithm);

        SecretKey secretkey = KeyFactory.generateSecret(keySpec);

        cipher.init(Cipher.ENCRYPT_MODE, secretkey);
        
        byte[] encryptedBytes = cipher.doFinal(data);

        return encryptedBytes;



    }

    public static byte[] decryptMessage(byte[] key, byte[] cryptdata) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {


        DESKeySpec keySpec = new DESKeySpec(key);

        SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance(Encryption_Algorithm);

        Cipher cipher = Cipher.getInstance(Encryption_Algorithm);

        SecretKey secretkey = KeyFactory.generateSecret(keySpec);


        cipher.init(Cipher.DECRYPT_MODE, secretkey);

        byte[] plainTextbytes = cipher.doFinal(cryptdata);


        return plainTextbytes;
    }

    
}
