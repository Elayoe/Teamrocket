/**
 * 
 */
package dk.itu.smds.e2013.rest.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import dk.itu.smds.e2013.serialization.common.*;

/**
 * @author rao
 * 
 */
public enum TaskManagerDAOEnum {

	INSTANCE;

	private TaskManager taskManager = null;

	// Get reference to to the servlet context
	ServletContext context;


	public void save() {

		try {

			Object obj = new Object();

			// Get path to the task-manager-xml.xml using relative path.
			String somePath = new File(".").getAbsolutePath();
			String path = somePath.substring(0, somePath.length()-2) + "/WebContent/WEB-INF/resources/task-manager-xml.xml";
			System.out.println(path);
			//String path = /Users/Thomas/Dropbox/ITU/3. semester/Mobile and Distributed Systems/TeamRocket/TaskManagerWeb-03/WebContent/WEB-INF/resources/task-manager-xml.xml

			// create an instance context class, to serialize/deserialize.
			JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

			// Serialize cal object into xml.

			StringWriter writer = new StringWriter();

			// We can use the same context object, as it knows how to
			// serialize or deserialize Cal class.
			jaxbContext.createMarshaller().marshal(taskManager, writer);

			System.out
					.println("Printing serialized cal Xml before saving into file!");

			// Print the serialized Xml to Console.
			System.out.println(writer.toString());

			// Finally save the Xml back to the file.
			SaveFile(writer.toString(), path);

		} catch (JAXBException | IOException ex) {
			Logger.getLogger(TaskManagerDAOEnum.class.getName()).log(Level.SEVERE,
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
	
	public void setTaskManager(TaskManager taskMgr){ 
		
		taskManager = taskMgr;
		
	}
	

	public TaskManager getTaskManager(){ 
		
		return taskManager;
		
	}


	public void addTask(Task task) {

		// First delete task if it exits
		removeTask(task.id);
		
		synchronized (this) {
			taskManager.tasks.add(task);
		}
		
		save();

	}

	public Boolean removeTask(String taskId) {

		Task task = getTask(taskId);

		if (task != null) {

			synchronized (this) {
				taskManager.tasks.remove(task);
			}

			return true;
		}

		return false;
	}
	

	public Boolean updateTask(Task newTask) {

		Task task = getTask(newTask.id);

		if (task != null) {

			synchronized (this) {
				
				taskManager.tasks.remove(task);
				
				taskManager.tasks.add(task);
			}

			return true;
		}

		return false;
	}

	

	public Task getTask(String taskId) {

		ListIterator<Task> listIterator = taskManager.tasks.listIterator();

		while (listIterator.hasNext()) {
			Task nextTask = listIterator.next();

			if (nextTask.id.equals(taskId)) {

				return nextTask;
			}
		}

		return null;

	}
	
	
	public List<Task> getAllTasks(){ 
		
		List<Task> tasks = new ArrayList<Task>();
		
		Task task = new Task();
		
		task.name = "rao";
		
		tasks.add(task);

		//return tasks;
		
	
		
		return taskManager.tasks;
		
	}
	
	
		
	

}