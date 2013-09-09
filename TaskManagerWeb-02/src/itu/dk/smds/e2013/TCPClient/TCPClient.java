package itu.dk.smds.e2013.TCPClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jmx.snmp.tasks.Task;

public class TCPClient {

	// MyExamples
	private static String command = "PUT";
	private static String myID = "1234";
	private static Task mytask = new Task();

	public static void main(String args[]) {
		try {
			// IP address of the server,
			InetAddress serverAddress = InetAddress.getByName("localhost");

			// It is the same port where server will be listening.
			int serverPort = 2000;

			String message = command;

			// Open a socket for communication.
			Socket socket = new Socket(serverAddress, serverPort);

			// Get data output stream to send a String message to server.
			DataOutputStream dos = new DataOutputStream(
					socket.getOutputStream());

			dos.writeUTF(message);

			dos.flush();

			// Now switch to listening mode for receiving message from
			// server.
			DataInputStream dis = new DataInputStream(socket.getInputStream());

			// Note that this is a blocking call,
			String response = dis.readUTF();

			// Print the message.
			if (response.equals(command)) {

				// create object streams to send and receive objects if neccessary
				ObjectOutputStream myOutputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream myIntputStream = new ObjectInputStream(socket.getInputStream());
				
				// Format:
				// Send something to the server
				// Flush the stream
				// Display the Server response
				switch (command) {
				
				case "POST":
					myOutputStream.writeObject(mytask);
					myOutputStream.flush();
					System.out.println("The posted object is: " + dis.readUTF());
					break;
					
				case "PUT":
					myOutputStream.writeObject(mytask);
					myOutputStream.flush();
					System.out.println("The puted object is: " + dis.readUTF());
					break;
					
				case "GET":
					dos.writeUTF(myID);
					
					@SuppressWarnings("unchecked")
					List<Task> myList = (List<Task>) myIntputStream.readObject();
					
					foreach(Task elem : myList) {
						System.out.println(elem.toString());
					}	
					break;
					
				case "DELETE":
					dos.writeUTF(myID);
					System.out.println("The delited object is: " + dis.readUTF());
					break;
				default:
					System.out.println("Command wasn't found");
					break;
				}

			} else {
				System.out.println("Arwww - no correct answer from server");
			}

			// Finnaly close the socket.
			socket.close();

		} catch (IOException ex) {
			Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null,
					ex);

			System.out.println("error message: " + ex.getMessage());
		}
	}
}
