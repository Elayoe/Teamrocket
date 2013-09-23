package dk.itu.smds.e2013.rest.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import dk.itu.smds.e2013.rest.dao.TaskManagerDAOEnum;
import dk.itu.smds.e2013.serialization.common.Task;

//Will map the resource to the URL todos
@Path("/tasks")
public class TasksResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;


	// Return the list of todos to the user in the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<Task> getTasksBrowser() {
		List<Task> todos = new ArrayList<Task>();
		todos.addAll(TaskManagerDAOEnum.INSTANCE.getAllTasks());
		return todos; 
	}

	// Return the list of todos for applications
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Task> getTasks() {
		List<Task> todos = new ArrayList<Task>();
		todos.addAll(TaskManagerDAOEnum.INSTANCE.getAllTasks());
		return todos; 
	}


	// retuns the number of tasks
	// Use http://localhost:8080/de.vogella.jersey.todo/rest/todos/count
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = TaskManagerDAOEnum.INSTANCE.getAllTasks().size();
		return String.valueOf(count);
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newTask(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("date") String date,
			@FormParam("status") String status,
			@FormParam("required") String required,
			@FormParam("description") String description,
			@FormParam("attendants") String attendants,
			@Context HttpServletResponse servletResponse) throws IOException {
		Task task = new Task(id,name,date,status,required,description,attendants);
		/*if (description!=null){
			task.setDescription(description);
		}*/
		TaskManagerDAOEnum.INSTANCE.updateTask(task);

		servletResponse.sendRedirect("../create_task.html");
	}


	// Defines that the next path parameter after todos is
	// treated as a parameter and passed to the TodoResources
	// Allows to type http://localhost:8080/de.vogella.jersey.todo/rest/todos/1
	// 1 will be treaded as parameter todo and passed to TodoResource
	@Path("{task}")
	public TaskResource getTask(@PathParam("task") String id) {
		return new TaskResource(uriInfo, request, id);
	}
}
