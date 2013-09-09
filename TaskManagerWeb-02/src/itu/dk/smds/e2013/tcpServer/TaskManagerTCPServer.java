package itu.dk.smds.e2013.tcpServer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskManagerTCPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
        try {
            int serverPort = 2000;

            // create a server socket listening at port 2000
            ServerSocket serverSocket = new ServerSocket(serverPort);

            System.out.println("Server started at 2000");

            // Server starts accepting requests.
            // This is blocking call, and it wont return, until there is request from a client.
            Socket socket = serverSocket.accept();

            // Get the inputstream to receive data sent by client. 
            InputStream is = socket.getInputStream();

            // based on the type of data we want to read, we will open suitable input stream.  
            DataInputStream dis = new DataInputStream(is);

            // Read the String data sent by client at once using readUTF,
            // Note that read also calls blocking and wont return until we have some data sent by client. 
            String message = dis.readUTF(); // blocking call

            // Print the message.
            System.out.println("Message from Client: " + message);

            // Now the server switches to output mode delivering some message to client.
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(message);

            outputStream.flush();

            socket.close();
            
            serverSocket.close();
        	

        } catch (IOException ex) {
            Logger.getLogger(TaskManagerTCPServer.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println("error message: " + ex.getMessage());
        }
	}

}
