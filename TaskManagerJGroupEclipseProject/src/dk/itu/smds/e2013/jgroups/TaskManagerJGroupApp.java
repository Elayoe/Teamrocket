/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.jgroups;

import dk.itu.smds.e2013.jgroups.common.TaskProvider;
import dk.itu.smds.e2013.serialization.common.Envelope;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskSerializer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.bind.JAXBException;
import org.jgroups.JChannel;
import org.jgroups.Message;

/**
 *
 * @author rao
 */
public class TaskManagerJGroupApp {

    //private String taskXmlPath = "/Users/rao/Dropbox/PhDWork/teaching/NetBeans-Projects/lab-exercises/week-06/TaskManagerJGroupApp/src/resources/task-manager-xml.xml";
    private String taskXmlPath = "/Users/Nicki/Documents/ITU/BMDS/TeamRocket/TaskManagerJGroupEclipseProject/bin/resources/task-manager.xml";
    String user_name = System.getProperty("user.name", "n/a");
    private TaskProvider provider;
    String localIp = "127.0.0.1";
    int addPort = 51924;
    int deletePort = 51925;
    int updatePart = 51926;
    private JChannel channelTasks;
    BufferedReader in;
    String id;

    public void start() throws Exception {


        in = new BufferedReader(new InputStreamReader(System.in));


        System.out.println("Please enter the path to task-manager-xml!");

        System.out.println(">");

        try {

            String taskXmlPath = "/Users/Nicki/Documents/ITU/BMDS/TeamRocket/TaskManagerJGroupEclipseProject/bin/resources/task-manager.xml";


            // First load the tasks from the task manager Xml.
            provider = new TaskProvider(taskXmlPath);


        } catch (Exception ex) {

            System.out.println("Failed to load Task Manager Xml! Error message:" + ex.getMessage());

            return;
        }



        // Create channels for each opertaion.
        //channelTasks = ChannelHelper.getNewChannel(localIp, addPort);
        channelTasks = new JChannel();

        channelTasks.setReceiver(new TaskReceiver(provider, channelTasks));

        channelTasks.connect("Add Tasks Channel");

        channelTasks.getState(null, 10000);

        eventLoop();

        channelTasks.close();


    }

    private void eventLoop() {


        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {

                System.out.println("Usage: type one of the commands: 'add', 'delete', 'update', 'replicate', 'execute', 'request', 'get' ! otherwise type 'exit' to quit!  ");

                System.out.print("> ");

                System.out.flush();

                String command = in.readLine().toLowerCase();

                Envelope envelope = new Envelope();



                switch (command.toLowerCase().trim()) {

                    case "add":

                        System.out.println("type or paste task Xml you want to add (in single line)!");

                        System.out.print("> ");

                        //Write Task To Channel
                        String taskXml = in.readLine();


                        Task addTask = TaskSerializer.DeserializeTask(taskXml);

                        envelope.command = command;

                        envelope.data.add(addTask);


                        WriteEnvelopeToChannel(envelope, channelTasks);

                        //WriteTaskToChannel(taskXml, channelAddTaks);

                        break;

                    case "update":

                        System.out.println("type or paste task Xml ");

                        System.out.print("> ");



                        break;



                    case "delete":

                        System.out.println("type or paste task Xml you want to delete (in single line)!");

                        System.out.print("> ");

                        //Write Task To Channel
                        String taskXml2 = in.readLine();


                        Task deleteTask = TaskSerializer.DeserializeTask(taskXml2);



                        envelope.command = command;

                        envelope.data.add(deleteTask);


                        WriteEnvelopeToChannel(envelope, channelTasks);


                        break;

                    case "trace":

                        envelope.command = command;

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;


                    case "replicate":

                        envelope.command = command;

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;
                        
                    case "execute":
                    	
                    	System.out.println("Enter task id!");
                    	
            			System.out.print("> ");
                    	
            			id = in.readLine();
            			
            			envelope.taskId = id;
            			
                    	envelope.command = command;

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;
                        
                    case "request":
                    	
                    	System.out.println("Enter task id!");
                    	
            			System.out.print("> ");
                    	
            			id = in.readLine();
            			
            			envelope.taskId = id;
                    	
                    	envelope.command = command;

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;
                        
                    case "get":
                    	
                    	System.out.println("Enter role name!");
                    	
                    	System.out.print("> ");
                    	
                    	String role = in.readLine();
                    	
                    	envelope.roleName = role;
                    	
                    	envelope.command = command;

                        WriteEnvelopeToChannel(envelope, channelTasks);

                        break;


                    case "exit":
                        return;

                    default:
                        //System.out.println("Usage: type one of the commands: add, delete, update !  ");
                        continue;


                }








            } catch (Exception e) {
                System.out.println("Exit from EventLoop! Error message:" + e.getMessage());

            }
        }
    }

    private void WriteEnvelopeToChannel(Envelope envelope, JChannel channel) throws Exception {

        try {
            String envelopeXml = TaskSerializer.SerializeEnvelope(envelope);

            Message msg = new Message(null, null, envelopeXml);

            channel.send(msg);




        } catch (JAXBException ex) {
            System.out.println("Failed to write task object to the channel. Error message:" + ex.getMessage());
        }



    }

    private void writeToChannel(JChannel channel, String message) throws Exception {
        message = "[" + user_name + "] " + message;

        Message msg = new Message(null, null, message);

        channel.send(msg);
    }

    public static void main(String[] args) throws Exception {


        // Start Task manager.
        new TaskManagerJGroupApp().start();
    }
}
