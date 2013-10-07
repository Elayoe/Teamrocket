/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security;

import java.io.IOException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author rao
 */
public class Utilities {

    public static String getBase64EncodedString(byte[] data) {


        BASE64Encoder base64encoder = new BASE64Encoder();

        return base64encoder.encode(data);
    }

    public static byte[] getBase64DecodedBytes(String encodedString) throws IOException {


        BASE64Decoder base64decoder = new BASE64Decoder();

        return base64decoder.decodeBuffer(encodedString);
    }

    public static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }
}
