//ServerGUI.java
package ee402;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
@SuppressWarnings("serial")
public class ServerGUI extends JFrame implements Runnable,WindowListener{
	private Thread thread=new Thread(this);
	private Statistics stats=null;
	private JTable temp;
	private boolean listening = true;
	private double temp_histAvg=0,co2_histAvg=0,ch4_histAvg=0;
	private int[] timestamp=new int[10];
	private JTextArea All_Averages, Devices; 
	private String [] Headers={"Time Window","Temperature Average","CO2 Average","Methane Average"};
	private Object [][] history_arr=new Object[10][4];
	private SensorGauge Gauge1,Gauge2,Gauge3;
	private JComboBox<Integer> comboBox=new JComboBox<>();;
	DefaultTableModel  model=new DefaultTableModel(history_arr,Headers );
	public ServerGUI(Statistics stats) {
super("Server GUI Saria-LLC");
this.stats=stats;
    	this.setMinimumSize(new Dimension(1000,600));
    	
this.temp=new JTable(model) {@Override
            public boolean isCellEditable(int row, int column) {
            return false;
        }};
        JScrollPane grid=new JScrollPane(temp);
        grid.setAlignmentX(JScrollPane.CENTER_ALIGNMENT);
        grid.setSize(200, 100);
        this.temp.setBackground(new Color(240,240,240));
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel tableP=new JPanel();
        tableP.setBorder(new TitledBorder("Sensor History: "));
        tableP.setSize(200, 200);;
        tableP.add(grid);
        this.All_Averages=new JTextArea();
        this.All_Averages.setEditable(false);
        this.All_Averages.setBackground(Color.WHITE);
        this.All_Averages.setForeground(new Color(0,230,170));
        tableP.setLayout(new BoxLayout(tableP, BoxLayout.Y_AXIS));
        tableP.add(All_Averages);
        
        JPanel Right=new JPanel();
        Right.setLayout(new BoxLayout(Right, BoxLayout.Y_AXIS));
        this.Gauge1=new SensorGauge("CO2");
        Right.add(Gauge1);
        this.Gauge2=new SensorGauge("Methane");
        Right.add(Gauge2);
        this.Gauge3=new SensorGauge("Temperature");
        Right.add(Gauge3);
        JPanel DeviceC=new JPanel();
        DeviceC.setBorder(new TitledBorder("Connected Devices: "));
        Devices=new JTextArea(); 
        this.Devices.setEditable(false);
        this.Devices.setBackground(Color.WHITE);
        this.Devices.setFont(new Font("Arial", Font.BOLD, 16) );
        this.Devices.setForeground(new Color(0,230,170));
        DeviceC.setLayout(new BorderLayout()); // Set BorderLayout for DeviceC
        DeviceC.add(Devices,BorderLayout.WEST);
        Integer [] sleepOptions=new Integer[15];
    	for(int k=3;k<=17;k++) {sleepOptions[k-3]=k;}
    	this.comboBox=new JComboBox<>(sleepOptions);
    	this.comboBox.setSelectedIndex(2);
    	JLabel boxLabel=new JLabel("Time Frame (s):");
    	JPanel TimeFrame=new JPanel();
    	TimeFrame.add(boxLabel);
    	TimeFrame.add(comboBox);
    	DeviceC.add(TimeFrame,BorderLayout.SOUTH);
        DeviceC.setBackground(Color.WHITE);
        tableP.setBackground(Color.WHITE);
        JSplitPane Left=new JSplitPane(JSplitPane.VERTICAL_SPLIT,DeviceC,tableP);
        Left.setMaximumSize(new Dimension(10,10));
    	Left.setDividerLocation(250);
        Left.setEnabled(false);
        JSplitPane FullPanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,Left,Right);
        FullPanel.setDividerLocation(500);
        FullPanel.setEnabled(false);
this.getContentPane().add(FullPanel);
    	this.pack();
    
    	this.setResizable(false);
    	this.setVisible(true);


	}

	public void run() {
while(listening){
	try {
Thread.sleep(1000*(int)this.comboBox.getSelectedItem());
	} catch (InterruptedException e) {
e.printStackTrace();
	}
	System.out.println("First Attempt");
	this.Devices.setText("");
	this.stats.getInstantAvg();
	timestamp=this.stats.getWindow();
	this.Gauge1.setValues(stats.getMin()[0], stats.getMax()[0], stats.getCO2Avg());
	this.Gauge2.setValues(stats.getMin()[1], stats.getMax()[1], stats.getCh4Avg());
	this.Gauge3.setValues(stats.getMin()[2], stats.getMax()[2], stats.getCelsiusAvg());

	System.out.println("Temperature: "+stats.getCelsiusAvg());
	Set<String> DeviceID=this.stats.getConnected();
	for(int s=0;s<DeviceID.size();s++) {
this.Devices.append("Device #"+(s+1)+": "+DeviceID.toArray()[s].toString()+"\n");
	}
	if(stats.getTenReadings()!=null) {
System.out.println("Historical Value Averaging Active");
model.setRowCount(0);
	for(int i=0; i<=9;i++) {
SensorValue [] sensor_history=stats.getTenReadings();
System.out.println("Average Temp Value #"+(i+1)+": "+sensor_history[i].getCelsius());
temp_histAvg+=sensor_history[i].getCelsius();
co2_histAvg+=sensor_history[i].getCO2();
ch4_histAvg+=sensor_history[i].getCH4();
Object [] temparray={timestamp[i],
sensor_history[i].getCO2(),
sensor_history[i].getCH4(),sensor_history[i].getCelsius()};
this.history_arr[i]= temparray;
this.model.addRow(history_arr[i]);	}
	System.out.println(history_arr.length);
	temp_histAvg=Math.round(temp_histAvg*100)/1000.0;
	co2_histAvg=Math.round(co2_histAvg*100)/1000.0;
	ch4_histAvg=Math.round(ch4_histAvg*100)/1000.0;
	model.fireTableDataChanged();
	All_Averages.setText("History Average: \n"
	+ "CO2: "+co2_histAvg+"\n"
	+"CH4: "+ch4_histAvg+"\n"
	+"Temperature: "+temp_histAvg);
	        this.All_Averages.setFont(new Font("Arial", Font.BOLD, 16) );

	history_arr=new Object[10][4]; 
	System.out.println("Redrawn!: "+history_arr);
	System.out.println("Average of all Historical Values for CO2: "+co2_histAvg);
	System.out.println("Average of all Historical Values for Methane: "+ch4_histAvg);
	System.out.println("Average of all Historical Values for Temperature: "+temp_histAvg);
	temp_histAvg=0;
	co2_histAvg=0;
	ch4_histAvg=0;
	}
	this.stats.resetAll();

}

	}

	public void start() {

this.thread.start();

	}
	public void windowClosing(WindowEvent e) {
        System.out.println("Closing the application gracefully.");
        
        dispose();
        System.exit(0);}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}

}



