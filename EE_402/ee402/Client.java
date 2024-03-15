//Client.java
package ee402;
import java.net.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;

@SuppressWarnings("serial")
public class Client extends JFrame implements Runnable, ActionListener,ChangeListener {
	private JButton toggle, changeName;
	private static int portNumber = 5050,counter=1;
	private Socket socket = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;
    private String sensorname="";
    private Thread thread;
    private JTextArea sens,stream,co2,ch4,temp;
    private boolean status=false,status2=false;
    private JSlider s1,s2,s3;
    private  double coinit=1000, ch4init=1.5,tempinit=25;
    private FileOutputStream out ;
    private ObjectOutputStream s;
     

	// the constructor expects the IP address of the server - the port is fixed
    public Client(String serverIP, String sensorname) {
    	super("Client: "+sensorname+" Application");
    	this.sensorname=sensorname;
    	if (!connectToServer(serverIP)) {
    		System.out.println("XX. Failed to open socket connection to: " + serverIP);    
    		JOptionPane.showMessageDialog(this, "Server IP Address Incorrect or Server Doesn't Exist \n Please Turn On Server or Input a Different IP Address!", 
    				"Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	
    	}
    	else {	
    try {
this.out = new FileOutputStream(this.sensorname + ".ser");
this.s = new ObjectOutputStream(out);
    } catch (IOException e) {
e.printStackTrace();
    }
    	this.toggle=new JButton("Start");
    	this.changeName=new JButton("Change Device Name");
    	this.changeName.addActionListener(this);
    	this.toggle.addActionListener(this);
    	JPanel grid=new JPanel();
    	this.stream=new JTextArea("Output Stream",20,1);
    	this.stream.setEditable(false);
    	JScrollPane grid1=new JScrollPane(stream,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
    			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    	);
		this.s1 = new JSlider(100, 2000, 400);
		this.s1.getAccessibleContext().setAccessibleName("Initial CO2 Levels");
		this.s1.setPaintTicks(true);
		this.s1.setMajorTickSpacing(100);
		this.co2=new JTextArea("CO2: "+this.s1.getValue()+" ppm");
		JPanel slider1 = new JPanel();
		slider1.setLayout(new BoxLayout(slider1, BoxLayout.Y_AXIS));
		slider1.setBorder(new TitledBorder("Initial CO2 Levels"));
		slider1.add(s1);
		slider1.add(co2);
		this.s2 = new JSlider(8, 20, 15);
		this.s2.getAccessibleContext().setAccessibleName("Initial CO2 Levels");
		this.s2.setPaintTicks(true);
		this.s2.setMajorTickSpacing(1);
		this.ch4=new JTextArea("CH4: "+this.s2.getValue()/10+" ppm");
		JPanel slider2 = new JPanel();
		slider2.setLayout(new BoxLayout(slider2, BoxLayout.Y_AXIS));
		slider2.setBorder(new TitledBorder("Initial Methance Levels"));
		slider2.add(s2);
		slider2.add(ch4);
		this.s3 = new JSlider(-10, 50, 25);
		this.s3.getAccessibleContext().setAccessibleName("Initial CO2 Levels");
		this.s3.setPaintTicks(true);
		this.s3.setMajorTickSpacing(100);
		this.temp=new JTextArea("Temperature: "+this.s3.getValue()+" °C");
		JPanel slider3 = new JPanel();
		slider3.setLayout(new BoxLayout(slider3, BoxLayout.Y_AXIS));
		slider3.setBorder(new TitledBorder("Initial Temperature(Celsius)"));
		slider3.add(s3);
		slider3.add(temp);
		this.s1.addChangeListener(this);
		this.s2.addChangeListener(this);
		this.s3.addChangeListener(this);
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    	grid.add(toggle);
    	grid.add(changeName);
    	grid.add("South",slider1);
    	grid.add("South",slider2);
    	grid.add("South",slider3);
    	JSplitPane pain=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,grid,grid1);
    	pain.setMaximumSize(new Dimension(10,10));
    	pain.setEnabled(false);
    	pain.setDividerLocation(250);
    	this.sens=new JTextArea("Client: "+this.getSensorName());
    	this.sens.setSize(40, 30);
    	this.sens.setEditable(false);
    	this.getContentPane().add("North",sens);
    	this.getContentPane().add("South",pain);
    	this.thread= new Thread(this);
    	this.setMinimumSize(new Dimension(600,400));
    	this.setVisible(true);
    	this.setResizable(false);
    	}
    }
    public String getSensorName() {return this.sensorname;}
    public void modifySensorName(String sensorname) { 
    	
    	this.sensorname=sensorname;
    }

    private boolean connectToServer(String serverIP) {
    	try { // open a new socket to the server 
    		this.socket = new Socket(serverIP,portNumber);
    		this.os = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    	} 
catch (Exception e) {
	System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
	System.out.println("    Exception: " + e.toString());	
	return false;
}
		return true;
    }

    private SensorValue randomNoise(String devName) {

    	double co2Val=  (Math.round(this.coinit*Math.random()*15.0))/10.0 ;
    	double ch4Val= (Math.round(this.ch4init*Math.random()*20))/100.0;
    	double celsiusVal= (Math.round(this.tempinit*Math.random()*20.0))/10.0;
    	this.stream.append("\n Measurement Iteration No."+counter+++" With Sensor Values: "
    			+ "\n CO2: "+co2Val+" ppm"
    			+ "\n Methane: "+ch4Val+" ppm"
    			+ "\n Temperature: "+celsiusVal+" °C"+
    			"\n ***************************************************************************************");
		SensorValue senobj=new SensorValue(co2Val,ch4Val,celsiusVal,devName);
		this.send(senobj);
		try{
    		SensorValue response = (SensorValue) receive();
     		System.out.println("05. <- The Server responded with: ");
    		if(response.getCO2()==senobj.getCO2() 
    				&& response.getCH4()==senobj.getCH4()
    				&& response.getCelsius()==senobj.getCelsius()) 
    		{System.out.println("    <- Object of type " + response.getClass()+" Sent Successfully");}
    		else {    		System.out.println("Invalid object or object corrupted");}
		}
    	catch (Exception e){
    		System.out.println("XX. There was an invalid object sent back from the server");
    	}
    	System.out.println("06. -- Disconnected from Server.");
    	return senobj;
		
    }
	
    // method to send a generic object.
    private void send(Object o) {
		try {
			
		    System.out.println("02. -> Sending an object...");
		    os.writeObject(o);
		    os.flush();
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Sending:" +  e.toString());
		}
    }

    // method to receive a generic object.
    private Object receive() 
    {
		Object o = null;
		try {
			System.out.println("03. -- About to receive an object...");
		    o = is.readObject();
		    System.out.println("04. <- Object received...");
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		}
		return o;
    }

    public static void main(String args[]) 
    {
    	System.out.println("**. Java Client Application - EE402 OOP Module, DCU");
    	if(args.length==2){

    		Client theApp = new Client(args[0],args[1]);
    		System.out.println("Created Sensor: "+theApp.getSensorName());
    		}
    	else
    	{
    		System.out.println("Error: you must provide the address of the server");
    		System.out.println("Usage is:  java Client x.x.x.x  (e.g. java Client 192.168.7.2)");
    		System.out.println("      or:  java Client hostname (e.g. java Client localhost)");
    	}    
    	System.out.println("**. End of Application.");
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("status1: "+this.status);
		System.out.println("status2: "+this.status2);

		if(e.getSource().equals(toggle)) { 
			
		if(this.status&this.status2) {
		System.out.println("Starting Again");
				this.status2=false;
				synchronized(this) {this.notify();}
				this.toggle.setText("Pause");
				this.stream.append("\n Simulation Resumed \n"
						+ "************************************************************");
		}
		else if(this.status &!this.status2){
			System.out.println("Pausing");
			this.status2=true;
			this.toggle.setText("Start");
			this.stream.append("\n Simulation Paused \n"
					+ "************************************************************");
		} 
		else if(!this.status &!this.status2) {
		System.out.println("Starting");
		this.status=true;
		this.toggle.setText("Pause");


		this.thread.start();


	}}
		if(e.getSource().equals(changeName)) {
			String name = JOptionPane.showInputDialog(this, "Change Device Name", 
					"Device Options", JOptionPane.QUESTION_MESSAGE);
			if(name!=null) {
			this.sensorname=name;
		    this.sens.setText("Client: "+name);
			System.out.println("Device Name changed to: "+name);
			this.stream.append("\n Name of device was changed to: "+name);
			}
		}
	}
	@Override
	public void run() {

		while(status) {
		SensorValue   logged_obj= this.randomNoise(this.getSensorName());

		    try {
			    createLog(logged_obj);
				Thread.sleep(10000);
				while(status2) {
					
					synchronized(this) {
					System.out.println("Passed Through here!");
					 wait();}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
	if(e.getSource().equals(s1)) {
		this.co2.setText("CO2: "+this.s1.getValue()+" ppm");
		this.coinit=this.s1.getValue();
	}
	if(e.getSource().equals(s2)) {
		this.ch4.setText("CH4: "+(this.s2.getValue()/10.0)+" ppm");
		this.ch4init=this.s2.getValue();
	}
	if(e.getSource().equals(s3)) {
		this.temp.setText("Temperature: "+this.s3.getValue()+" °C");
		this.tempinit=this.s3.getValue();
	}}

    private void createLog(Object object) {
try {
    s.writeObject(object);
    s.flush();
} catch (IOException e) {
    e.printStackTrace();
}
    }
	
}