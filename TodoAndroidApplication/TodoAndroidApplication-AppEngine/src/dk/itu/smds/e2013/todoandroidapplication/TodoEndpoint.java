package dk.itu.smds.e2013.todoandroidapplication;

import dk.itu.smds.e2013.todoandroidapplication.EMF;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "todoendpoint", namespace = @ApiNamespace(ownerDomain = "itu.dk", ownerName = "itu.dk", packagePath = "smds.e2013.todoandroidapplication"))
public class TodoEndpoint {

	// Rao: 20131103 Added necessary parameters for sending the Todo items to
	// devises.

	/*
	 * TODO: Fill this in with the server key that you've obtained from the API
	 * Console (https://code.google.com/apis/console). This is required for
	 * using Google Cloud Messaging from your AppEngine application even if you
	 * are using a App Engine's local development server.
	 */
	private static final String API_KEY = "AIzaSyBfGwDhIKnjEP98gjZzAW7jSkDrEMw_JKw";

	private static final DeviceInfoEndpoint endpoint = new DeviceInfoEndpoint();

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listTodo")
	public CollectionResponse<Todo> listTodo(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Todo> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Todo as Todo");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Todo>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (Todo obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Todo> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getTodo")
	public Todo getTodo(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Todo todo = null;
		try {
			todo = mgr.find(Todo.class, id);
		} finally {
			mgr.close();
		}
		return todo;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param todo
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertTodo")
	public Todo insertTodo(Todo todo) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsTodo(todo)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(todo);
		} finally {
			mgr.close();
		}
		return todo;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param todo
	 *            the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateTodo")
	public Todo updateTodo(Todo todo) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsTodo(todo)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(todo);
		} finally {
			mgr.close();
		}
		return todo;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeTodo")
	public void removeTodo(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Todo todo = mgr.find(Todo.class, id);
			mgr.remove(todo);
		} finally {
			mgr.close();
		}
	}

	// Rao:20131103: New methods for sending Todo items.

	@ApiMethod(name = "sendTodo")
	public void sendTodo(@Named("id") String id,
			@Named("description") String description,
			@Named("summary") String summary) throws IOException {
		
	    Sender sender = new Sender(API_KEY);
	    // create a new Todo entity when and persist it
	    Todo todo = new Todo();
	    todo.setId(id);
	    todo.setDescription(description);
	    todo.setSummary(summary);
	    
	    EntityManager mgr = getEntityManager();
	    try {
	      mgr.persist(todo);
	    } finally {
	      mgr.close();
	    }

	    // ping a max of 10 registered devices
	    CollectionResponse<DeviceInfo> response = endpoint.listDeviceInfo(null,
	        10);
	    for (DeviceInfo deviceInfo : response.getItems()) {
	      doSendViaGcm(todo.getId(), sender, deviceInfo);
	    }

	}

	/**
	 * Sends the message using the Sender object to the registered device.
	 * 
	 * @param message
	 *            the message to be sent in the GCM ping to the device.
	 * @param sender
	 *            the Sender object to be used for ping,
	 * @param deviceInfo
	 *            the registration id of the device.
	 * @return Result the result of the ping.
	 */
	private static Result doSendViaGcm(String message, Sender sender,
			DeviceInfo deviceInfo) throws IOException {
		// Trim message if needed.
		if (message.length() > 1000) {
			message = message.substring(0, 1000) + "[...]";
		}

		// This message object is a Google Cloud Messaging object, it is NOT
		// related to the MessageData class
		//Message msg = new Message.Builder().addData("message", message).build();
		
		// Rao: 20131103 : add id of newly created todo item.
		Message msg = new Message.Builder().addData("new_todo_id", message).build();
		
		Result result = sender.send(msg, deviceInfo.getDeviceRegistrationID(),
				5);
		if (result.getMessageId() != null) {
			String canonicalRegId = result.getCanonicalRegistrationId();
			if (canonicalRegId != null) {
				endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
				deviceInfo.setDeviceRegistrationID(canonicalRegId);
				endpoint.insertDeviceInfo(deviceInfo);
			}
		} else {
			String error = result.getErrorCodeName();
			if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
				endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
			}
		}

		return result;
	}

	private boolean containsTodo(Todo todo) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {

			// Rao: 20131103: Fix for the null point exception for the
			// when accessing the insertTodo and listTodo methods over the REST
			// interface.
			if (todo.getKey() == null) {

				return false;
			}

			Todo item = mgr.find(Todo.class, todo.getKey());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
