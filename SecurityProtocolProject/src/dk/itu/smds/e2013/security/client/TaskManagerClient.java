/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security.client;

import dk.itu.smds.e2013.security.DESEncryptionHelper;
import dk.itu.smds.e2013.security.RoleBasedToken;
import dk.itu.smds.e2013.security.Utilities;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 
 * @author rao
 */
public class TaskManagerClient {

	private static String Client_TokenService_Key_Passcode = "Topmost Secret";
	private static String Server_Client_Key_Passcode = "You shall not pass";
	private static final String Encoding_Format = "UTF8";
	private static BufferedReader in;
	private static InetAddress serverAddress;
	private static int tokenServerPort = 8008;
	private static int clientPort = 8009;
	private static int serverPort = 8010;

	public static void main(String args[]) throws Exception {

		serverAddress = InetAddress.getByName("localhost");

		// Socket socket = null;

		DataInputStream dis;

		ObjectInputStream ois;

		ObjectOutputStream oos;

		// hook on to conole input ..
		in = new BufferedReader(new InputStreamReader(System.in));

		RoleBasedToken roleBasedToken = getRoleBasedToken();

		if (!roleBasedToken.result) {

			System.out
					.println("Failed to get Role based security token. Error message: "
							+ roleBasedToken.errorMessage
							+ " Enter 'try' to try once again or enter 'exit' to stop the client.");

			String option = in.readLine();

			if (option.toLowerCase().equals("try")) {
				roleBasedToken = getRoleBasedToken();
			} else {
				return;
			}

		}
		// If we fail to get role token second time also, then exit.
		if (!roleBasedToken.result) {
			System.out
					.println("Failed to get Role based security token second time also. Error message: "
							+ roleBasedToken.errorMessage
							+ "The client program will be stopped!");
			return;
		}

		String serverToken64format;
		// Get the once encrypted message from the token service
		try {
			serverToken64format = getServerToken(roleBasedToken.token);

		} catch (Exception ex) {

			System.out
					.println("Failed to decrypt the token received from token service and server token could not be extracted!. Error message: "
							+ ex.getMessage()
							+ "The client program will be stopped!");

			return;
		}

		System.out.println("Server token extracted successfully. Token:"
				+ serverToken64format);

		// Format: serverToken;taskid
		String serverMessage = "authenticate;"
				+ serverToken64format.split(";")[0];

		// create a connection to server and write the message which contains
		// server token and taskid.
		Socket socket = new Socket(serverAddress, serverPort);

		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

		dos.writeUTF(serverMessage);

		dos.flush();

		
		
		
		while (true) {
			
			// open an object stream and get the rolebased security token
			dis = new DataInputStream(socket.getInputStream());
			// Read server message
			String[] answer = dis.readUTF().split(";");

			
			if (answer.length > 2) {
				byte[] encryptedServerMessageInBytes = Utilities
						.getBase64DecodedBytes(answer[3]);
				byte[] decryptedServerMessageInBytes = DESEncryptionHelper
						.decryptMessage(Server_Client_Key_Passcode.getBytes(),
								encryptedServerMessageInBytes);
				String serverAnswerMessage = new String(
						decryptedServerMessageInBytes);

				System.out.println(serverAnswerMessage);
			}

			// Process server response
			switch (answer[0]) {
			case "yes":

				// Decrypt Number
				byte[] encryptedServerAnswerInBytes = Utilities
						.getBase64DecodedBytes(answer[1]);
				byte[] decryptedServerAnswerInBytes = DESEncryptionHelper
						.decryptMessage(Server_Client_Key_Passcode.getBytes(),
								encryptedServerAnswerInBytes);
				String decrytpedServerAnswerAsString = new String(
						decryptedServerAnswerInBytes);
				int serverNonce = Integer
						.parseInt(decrytpedServerAnswerAsString);

				System.out.println("Nonce by Server: " + serverNonce);

				// Create Own number
				int noncy = serverNonce - 1;
				String clientNonce = noncy + "";
				clientNonce = Utilities.bytes2String(DESEncryptionHelper
						.encryptMessage(Server_Client_Key_Passcode.getBytes(),
								clientNonce.getBytes()));

				// Get desired task
				String taskId = getTask();

				if (taskId == null) {
					dis.close();
					continue; }

				String data = Utilities.bytes2String(DESEncryptionHelper
						.encryptMessage(Server_Client_Key_Passcode.getBytes(),
								taskId.getBytes()));

				String result = "execute;" + clientNonce + ";" + data;

				dos.writeUTF(result);
				dos.flush();

				break;
			case "no":
				String errorMessage = Utilities
						.bytes2String((DESEncryptionHelper.decryptMessage(
								Server_Client_Key_Passcode.getBytes(),
								answer[1].getBytes())));
				System.out.println(errorMessage);
				break;
			}

		}

	}

	private static String getServerToken(String tokenFromTokenService)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			FileNotFoundException, IllegalBlockSizeException,
			BadPaddingException {

		// decrypt the tokenfrom token server.
		byte[] base64DecodedBytes = Utilities
				.getBase64DecodedBytes(tokenFromTokenService);

		// Get the decrypted server token bytes.
		byte[] decryptedBytes = DESEncryptionHelper.decryptMessage(
				Client_TokenService_Key_Passcode.getBytes(Encoding_Format),
				base64DecodedBytes);
		// Convert these bytes to String to get server token in base64 encoding.
		String serverToken64format = Utilities.bytes2String(decryptedBytes);

		return serverToken64format;

	}

	private static RoleBasedToken getRoleBasedToken() {

		RoleBasedToken roleTokenObj = new RoleBasedToken();

		try {
			System.out.println("Please enter your ITU username");

			System.out.println(">");

			String username = in.readLine();

			System.out.println("Please enter your password");

			System.out.println(">");

			String password = in.readLine();

			// This is for taking the password in the base64 format, so that
			// other people can't see your password!
			// password =
			// Utilities.bytes2String(Utilities.getBase64DecodedBytes(password));

			String encrypteduserNameToken = getUserNameToken(username, password);

			// create a connection to token server and write the encrypted
			// username token
			Socket socket = new Socket(serverAddress, tokenServerPort);

			DataOutputStream dos = new DataOutputStream(
					socket.getOutputStream());

			dos.writeUTF(encrypteduserNameToken);

			dos.flush();

			// open an object stream and get the rolebased security token
			// object.
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			roleTokenObj = (RoleBasedToken) ois.readObject();

			return roleTokenObj;

		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException
				| InvalidKeySpecException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException
				| ClassNotFoundException ex) {

			roleTokenObj.result = false;

			roleTokenObj.errorMessage = "Error in encrypting the username password token. Error message: "
					+ ex.getMessage();

			return roleTokenObj;

		}

	}

	private static String getUserNameToken(String username, String password)
			throws UnsupportedEncodingException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, FileNotFoundException, IOException,
			IllegalBlockSizeException, BadPaddingException {

		// Format: Username=XXX;Password=XXX";
		String usernamePwd = "Username=" + username + ";Password=" + password;

		// First encrypt the username token with encryption key shared between
		// Client and token service
		byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(
				Client_TokenService_Key_Passcode.getBytes(Encoding_Format),
				usernamePwd.getBytes(Encoding_Format));

		return Utilities.getBase64EncodedString(encryptTokenBytes);

	}

	private static String getTask() {
		System.out
				.println("Please enter taks id to execute! or enter 'exit' to stop the program!");

		System.out.println(">");

		String taskId = "";

		try {
			taskId = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (taskId.toLowerCase().equals("exit")) {
			return null;
		}

		return taskId;
	}
}
