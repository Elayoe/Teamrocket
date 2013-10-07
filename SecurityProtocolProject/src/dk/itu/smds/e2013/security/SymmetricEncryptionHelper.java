package dk.itu.smds.e2013.security;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package dk.itu.smds.e2013.security;
//
//import java.io.*;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//import javax.crypto.*;
//import javax.crypto.spec.DESKeySpec;
//
///**
// *
// * @author rao
// */
//public class SymmetricEncryptionHelper {
//
//    public static byte[] encryptMessage(String password, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IOException {
//
//
//        // Create Key
//        byte key[] = password.getBytes("UTF8");
//
//        DESKeySpec desKeySpec = new DESKeySpec(key);
//
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//
//        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//
//        // Create Cipher 
//        Cipher desCipher = Cipher.getInstance("DES");
//
//
//        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//        // Create outputstream
//        //FileOutputStream fos = new FileOutputStream("out.txt");
//
//        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//
//
//
//        //BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//        BufferedOutputStream bos = new BufferedOutputStream(byteStream);
//
//
//        CipherOutputStream cos = new CipherOutputStream(bos, desCipher);
//
//        ObjectOutputStream oos = new ObjectOutputStream(cos);
//
//        // Write data
//        oos.write(data);
//
//        oos.flush();
//
//        oos.close();
//
//
//        return byteStream.toByteArray();
//    }
//
//    public static byte[] decryptMessage(String password, byte[] cryptdata) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
//
//
//        // Create Key
//        byte key[] = password.getBytes("UTF8");
//
//        DESKeySpec desKeySpec = new DESKeySpec(key);
//
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//
//        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//
//        // Create Cipher
//        Cipher desCipher = Cipher.getInstance("DES");
//
//        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//
//
//        // Change cipher mode
//        desCipher.init(Cipher.DECRYPT_MODE, secretKey);
//        
//        byte[] doFinal = desCipher.doFinal(cryptdata);
//
//        // Create stream
//        //FileInputStream fis = new FileInputStream("out.txt");
//
////        ByteArrayInputStream byteStream = new ByteArrayInputStream(cryptdata);
////
////        BufferedInputStream bis = new BufferedInputStream(byteStream);
////
////        CipherInputStream cis = new CipherInputStream(bis, desCipher);
////
////        ObjectInputStream ois = new ObjectInputStream(cis);
////        // Read 
////        //String plaintext2 = ois.readUTF();
////        
////        
////        byte[] data = new byte[cis.available()];
////
////
////        ois.read(data);
////
////
////
////        ois.close();
//
//
//
//
//        return doFinal;
//    }
//}
