package dk.itu.smds.e2013.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import dk.itu.smds.e2013.serialization.*;
import dk.itu.smds.e2013.serialization.common.Task;

public class Tester {
  public static void main(String[] args) {
    ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    WebResource service = client.resource(getBaseURI());
    // create one task
    Task task = new Task("prereport-01", "Exam Pre Report", "15-11-2013", "Not executed", "True", "Some description here...", "Team-Rocket");
    ClientResponse response = service.path("rest").path("/taskmgr/tasks")
        .path(task.id)
        .put(ClientResponse.class, task);
    // Return code should be 201 == created resource
    System.out.println(response.getStatus());
    // Get the tasks
    System.out.println(service.path("rest").path("/taskmgr/tasks")
        .accept(MediaType.TEXT_XML).get(String.class));
    // Get JSON for application
    System.out.println(service.path("rest").path("/taskmgr/tasks")
        .accept(MediaType.APPLICATION_JSON).get(String.class));
    // Get XML for application
    System.out.println(service.path("rest").path("/taskmgr/tasks")
        .accept(MediaType.APPLICATION_XML).get(String.class));

    // Get the task with id handin-01
    System.out.println(service.path("rest").path("/taskmgr/tasks/handin-01")
        .accept(MediaType.APPLICATION_XML).get(String.class));
    // Delete Task with id handin-01
    service.path("rest").path("/taskmgr/tasks/handin-01").delete();
    // Get the all todos, id 1 should be deleted
    System.out.println(service.path("rest").path("todos")
        .accept(MediaType.APPLICATION_XML).get(String.class));

    // create a Task
    Form form = new Form();
    form.add("id", "4");
    form.add("summary", "Demonstration of the client lib for forms");
    response = service.path("rest").path("todos")
        .type(MediaType.APPLICATION_FORM_URLENCODED)
        .post(ClientResponse.class, form);
    System.out.println("Form response " + response.getEntity(String.class));
    // Get the all task, id 4 should be created
    System.out.println(service.path("rest").path("todos")
        .accept(MediaType.APPLICATION_XML).get(String.class));

  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/de.vogella.jersey.todo").build();
  }
} 