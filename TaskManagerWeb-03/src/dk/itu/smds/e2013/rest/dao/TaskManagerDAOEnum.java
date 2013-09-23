/**
 * 
 */
package dk.itu.smds.e2013.rest.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletContext;
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

		// TODO: Implement method for saving Task manager.

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