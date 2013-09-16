package itu.dk.smds.e2013.tcpServer;

import itu.dk.smds.e2013.common.Cal;
import itu.dk.smds.e2013.common.CalSerializer;
import itu.dk.smds.e2013.common.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class TaskManagerTCPServer {

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException {

		try {
			int serverPort = 2000;

			// create a server socket listening at port 2000
			ServerSocket serverSocket = new ServerSocket(serverPort);

			System.out.println("Server started at 2000");

			// Server starts accepting requests.
			// This is blocking call, and it wont return, until there is request
			// from a client.
			Socket socket = serverSocket.accept();

			// Get the inputstream to receive data sent by client.
			InputStream is = socket.getInputStream();

			// based on the type of data we want to read, we will open suitable
			// input stream.
			DataInputStream dis = new DataInputStream(is);

			// Read the String data sent by client at once using readUTF,
			// Note that read also calls blocking and wont return until we have
			// some data sent by client.
			String message = dis.readUTF(); // blocking call

			
			// Print the message.
			System.out.println("Message from Client: " + message);

			// Now the server switches to output mode delivering some message to
			// client.
			DataOutputStream outputStream = new DataOutputStream(
					socket.getOutputStream());

			outputStream.writeUTF(message);

			outputStream.flush();
			
			ObjectInputStream myIntputStream;
			ObjectOutputStream myOutputStream;
			
			
			switch (message) {
			case "PUT":
				
				myIntputStream = new ObjectInputStream(socket.getInputStream());
				Task putTask = null; 
				
				try {
					putTask = (Task) myIntputStream.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				
				outputStream.writeUTF(CalSerializer.putTask(putTask));
				
				break;
			
			case "POST":
				
				myIntputStream = new ObjectInputStream(socket.getInputStream());
				Task postTask = null; 
				
				try {
					postTask = (Task) myIntputStream.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			
				outputStream.writeUTF(CalSerializer.postTask(postTask));
				
				break;
				
			case "GET":
				
				String getID = dis.readUTF();
				
				myOutputStream = new ObjectOutputStream(socket.getOutputStream());

				myOutputStream.writeObject(CalSerializer.getList(getID));
				
				break;
				
			case "DELETE":
				
				String deleteID = dis.readUTF();

				outputStream.writeUTF(CalSerializer.deleteTask(deleteID));
				
				break;
			default:
				break;

			}

			socket.close();

			serverSocket.close();

		} catch (IOException ex) {
			Logger.getLogger(TaskManagerTCPServer.class.getName()).log(
					Level.SEVERE, null, ex);

			System.out.println("error message: " + ex.getMessage());
		}
	}

}
