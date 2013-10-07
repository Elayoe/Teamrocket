/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *
 * @author rao
 * 
 * based on JSch - Examples - Shell.java
 * http://www.jcraft.com/jsch/examples/Shell.java.html
 * 
 */
public class SSHAuthenticationHelper {

    private static final String ITU_SSH_URL = "ssh.itu.dk";

    public static boolean Authenticate(String userName, String password) throws JSchException {


        JSch jsch = new JSch();


        try {
            Session session = jsch.getSession(userName, ITU_SSH_URL, 22);

            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();   // making a connection with timeout.

            session.disconnect();

            return true;

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return false;

        }
    }
}
