package itu.dk.smds.e2013.TCPClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClient {

	private static String command = "GET";

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
				System.out.println("Message from Server: " + response);
				System.out.println("Horray - correct respond from server");

				if (command == "POST" || command == "PUT") {
					System.out.println("POST || PUT");
				} else if (command == "GET" || command == "DELETE") {
					System.out.println("GET || DELETE");
				} else {
					System.out.println("Command wasn't found");
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
