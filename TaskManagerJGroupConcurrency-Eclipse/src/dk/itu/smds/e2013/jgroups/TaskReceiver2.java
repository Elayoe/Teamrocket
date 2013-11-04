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
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

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
        // We know that, we will always get Xml.
    	Envelope envelope = new Envelope();
    	
        String envelopeXml = msg.getObject().toString();

        String messageSourceId = msg.getSrc().toString();

        Envelope DeserializeEnvelope;

        try {
            DeserializeEnvelope = TaskSerializer.DeserializeEnvelope(envelopeXml);

            //System.out.println("Input envelope received: " + inputData);
            // We may get
        } catch (JAXBException ex) {
            System.out.println(prefix + "Failed to deserialize envelope Xml. Error message" + ex);

            return;
        }

        String sourceInfo = String.format("[ command: %s, source: %s ]:", DeserializeEnvelope.command, messageSourceId);

        if (DeserializeEnvelope.command.equals("execute")) {

            // Only handle the command if it is from other sources.
            if (!messageSourceId.equals(hostProcessAddress)) {

                Task task = GetTaskWithId(DeserializeEnvelope.taskId);

                if (task != null) {

                    task.status = "executed";

                    task.required = "false";

                    try {
                        provider.PersistTaskManager();
                    } catch (JAXBException ex) {
                        System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                    } catch (IOException ex) {
                        System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                    }

                    System.out.println(hostProcessAddress + sourceInfo + " Task :" + DeserializeEnvelope.taskId + " executed successfully! ");
                } else {

                    System.out.println(hostProcessAddress + sourceInfo + " Error: Task :" + DeserializeEnvelope.taskId + " NOT found! ");

                }

            }

        } else if (DeserializeEnvelope.command.equals("request")) {

            // Only handle the command if it is from other sources.
            if (!messageSourceId.equals(hostProcessAddress)) {

                Task task = GetTaskWithId(DeserializeEnvelope.taskId);

                if (task != null) {

                    task.required = "true";

                    try {
                        provider.PersistTaskManager();
                    } catch (JAXBException ex) {
                        System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                    } catch (IOException ex) {
                        System.out.println(prefix + "Failed to persist envelope Xml. Error message" + ex);
                    }

                    System.out.println(hostProcessAddress + sourceInfo + " Task :" + DeserializeEnvelope.taskId + " marked as required successfully! ");
                } else {

                    System.out.println(hostProcessAddress + sourceInfo + " Error: Task :" + DeserializeEnvelope.taskId + " NOT found! ");

                }

            }
        } else if(DeserializeEnvelope.lock.equals("requestLock")){
        	
        	// Only handle the command if it is from other sources.
            if (!messageSourceId.equals(hostProcessAddress)) {
            	
            	System.out.println("Got a request from " + hostProcessAddress);
            	
            	if(TaskManagerServer.hashTable.contains(DeserializeEnvelope.taskId)){
            		envelope.initiator = hostProcessAddress;

            		envelope.lock = "denyLock";

            		envelope.taskId = DeserializeEnvelope.taskId;

            		WriteEnvelopeToChannel(envelope, channelTasks);
            		
            	} else {
            		
            		TaskManagerServer.setHashTable(DeserializeEnvelope.taskId);
        		
            		envelope.initiator = hostProcessAddress;

            		envelope.lock = "grantLock";

            		envelope.taskId = DeserializeEnvelope.taskId;

            		WriteEnvelopeToChannel(envelope, channelTasks);
            	}
        	}
            
        } else if(DeserializeEnvelope.lock.equals("grantLock")){
        	
        	System.out.println("GrantLock recieved from " + hostProcessAddress);
        	
        	// Only handle the command if it is from other sources.
            if (!messageSourceId.equals(hostProcessAddress)) {
            	
            	TaskManagerServer.hashTable.put(DeserializeEnvelope.taskId, 
            			TaskManagerServer.hashTable.get(DeserializeEnvelope.taskId)-1);	
            }
            
        } else if(DeserializeEnvelope.lock.equals("denyLock")){
        	// abort loop
        	
        }

    }

    @Override
    public void getState(OutputStream output) throws Exception {

    }

    @Override
    public void setState(InputStream input) throws Exception {

    }

    @Override
    public void viewAccepted(View new_view
    ) {
        System.out.println("** view: " + new_view);
    }

    private void SendAddTaskCommand(Task task) {

        Envelope envelope = new Envelope();

        envelope.command = "add";

        envelope.data.add(task);

        WriteEnvelopeToChannel(envelope, channelTasks);

    }

    private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) {

        try {
            String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);

            Message msg = new Message(null, null, envelopeXml);

            channel.send(msg);

        } catch (Exception ex) {
            System.out.println("Failed to write task object to the channel. Error message:" + ex.getMessage());
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

}
