import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class clientThread extends Thread {

  private DataInputStream is = null;
  private DataOutputStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new DataOutputStream(clientSocket.getOutputStream());

	  ATM atm = new ATM(clientSocket.getInputStream(),os);
      
      while (true) {
    	  
    	  int i = 0;
    	  i = atm.begin();
        if (i == -1) {
          break;
        }
      }
      
      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] == this) {
          threads[i] = null;
        }
      }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    } catch (Exception e) {
    }
  }
}