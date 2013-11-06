/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.jgroups.common;

import dk.itu.smds.e2013.serialization.common.TaskManager;
import dk.itu.smds.e2013.serialization.common.Utilities;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 *
 * @author rao
 */
public class TaskProvider {

    private String taskManagerPath;
    final public TaskManager TaskManagerInstance;

    public TaskProvider(String path) throws FileNotFoundException, JAXBException {

        taskManagerPath = path;

        FileInputStream stream = new FileInputStream(path);

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

        TaskManagerInstance = (TaskManager) jaxbContext.createUnmarshaller().unmarshal(stream);
        
        System.out.println("Task manager with " + TaskManagerInstance.tasks.size() + " tasks loaded!");


    }

    public void PersistTaskManager() throws JAXBException, IOException {
        // Finally save the Xml, so that we can persist the tasks.
        StringWriter writer = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

        synchronized (TaskManagerInstance) {

            jaxbContext.createMarshaller().marshal(TaskManagerInstance, writer);

            Utilities.PersistXml(writer.toString(), taskManagerPath);

        }

    }
}
