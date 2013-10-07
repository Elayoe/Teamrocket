/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.serialization.common;

/**
 *
 * @author rao
 */
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author rao
 */
public class TaskSerializer {

    public static Task DeserializeTask(String taskXml) throws JAXBException {

        // create a context object for Student Class
        JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);


        StringReader reader = new StringReader(taskXml);
        // Call unmarshal method to deserialize task Xml into object.
        Task task = (Task) jaxbContext.createUnmarshaller().unmarshal(reader);


        return task;


    }

    
    

    public static List<Task> DeserializeTaskListRevised(String taskListXml) throws JAXBException {

        // create a context object for Student Class
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);


        StringReader reader = new StringReader(taskListXml);
        // Call unmarshal method to deserialize task Xml into object.
        List<Task> tasklist = ((TaskList) jaxbContext.createUnmarshaller().unmarshal(reader)).list;


        return tasklist;

    }

    public static String SerializeTaskList(List<Task> tasklist) throws JAXBException {


        // create a context object for Student Class
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);

        TaskList tl = new TaskList(tasklist);

        // Serialize university object into xml.

        StringWriter writer = new StringWriter();

        // We can use the same context object, as it knows how to 
        //serialize or deserialize University class.
        jaxbContext.createMarshaller().marshal(tl, writer);



        return writer.toString();

    }

    public static List<Task> DeserializeTaskList(String taskListXml) throws JAXBException, SAXException, IOException, JDOMException {


        StringReader reader = new StringReader(taskListXml);


        InputSource source = new InputSource(reader);

        SAXBuilder builder = new SAXBuilder();


        Document doc = builder.build(source);


        Element rootElement = doc.getRootElement();

        List<Element> children = rootElement.getChildren();

        List<Task> tasklist = new ArrayList<Task>();

        for (int index = 0; index < children.size(); index++) {

            Element child = children.get(index);


            String taskXml = GetElementXml(child.clone());

            Task DeserializeTask = DeserializeTask(taskXml);

            tasklist.add(DeserializeTask);




        }




        return tasklist;
    }

    private static String GetElementXml(Element element) throws IOException {

        Document xmlDoc = new Document();

        StringWriter writer = new StringWriter();

        xmlDoc.addContent(element);

        new XMLOutputter().output(xmlDoc, writer);



        return writer.toString();

    }
}
