/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.jgroups.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jgroups.JChannel;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;

/**
 *
 * Code for programmatic creation of JChannel adopted from JGroups documentation
 * http://www.jgroups.org/manual-3.x/html/user-channel.html#UtilityClasses
 */
 


public class ChannelHelper {

    // Usage: pass the ipaddress and a port number to create channel.
    // For example you can pass  values: "127.0.0.1" and 51924 to create a JChannel on your local computer.
    //  
    // 
    public static JChannel getNewChannel(String ipAddress,int port) throws UnknownHostException, Exception {

        // The following code taken from an example on programmatic creation  from JGroups documentation. 
        // Therefore refer to http://www.jgroups.org/manual-3.x/html/user-channel.html#CreatingAChannel page
        // for documetaion and explanataion of the following code fragmnent. 
        
        JChannel ch = new JChannel(false);         // 1
        ProtocolStack stack = new ProtocolStack(); // 2
        ch.setProtocolStack(stack);              // 3
        
        UDP udp = new UDP();
        
        
        
        
        udp.setBindAddress(InetAddress.getByName(ipAddress));
        
        udp.setMulticastPort(port);
        
        stack.addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE2())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL()
                .setValue("timeout", 300000)
                .setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                //.addProtocol(new NAKACK())
                //.addProtocol(new UNICAST2())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER());       // 4
        

                stack.init();                            // 5

        
        
        stack.init();                            // 5


        return ch;
    }
}
