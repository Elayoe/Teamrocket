/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.serialization.common;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 *
 * @author rao
 */
public class SerializationTester {

    public static void main(String[] args)  {
        // Start Task manager.
        
        String envelopeXml = "<envelope><command>add</command><data><task id='tch-01' name='teach lect 01' date='03-09-2012' status='executed'><description> Teaching on  Distributed Systems and System Model </description><attendants>hilde</attendants></task> </data> </envelope>";
        try {
            
            Envelope DeserializeEnvelope = TaskSerializer.DeserializeEnvelope(envelopeXml);
            
            System.out.println(DeserializeEnvelope.command);
            
            System.out.println(DeserializeEnvelope.data.toString());
            
            
            String xml = TaskSerializer.SerializeEnvelope(DeserializeEnvelope);
            
            DeserializeEnvelope = TaskSerializer.DeserializeEnvelope(xml);
            
            System.out.println(DeserializeEnvelope.data.get(0).id);
            
        } catch (JAXBException ex) {
            System.out.println(ex);
        }
        
        
        
        
    }
}
