/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security.server;

import java.util.Random;
import dk.itu.smds.e2013.security.DESEncryptionHelper;
import dk.itu.smds.e2013.security.Utilities;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskManager;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

import com.sun.xml.internal.messaging.saaj.util.Base64;

/**
 * 
 * @author rao
 */
public class TaskManagerServer {

	private static String Token_Server_Key_Passcode = "Don't reveal the Secret";
	private static String Server_Client_Key_Passcode = "You shall not pass";
	private static int serverPort = 8010;
	private static final String Encoding_Format = "UTF8";
	private static BufferedReader in;
	private static ServerSocket serverSocket = null;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static Socket socket = null;
	private static final SimpleDateFormat formatted = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static int nonce = 0;
	private static String timestamp = "";
	private static String role = "";

	public static void main(String args[]) throws Exception {

		// hook on to conole input ..
		in = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter the path to taskmanager Xml!");

		System.out.println(">");

		String path = in.readLine();

		FileInputStream stream = new FileInputStream(path);

		JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

		TaskManager taskManager = (TaskManager) jaxbContext
				.createUnmarshaller().unmarshal(stream);

		System.out.println("Taskmanager loaded with :"
				+ taskManager.tasks.size() + " tasks!");

		try {

			serverSocket = new ServerSocket(serverPort);

			System.out.println("Server started at: " + serverPort);

			socket = serverSocket.accept(); // blocking call

			
			
			while (true) {

				// Data Input stream
				dis = new DataInputStream(socket.getInputStream());

				String request = dis.readUTF(); // blocking call

				System.out.println("Received client Request: " + request);

				String[] requestArray = request.split(";");
	

				// Process communication
				switch (requestArray[0]) {
				case "authenticate":
					String incomingMessage = decryptServerToken(requestArray[1]);
					String[] content = incomingMessage.split(";");
					if (content[3].equals(Server_Client_Key_Passcode)) {

						// Set Data to check later
						role = content[0];
						timestamp = content[1];

						// Generate Nonce
						nonce = new Random().nextInt();
						String numberAsString = nonce + "";
						byte[] encrypt = DESEncryptionHelper.encryptMessage(
								Server_Client_Key_Passcode.getBytes(),
								numberAsString.getBytes());
						String encrypedNumber = Utilities
								.getBase64EncodedString(encrypt);
						String answer = "yes;" + encrypedNumber;

						writeToClient(answer);
					} else {
						writeToClient("no;"
								+ Utilities.bytes2String(DESEncryptionHelper
										.encryptMessage(
												Server_Client_Key_Passcode
														.getBytes(),
												"No valid authentification fool"
														.getBytes())));
					}

					
					continue;
				case "execute":
					System.out.println("Try to execute");
					// Decrypt Number
					byte[] encryptedClientNumberInBytes = Utilities
							.getBase64DecodedBytes(requestArray[1]);
					byte[] decryptedClientNumberInBytes = DESEncryptionHelper
							.decryptMessage(
									Server_Client_Key_Passcode.getBytes(),
									encryptedClientNumberInBytes);
					String decrytpedClientNumberAsString = new String(
							decryptedClientNumberInBytes);
					int clientNonce = Integer
							.parseInt(decrytpedClientNumberAsString);

					// Decrypt Task
					byte[] encryptedServerTaskInBytes = Utilities
							.getBase64DecodedBytes(requestArray[2]);
					byte[] decryptedServerTaskInBytes = DESEncryptionHelper
							.decryptMessage(
									Server_Client_Key_Passcode.getBytes(),
									encryptedServerTaskInBytes);
					String clientTask = new String(decryptedServerTaskInBytes);

					if (nonce != (clientNonce + 1)) {
						System.out.println("Nonce is not valid");
						nonce = new Random().nextInt();
						String numberAsString = nonce + "";
						byte[] encrypt = DESEncryptionHelper.encryptMessage(
								Server_Client_Key_Passcode.getBytes(),
								numberAsString.getBytes());
						String encrypedNumber = Utilities
								.getBase64EncodedString(encrypt);
						writeToClient("no;"
								+ encrypedNumber
								+ Utilities.bytes2String(DESEncryptionHelper
										.encryptMessage(
												Server_Client_Key_Passcode
														.getBytes(),
												"No valid nonce fool"
														.getBytes())));
					
						continue;
					}

					System.out.println("Current Date Time: "
							+ getCurrentDateTime());

					if (!validateTimestamp(timestamp)) {
						System.out.println("Timestamp is not valid");
						nonce = new Random().nextInt();
						String numberAsString = nonce + "";
						byte[] encrypt = DESEncryptionHelper.encryptMessage(
								Server_Client_Key_Passcode.getBytes(),
								numberAsString.getBytes());
						String encrypedNumber = Utilities
								.getBase64EncodedString(encrypt);
						writeToClient("no;"
								+ encrypedNumber
								+ Utilities.bytes2String(DESEncryptionHelper
										.encryptMessage(
												Server_Client_Key_Passcode
														.getBytes(),
												"No valid timestamp fool"
														.getBytes())));
					
						continue;
					}

					Task requestedtask = GetTask(taskManager, clientTask);

					if (requestedtask == null) {

						System.out.println("Task is not valid");
						nonce = new Random().nextInt();
						String numberAsString = nonce + "";
						byte[] encrypt = DESEncryptionHelper.encryptMessage(
								Server_Client_Key_Passcode.getBytes(),
								numberAsString.getBytes());
						String encrypedNumber = Utilities
								.getBase64EncodedString(encrypt);
						writeToClient("no;"
								+ encrypedNumber
								+ Utilities
										.bytes2String(DESEncryptionHelper.encryptMessage(
												Server_Client_Key_Passcode
														.getBytes(),
												("Task with Id:"
														+ requestArray[1] + " can not be found in task manager!")
														.getBytes())));
						
						continue;
					}

					if (!requestedtask.role.contains(role)) {
						if (!matchRolemappings(requestedtask.role, role)) {

							System.out.println("Role is not valid");
							nonce = new Random().nextInt();
							String numberAsString = nonce + "";
							byte[] encrypt = DESEncryptionHelper
									.encryptMessage(Server_Client_Key_Passcode
											.getBytes(), numberAsString
											.getBytes());
							String encrypedNumber = Utilities
									.getBase64EncodedString(encrypt);
							writeToClient("no;"
									+ encrypedNumber
									+ Utilities
											.bytes2String(DESEncryptionHelper.encryptMessage(
													Server_Client_Key_Passcode
															.getBytes(),
													("The client is not authorized to execute task with Id:"
															+ requestArray[1] + " due to role mismatch!")
															.getBytes())));

							
							continue;
						}
					}
					
					// Finally if everything goes well update the task.
					requestedtask.status = "executed";

					System.out.println("Task executed successfully");
					nonce = new Random().nextInt();
					String numberAsString = nonce + "";
					byte[] encrypt = DESEncryptionHelper
							.encryptMessage(Server_Client_Key_Passcode
									.getBytes(), numberAsString
									.getBytes());
					String encrypedNumber = Utilities
							.getBase64EncodedString(encrypt);
					writeToClient("yes;"
							+ encrypedNumber
							+ Utilities
									.bytes2String(DESEncryptionHelper.encryptMessage(
											Server_Client_Key_Passcode
													.getBytes(),
											("Task successfully executed").getBytes())));
				}

				continue;
			}
		}
		 catch (Exception ex) {

			System.out.println(ex.getMessage());
		}

	}

	private static void writeToClient(String message) throws IOException {

		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

		dos.writeUTF(message);

		dos.flush();

	}

	private static String decryptServerToken(String serverToken)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			FileNotFoundException, IllegalBlockSizeException,
			BadPaddingException {
		// Format of server token = [role];[timestamp]
		byte[] base64DecodedBytes = Utilities
				.getBase64DecodedBytes(serverToken);

		byte[] decryptMessageBytes = DESEncryptionHelper.decryptMessage(
				Token_Server_Key_Passcode.getBytes(Encoding_Format),
				base64DecodedBytes);

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

				if (taskRoleArray[index].trim().equals(
						userRoleArray[index2].trim())) {

					return true;
				}

			}
		}
		return false;

	}
}
