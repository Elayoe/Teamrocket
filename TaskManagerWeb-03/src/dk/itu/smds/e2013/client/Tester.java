package dk.itu.smds.e2013.client;

import java.net.URI;

import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import dk.itu.smds.e2013.rest.dao.TaskLoaderServlet;
import dk.itu.smds.e2013.rest.dao.TaskManagerDAOEnum;
import dk.itu.smds.e2013.serialization.*;
import dk.itu.smds.e2013.serialization.common.Task;

public class Tester {
	public static void main(String[] args) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());

		// create one task
		Task task = new Task("prereport-02", "Exam Pre Report", "15-11-2013", "Not executed", "True", "Some description here...", "Team-Rocket");
		
		ClientResponse response = service.path("rest").path("/taskmgr/tasks/")
		        .path(task.id).accept(MediaType.APPLICATION_XML)
		        .put(ClientResponse.class, task);
		
		System.out.println(response.getStatus());
		
		System.out.println(TaskManagerDAOEnum.INSTANCE.getTaskManager());
		//TaskManagerDAOEnum.INSTANCE.addTask(task);

	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/TaskManagerWeb-03/").build();
	}
} 