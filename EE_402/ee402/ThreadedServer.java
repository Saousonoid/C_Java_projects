//ThreadedServer.java
package ee402;
import java.net.*;
import java.io.*;
public class ThreadedServer 
{
	private static int portNumber = 5050;
	
	public static void main(String args[]) {
Statistics stats= new Statistics();
ServerGUI face=new ServerGUI(stats);
face.start();
boolean listening = true;
ServerSocket serverSocket = null;
try 
{
    serverSocket = new ServerSocket(portNumber);
    System.out.println("New Server has started listening on port: " + portNumber );
} 
catch (IOException e) 
{
    System.out.println("Cannot listen on port: " + portNumber + ", Exception: " + e);
    System.exit(1);
}

while (listening) // almost infinite loop - loop once for each client request
{
    Socket clientSocket = null;
    try{
    	System.out.println("**. Listening for a connection...");
clientSocket = serverSocket.accept();
System.out.println("00. <- Accepted socket connection from a client: ");
System.out.println("    <- with address: " + clientSocket.getInetAddress().toString());
System.out.println("    <- and port number: " + clientSocket.getPort());
    } 
    catch (IOException e){
System.out.println("XX. Accept failed: " + portNumber + e);
listening = false;   // end the loop - stop listening for further client requests
    }	
    
    ThreadedConnectionHandler con = new ThreadedConnectionHandler(clientSocket,stats);
    con.start(); 
    System.out.println("02. -- Finished communicating with client:" + clientSocket.getInetAddress().toString());
    
    }
try 
{
    System.out.println("04. -- Closing down the server socket gracefully.");
    serverSocket.close();
} 
catch (IOException e) 
{
    System.err.println("XX. Could not close server socket. " + e.getMessage());
}

    }
}