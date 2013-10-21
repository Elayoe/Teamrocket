/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.serialization.common;

import java.io.*;

/**
 *
 * @author rao
 */
public class Utilities {

    public static void PersistXml(String xml, String path) throws IOException {


        File file = new File(path);
        
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(xml);
        }


    }

    public static String LoadXml(String path) throws IOException {

        InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
        StringBuilder builder;
        
        try (BufferedReader bufferReader = new BufferedReader(reader)) {
            builder = new StringBuilder();
            String line;
            while ((line = bufferReader.readLine()) != null) {
                builder.append(line);
            }
        }

        return builder.toString();

    }
}