/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security.server;

import dk.itu.smds.e2013.security.DESEncryptionHelper;
import dk.itu.smds.e2013.security.Utilities;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskManager;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.JAXBContext;

/**
 *
 * @author rao
 */
public class TaskManagerServer {

    private static String Server_Key_Passcode = "Don't reveal the Secret";
    private static int serverPort = 8010;
    private static final String Encoding_Format = "UTF8";
    private static BufferedReader in;
    private static ServerSocket serverSocket = null;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static Socket socket = null;
    private static final SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void main(String args[]) throws Exception {


        // hook on to conole input ..
        in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please enter the path to taskmanager Xml!");

        System.out.println(">");

        String path = in.readLine();


        FileInputStream stream = new FileInputStream(path);

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

        TaskManager taskManager = (TaskManager) jaxbContext.createUnmarshaller().unmarshal(stream);

        System.out.println("Taskmanager loaded with :" + taskManager.tasks.size() + " tasks!");


        try {

            serverSocket = new ServerSocket(serverPort);

            System.out.println("Server started at: " + serverPort);

            while (true) {

                socket = serverSocket.accept(); // blocking call

                // Data Input and output streams
                dis = new DataInputStream(socket.getInputStream());


                String request = dis.readUTF(); // blocking call

                System.out.println("Received client Request: " + request);

                String[] requestArray = request.split(";");

                if (requestArray.length != 2) {
                    writeToClient("Invalid request! The format of requst should be [server token];[task-id]");
                    continue;
                }

                String serverTokenPlain;

                try {
                    serverTokenPlain = decryptServerToken(requestArray[0]);
                } catch (Exception ex) {

                    System.out.println(ex.getMessage());

                    writeToClient("Failed to decrypt server token! Your request can not be processed!");

                    continue;
                }

                System.out.println("Current Date Time: " + getCurrentDateTime());
                System.out.println("Decrypted token: " + serverTokenPlain);



                String[] tokenArray = serverTokenPlain.split(";");


                if (tokenArray.length != 2) {
                    writeToClient("Invalid server token! The Format of server token should be [role];[timestamp]");
                    continue;
                }

                if (!validateTimestamp(tokenArray[1])) {

                    writeToClient("Timestamp for server token expired! The client request can not be processed!");
                    continue;
                }

                Task requestedtask = GetTask(taskManager, requestArray[1]);

                if (requestedtask == null) {

                    writeToClient("Task with Id:" + requestArray[1] + " can not be found in task manager!");
                    continue;
                }



                //if (!requestedtask.role.contains(tokenArray[0])) {
                if (!matchRolemappings(requestedtask.role, tokenArray[0])) {

                    writeToClient("The client is not authorized to execute task with Id:" + requestArray[1] + " due to role mismatch!");
                    
                    continue;
                }

                // Finnaly if ewverything goes well update the task.
                requestedtask.status = "executed";

                

                writeToClient("The task with Id:" + requestArray[1] + " executed successfully!");

            }

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }



    }

    private static void writeToClient(String message) throws IOException {

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF(message);

        dos.flush();

    }

    private static String decryptServerToken(String serverToken) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
        // Format of server token = [role];[timestamp]
        byte[] base64DecodedBytes = Utilities.getBase64DecodedBytes(serverToken);


        byte[] decryptMessageBytes = DESEncryptionHelper.decryptMessage(Server_Key_Passcode.getBytes(Encoding_Format), base64DecodedBytes);

        String serverTokenPlain = Utilities.bytes2String(decryptMessageBytes);

        return serverTokenPlain;

    }

    private static boolean validateTimestamp(String timestamp) {
        try {
            Date expiryDate = formatted.parse(timestamp);

            Date now = Calendar.getInstance().getTime();

            if (expiryDate.compareTo(now) < 0) {
                return false;
            }

            return true;



        } catch (ParseException ex) {
            return false;
        }
    }

    private static Task GetTask(TaskManager taskManager, String taskid) {

        ListIterator<Task> listIterator = taskManager.tasks.listIterator();

        while (listIterator.hasNext()) {
            Task nextTask = listIterator.next();

            if (nextTask.id.equals(taskid)) {

                return nextTask;
            }
        }


        return null;
    }

    private static String getCurrentDateTime() {

        Date now = Calendar.getInstance().getTime();

        return formatted.format(now);

    }

    private static boolean matchRolemappings(String taskRoles, String userRoles) {

        String[] taskRoleArray = taskRoles.split(",");

        String[] userRoleArray = userRoles.split(",");

        for (int index = 0; index < taskRoleArray.length; index++) {

            for (int index2 = 0; index2 < userRoleArray.length; index2++) {

                if (taskRoleArray[index].trim().equals(userRoleArray[index2].trim())) {

                    return true;
                }

            }
        }
        return false;

    }
}
