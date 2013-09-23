package dk.itu.smds.e2013.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import dk.itu.smds.e2013.rest.dao.TaskManagerDAOEnum;
import dk.itu.smds.e2013.serialization.common.Task;

public class TaskResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String id;
  public TaskResource(UriInfo uriInfo, Request request, String id) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.id = id;
  }
  
  //Application integration     
  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Task getTask() {
    Task task = TaskManagerDAOEnum.INSTANCE.getTask(id);
    if(task==null)
      throw new RuntimeException("Get: Task with " + id +  " not found");
    return task;
  }
  
  // For the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public Task getTodoHTML() {
    Task task = TaskManagerDAOEnum.INSTANCE.getTask(id);
    if(task==null)
      throw new RuntimeException("Get: Task with " + id +  " not found");
    return task;
  }
  
  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putTask(JAXBElement<Task> task) {
    Task existing = task.getValue();
    
    if(existing == null){
    	TaskManagerDAOEnum.INSTANCE.addTask(existing);
    } else{
    	TaskManagerDAOEnum.INSTANCE.updateTask(existing);
    }
    
    Response response = Response.noContent().build();
    return response;
  }
  
  @DELETE
  public void deleteTask() {
    Boolean c = TaskManagerDAOEnum.INSTANCE.removeTask(id);
    if(c==false)
      throw new RuntimeException("Delete: Task with " + id +  " not found");
  }
} 