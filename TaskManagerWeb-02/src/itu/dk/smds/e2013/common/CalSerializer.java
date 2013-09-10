package itu.dk.smds.e2013.common;

import java.io.*;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 *
 * 
 */
public class CalSerializer {

    public static void main(String args[]) throws IOException {
        try {


        	Object obj = new Object();
        	
        	// Get path to the task-manager-xml.xml using relative path.
        	String path = obj.getClass().getResource("../WebContent/WEB-INF/lib/task-manager-xml.xml").getPath();
        	
        	InputStream stream = obj.getClass().getResourceAsStream("../WebContent/WEB-INF/lib/task-manager-xml.xml");
        	
        	
            // create an instance context class, to serialize/deserialize.
            JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

            // Create a file input stream for the cal Xml.
            //FileInputStream stream = new FileInputStream(path);

            // Deserialize task xml into java objects.
            Cal cal = (Cal) jaxbContext.createUnmarshaller().unmarshal(stream);


            // Iterate through the collection of student object and print each student object in the form of Xml to console.
            ListIterator<Task> listIterator = cal.tasks.listIterator();
            
            System.out.println("Printing task objects serailized into Xml");
            
            
            while (listIterator.hasNext()) {

                PrintTaskObject(listIterator.next());

            }

            // Serialize cal object into xml.
            
            StringWriter writer = new StringWriter();

            // We can use the same context object, as it knows how to 
            //serialize or deserialize Cal class.
            jaxbContext.createMarshaller().marshal(cal, writer);

            
            System.out.println("Printing serialized cal Xml before saving into file!");
            
            // Print the serialized Xml to Console.
            System.out.println(writer.toString());
            
            
            // Finally save the Xml back to the file.
            SaveFile(writer.toString(), path);



        } catch (JAXBException ex) {
            Logger.getLogger(CalSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }





    }

    private static void PrintTaskObject(Task task) {

        try {


            StringWriter writer = new StringWriter();

            // create a context object for Task Class
            JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);

            // Call marshal method to serialize student object into Xml
            jaxbContext.createMarshaller().marshal(task, writer);

            System.out.println(writer.toString());

        } catch (JAXBException ex) {
            Logger.getLogger(CalSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

    private static void SaveFile(String xml, String path) throws IOException {


        File file = new File(path);

        // create a bufferedwriter to write Xml
        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        output.write(xml);

        output.close();



    }
}