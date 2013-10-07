/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.serialization.common;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rao
 */
@XmlRootElement(name = "cal")
public class TaskManager implements Serializable {

    @XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task")
    public List<Task> tasks;

    
    public static void saveTaskManager(TaskManager taskManager, String path) {
        try {
            // Finally save the Xml, so that we can persist the tasks.
            StringWriter writer = new StringWriter();

            JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

            jaxbContext.createMarshaller().marshal(taskManager, writer);


            Utilities.PersistXml(writer.toString(), path);


        } catch (Exception ex) {
            System.out.println(String.format("Failed to save task manager Xml at filepath : '%1$s'. Error messages:  '%2$s'",
                    path, ex.getMessage()));

        }

    }
}
