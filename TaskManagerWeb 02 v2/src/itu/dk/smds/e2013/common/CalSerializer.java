package itu.dk.smds.e2013.common;

import java.io.*;
import java.util.ArrayList;
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

	public static ArrayList<Task> getList(String id) {
		try {
			Object obj = new Object();

			// Get path to the task-manager-xml.xml using relative path.
			String path = "src/resources/task-manager-xml.xml";

			// InputStream stream = obj.getClass().getResourceAsStream(path);

			// create an instance context class, to serialize/deserialize.
			JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

			// Create a file input stream for the cal Xml.
			FileInputStream stream = new FileInputStream(path);

			// Deserialize task xml into java objects.
			Cal cal = (Cal) jaxbContext.createUnmarshaller().unmarshal(stream);

			// Iterate through the collection of student object and print each
			// student object in the form of Xml to console.
			ListIterator<Task> listIterator = cal.tasks.listIterator();

			ArrayList<Task> myList = new ArrayList<Task>();
			ArrayList<Task> returnList = new ArrayList<Task>();

			while (listIterator.hasNext()) {
				myList.add(listIterator.next());
			}

			for (int i = 0; i < myList.size(); i++) {

				if (myList.get(i).id.equals(id)) {
					returnList.add(myList.get(i));
				}
			}

			return returnList;
		} catch (Exception e) {
			return null;
		}
	}

	public static String deleteTask(String id) {
		try {
			Object obj = new Object();

			// Get path to the task-manager-xml.xml using relative path.
			String path = "src/resources/task-manager-xml.xml";

			// InputStream stream = obj.getClass().getResourceAsStream(path);

			// create an instance context class, to serialize/deserialize.
			JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

			// Create a file input stream for the cal Xml.
			FileInputStream stream = new FileInputStream(path);

			// Deserialize task xml into java objects.
			Cal cal = (Cal) jaxbContext.createUnmarshaller().unmarshal(stream);

			// Iterate through the collection of student object and print each
			// student object in the form of Xml to console.
			ListIterator<Task> listIterator = cal.tasks.listIterator();

			// Delete Object
			ArrayList<Task> myList = new ArrayList<Task>();

			while (listIterator.hasNext()) {
				myList.add(listIterator.next());
			}

			for (int i = 0; i < myList.size(); i++) {

				if (myList.get(i).id.equals(id)) {
					myList.remove(i);
				}

				System.out.println("Printing task objects serailized into Xml");

				while (listIterator.hasNext()) {

					PrintTaskObject(listIterator.next());

				}

				// Serialize cal object into xml.

				StringWriter writer = new StringWriter();

				// We can use the same context object, as it knows how to
				// serialize or deserialize Cal class.
				jaxbContext.createMarshaller().marshal(cal, writer);

				System.out
						.println("Printing serialized cal Xml before saving into file!");

				// Print the serialized Xml to Console.
				System.out.println(writer.toString());

				// Finally save the Xml back to the file.
				SaveFile(writer.toString(), path);

				return id;

			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String postTask(Task myTask) {
		try {

			Object obj = new Object();

			// Get path to the task-manager-xml.xml using relative path.
			String path = "src/resources/task-manager-xml.xml";

			// InputStream stream = obj.getClass().getResourceAsStream(path);

			// create an instance context class, to serialize/deserialize.
			JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

			// Create a file input stream for the cal Xml.
			FileInputStream stream = new FileInputStream(path);

			// Deserialize task xml into java objects.
			Cal cal = (Cal) jaxbContext.createUnmarshaller().unmarshal(stream);

			// Iterate through the collection of student object and print each
			// student object in the form of Xml to console.
			ListIterator<Task> listIterator = cal.tasks.listIterator();

			listIterator.add(myTask);

			System.out.println("Printing task objects serailized into Xml");

			while (listIterator.hasNext()) {

				PrintTaskObject(listIterator.next());

			}

			// Serialize cal object into xml.

			StringWriter writer = new StringWriter();

			// We can use the same context object, as it knows how to
			// serialize or deserialize Cal class.
			jaxbContext.createMarshaller().marshal(cal, writer);

			System.out
					.println("Printing serialized cal Xml before saving into file!");

			// Print the serialized Xml to Console.
			System.out.println(writer.toString());

			// Finally save the Xml back to the file.
			SaveFile(writer.toString(), path);

			return "Task POSTED";

		} catch (JAXBException | IOException ex) {

			Logger.getLogger(CalSerializer.class.getName()).log(Level.SEVERE,
					null, ex);

			return "Task not POSTED";
		}

	}

	public static String putTask(Task myTask) {

		try {
			Object obj = new Object();

			// Get path to the task-manager-xml.xml using relative path.
			String path = "src/resources/task-manager-xml.xml";

			// InputStream stream = obj.getClass().getResourceAsStream(path);

			// create an instance context class, to serialize/deserialize.
			JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

			// Create a file input stream for the cal Xml.
			FileInputStream stream = new FileInputStream(path);

			// Deserialize task xml into java objects.
			Cal cal = (Cal) jaxbContext.createUnmarshaller().unmarshal(stream);

			// Iterate through the collection of student object and print each
			// student object in the form of Xml to console.
			ListIterator<Task> listIterator = cal.tasks.listIterator();

			// Delete Object
			ArrayList<Task> myList = new ArrayList<Task>();

			while (listIterator.hasNext()) {
				myList.add(listIterator.next());
			}

			for (int i = 0; i < myList.size(); i++) {

				if (myList.get(i).equals(myTask)) {
					myList.remove(i);
					myList.add(myTask);
				}
			}

			System.out.println("Printing task objects serailized into Xml");

			for (int i = 0; i < myList.size(); i++) {

				PrintTaskObject(myList.get(i));

			}

			// Serialize cal object into xml.

			StringWriter writer = new StringWriter();

			// We can use the same context object, as it knows how to
			// serialize or deserialize Cal class.
			jaxbContext.createMarshaller().marshal(cal, writer);
	

			System.out
					.println("Printing serialized cal Xml before saving into file!");

			// Print the serialized Xml to Console.
			System.out.println(writer.toString());

			// Finally save the Xml back to the file.
			SaveFile(writer.toString(), path);

			return "Task was put";
		} catch (Exception e) {
			return "Task was not put";
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
			Logger.getLogger(CalSerializer.class.getName()).log(Level.SEVERE,
					null, ex);
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