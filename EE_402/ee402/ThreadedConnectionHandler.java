//ThreadedConnectionHandler.java
package ee402;

import java.net.*;


import java.io.*;

public class ThreadedConnectionHandler extends Thread
{	
	private Thread thread;
private Socket clientSocket = null;				// Client socket object
private ObjectInputStream is = null;			// Input stream
private ObjectOutputStream os = null;	
private SensorValue sensorobj;// Output stream
private static int objcount=0;
private Statistics stats=new Statistics();


	// The constructor for the connection handler
public ThreadedConnectionHandler(Socket clientSocket,Statistics stats) {
this.clientSocket = clientSocket;
	this.thread= new Thread(this);
	this.stats=stats;
}

public void run() {

 try {

this.is = new ObjectInputStream(clientSocket.getInputStream());
this.os = new ObjectOutputStream(clientSocket.getOutputStream());
while (this.readCommand()) {
						objcount++; }
 } 
 catch (IOException e) 
 {
	System.out.println("XX. There was a problem with the Input/Output Communication:");
e.printStackTrace();
 }
}

// Receive and process incoming string commands from client socket 
private boolean readCommand() {
	SensorValue s = null;
try {
s = (SensorValue) is.readObject();
sensorobj=s;
this.stats.setInstant(sensorobj);
System.out.println("Counter: "+objcount);


} 
catch (Exception e){// catch a general exception
	this.closeSocket();
return false;
}

System.out.println("01. <- Received a Sensor object from the client: "+s.getDevice()+"  with the following values:");
System.out.println(
		"CO2 Levels: "+s.getCO2()+"\n"+
		"CH4 Levels: "+s.getCH4()+"\n"+
		"Temperature(C): "+s.getCelsius());
this.send(s);

 

return true;
}
private void send(Object o) {
try {
System.out.println("02. -> Sending (" + o +") to the client.");
this.os.writeObject(o);
this.os.flush();
} 
catch (Exception e) {
	closeSocket();
System.out.println("XX. Client Closed Abruptly");
}
}

public void sendError(String message) { 
this.send("Error:" + message);	//remember a String IS-A Object!
}
public void closeSocket() { //gracefully close the socket connection
try {
this.os.close();
this.is.close();
this.clientSocket.close();
System.out.println(sensorobj.getDevice()+" Closed Gracefully");
} 
catch (Exception e) {
System.out.println("XX. " + e.getStackTrace());
}


}
	public void start() {
		this.thread.start();
	}
}