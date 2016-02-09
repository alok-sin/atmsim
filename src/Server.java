
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	
	private static ServerSocket serverSocket = null;

	  private static Socket server = null;

	 private static final int maxClientsCount = 100;
	  private static final clientThread[] threads = new clientThread[maxClientsCount];
	  
public static void main(String args[]){
	try {
		 
		serverSocket = new ServerSocket(2222); //2222 Port number
		
		 System.out.println("Waiting for client on port " +serverSocket.getLocalPort() + "...");
		 
		 while(true) {
		           try{
			 		server = serverSocket.accept();
		            
		            int i = 0;
		            for (i = 0; i < maxClientsCount; i++) {
		              if (threads[i] == null) {
		                (threads[i] = new clientThread(server, threads)).start();
		                break;
		              }
		            }
		            if (i == maxClientsCount) {
		              DataOutputStream os = new DataOutputStream(server.getOutputStream());
		              os.writeUTF("Server too busy. Try later.");
		              os.close();
		              server.close();
		            }
		           }
		           catch(Exception e) {}
	   serverSocket.close();
		 }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
}
