/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.jgroups;

import dk.itu.smds.e2013.jgroups.common.TaskProvider;
import dk.itu.smds.e2013.serialization.common.Envelope;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskSerializer;

import java.beans.DesignMode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import sun.security.krb5.internal.crypto.Des;

/**
 * 
 * @author Rao
 */
public class TaskReceiver2 extends ReceiverAdapter {

	final List<String> state = new LinkedList<>();
	protected TaskProvider provider;
	private JChannel channelTasks;
	private String prefix = "[receiver]: ";

	public String hostProcessAddress = "";

	public TaskReceiver2(TaskProvider provider, JChannel channel) {

		this.provider = provider;

		channelTasks = channel;

	}

	@Override
	public void receive(Message msg) {
		// We know that we will always get Xml.
		Envelope envelope = new Envelope();

		String envelopeXml = msg.getObject().toString();

		String messageSourceId = msg.getSrc().toString();

		Envelope DeserializeEnvelope;

		try {
			DeserializeEnvelope = TaskSerializer
					.DeserializeEnvelope(envelopeXml);
		} catch (JAXBException ex) {
			System.out.println(prefix
					+ "Failed to deserialize envelope Xml. Error message" + ex);

			return;
		}

		String lock = DeserializeEnvelope.lock;
		String command = DeserializeEnvelope.command;
		String taskId = DeserializeEnvelope.taskId;

		if (messageSourceId.equals(hostProcessAddress))
			return;

		// System.out.println("Message with command " + command + ", lock " +
		// lock
		// + ", task id " + taskId + " received by " + messageSourceId);

		switch (lock) {
		case "commit":
			System.out.println("Received commit from " + messageSourceId);
			if (command.equals("execute")) {
				SetTaskAsExecuted(taskId);
			} else {
				SetTaskAsRequested(taskId);
			}

			break;

		case "requestLock":

			System.out.println("Got a request from\t\t" + messageSourceId
					+ " to " + command + " " + taskId);

			if (TaskManagerServer.hashTable.contains(taskId)) {

				envelope.initiator = messageSourceId;
				envelope.lock = "denyLock";
				envelope.command = command;
				envelope.taskId = taskId;

				System.out.println("Sent DenyLock back");
				WriteEnvelopeToChannel(envelope, channelTasks);
			} else {

				envelope.initiator = messageSourceId;
				envelope.lock = "grantLock";
				envelope.command = command;
				envelope.taskId = taskId;

				System.out.println("Sent GrantLock back");
				WriteEnvelopeToChannel(envelope, channelTasks);
			}

			break;

		case "grantLock":

			System.out.println("GrantLock recieved from\t\t" + messageSourceId
					+ " to " + command + " " + taskId);
			try {
				int number = TaskManagerServer.hashTable.get(taskId) - 1;

				TaskManagerServer.hashTable.put(taskId, number);

				// If we got all grants, set the task to executed and commit
				if (number < 1) {

					// Delete entry from memory
					TaskManagerServer.hashTable.remove(taskId);

					if (command.equals("execute")) {
						SetTaskAsExecuted(taskId);
					} else {
						SetTaskAsRequested(taskId);
					}

					envelope.initiator = messageSourceId;
					envelope.lock = "commit";
					envelope.command = command;
					envelope.taskId = taskId;

					System.out.println("Send commit");
					WriteEnvelopeToChannel(envelope, channelTasks);
				}
			} catch (NullPointerException e) {
				System.out.println("Task was denied");
			}

			break;

		case "denyLock":
		    TaskManagerServer.hashTable.remove(taskId);
			System.out.println("Received DenyLock by " + messageSourceId);
			break;
		}
	}

	@Override
	public void getState(OutputStream output) throws Exception {

	}

	@Override
	public void setState(InputStream input) throws Exception {

	}

	@Override
	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}

	private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) {

		try {
			String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);

			Message msg = new Message(null, null, envelopeXml);

			channel.send(msg);

		} catch (Exception ex) {
			System.out
					.println("Failed to write task object to the channel. Error message:"
							+ ex.getMessage());
		}

	}

	private Task GetTaskWithId(String taskId) {

		for (int index = 0; index < provider.TaskManagerInstance.tasks.size(); index++) {
			if (provider.TaskManagerInstance.tasks.get(index).id.equals(taskId)) {
				return provider.TaskManagerInstance.tasks.get(index);
			}

		}
		return null;
		//

	}

	public void SetTaskAsExecuted(String taskId) {

		Task task = GetTaskWithId(taskId);

		if (task != null) {

			task.status = "executed";
			task.required = "false";

			try {
				// Save change
				provider.PersistTaskManager();

				System.out.println("The task with Id:\t\t" + taskId
						+ " executed successfully! ");

			} catch (JAXBException ex) {
				System.out.println(prefix
						+ "Failed to persist envelope Xml. Error message" + ex);
			} catch (IOException ex) {
				System.out.println(prefix
						+ "Failed to persist envelope Xml. Error message" + ex);
			}

		} else {
			System.out.println("Error: Task\t\t\t" + taskId + " NOT found! ");

		}
	}

	public void SetTaskAsRequested(String taskId) {

		Task task = GetTaskWithId(taskId);

		if (task != null) {

			task.required = "true";

			try {
				// Save change
				provider.PersistTaskManager();

				System.out.println("The task with Id:\t\t" + taskId
						+ " marked as required successfully!");
			} catch (JAXBException ex) {
				System.out.println(prefix
						+ "Failed to persist envelope Xml. Error message" + ex);
			} catch (IOException ex) {
				System.out.println(prefix
						+ "Failed to persist envelope Xml. Error message" + ex);
			}
		} else {

			System.out.println("Error: Task\t\t\t" + taskId + " NOT found! ");

		}

	}

}
