//SensorGauge.java
package ee402;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class SensorGauge extends JPanel {
	private JProgressBar progressBar,progressBarMax,progressBarMin;
	private JLabel	progressLabel,progressLabelMax,progressLabelMin,avglabel,minlabel,maxlabel;
	int []time=new int[10];
	 int[] average=new int[10] ;
	private double min, max, avg;
	private int  max_bound=0;
	private String unit,SensorType;
	private CanvasGauge barGauge;

	public SensorGauge(String value){
		if(value=="CO2"||value=="Methane") {
			this.unit=" ppm";
			if(value=="CO2") {
			this.max_bound=2000;}
			else {this.max_bound=4;}
		}
		else {
			this.unit="\u00B0C";
			this.max_bound=70;
		}
		this.SensorType=value;
			
		this.progressBar = new JProgressBar(JProgressBar.VERTICAL,0,max_bound);
		this.progressLabel = new JLabel("Low");
		this.progressBarMax = new JProgressBar(JProgressBar.VERTICAL,0,max_bound);
		this.progressLabelMax = new JLabel("Max");
		this.progressBarMin = new JProgressBar(JProgressBar.VERTICAL,0,max_bound);
		this.progressLabelMin = new JLabel("Min");
		this.avglabel=new JLabel(avg+"");
		this.avglabel.setFont(new Font("Arial", Font.BOLD, 24) );
		this.minlabel=new JLabel(min+"");
		this.minlabel.setFont(new Font("Arial", Font.BOLD, 14) );
		this.maxlabel=new JLabel(max+"");
		this.maxlabel.setFont(new Font("Arial", Font.BOLD, 14) );
		
		JPanel progress_dashboard=new JPanel();
		
		
		avglabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		minlabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		maxlabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		progressBarMin.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
		progressBarMin.setForeground(new Color(80,40,0)); 
		progressBarMax.setForeground(new Color(255,255,0)); 

		progressBar.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
		progressBarMax.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
		JPanel dashboard=new JPanel();
		dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
		dashboard.add("Center",avglabel);
		dashboard.setBackground(Color.WHITE);
		JPanel min_max_dashboard=new JPanel();
		min_max_dashboard.add(minlabel);
		min_max_dashboard.add(maxlabel);
		dashboard.add(min_max_dashboard);
		this.barGauge = new CanvasGauge("Bar Chart");
		dashboard.add(barGauge);
		progress_dashboard.add(progressBarMin);
		progress_dashboard.add(progressLabelMin);
		progress_dashboard.add(progressBar);
		progress_dashboard.add(progressLabel);
		progress_dashboard.add(progressBarMax);
		progress_dashboard.add(progressLabelMax);
		this.setLayout(new BorderLayout());
		min_max_dashboard.setBackground(Color.WHITE);
		progress_dashboard.setBackground(Color.WHITE);
		this.add(progress_dashboard,BorderLayout.WEST);
		this.add(Box.createHorizontalGlue());
		this.add(dashboard,BorderLayout.CENTER);
        this.setBorder(new TitledBorder("Sensor Value: "+value));
        this.setBackground(Color.WHITE);

	}
	public void setValues(double min,double max,double avg) {
		this.min=min;
		this.max=max;
		this.avg=avg;
		progressBar.setValue((int)avg);
		progressBarMax.setValue((int)max);
		progressBarMin.setValue((int)min);

		avglabel.setText(Math.round(avg*100.0)/100.0+unit);
		minlabel.setText("Min: "+Math.round(min*100.00)/100.0+unit);
		maxlabel.setText("Max: "+Math.round(max*100.00)/100.0+unit);



		SensorLogic();

	}
	public void SensorLogic() {
		Color avgcolor=Color.WHITE;
		if(SensorType.equals("CO2")) {
		if(avg<350)	{
			this.progressLabel.setText("Low");
		}
		if(avg>1000) {
			System.out.println(avg+" Means High!");
			this.progressLabel.setText("High");
			}
		if (avg<=1000 &&avg>=350) {

			this.progressLabel.setText("Normal");

			} }
		
		if(SensorType.equals("Methane")) {

		if(avg<1.2)	{
			this.progressLabel.setText("Low");
		}
		if(avg>1.9) {
		
			this.progressLabel.setText("High");
			}
		if(avg<=1.9 &&avg>=1.2)  {
			this.progressLabel.setText("Normal");
			}
		}
		if(SensorType.equals("Temperature")){

		if(avg<5)	{
			this.progressLabel.setText("Low");
		}
		if(avg>33) {
			this.progressLabel.setText("High");
			}
		if(avg<=33&&avg>=5)  {
			this.progressLabel.setText("Normal");
			}
		}
		if(this.progressLabel.getText()=="Low") {
			this.progressBar.setForeground(Color.BLUE);
			this.avglabel.setForeground(Color.BLUE);
			avgcolor=Color.BLUE;		}
		if (this.progressLabel.getText()=="High") {
			this.progressBar.setForeground(Color.RED);
			this.avglabel.setForeground(Color.RED);
			avgcolor=Color.RED;
		}
		if(this.progressLabel.getText()=="Normal") 
		 {this.progressBar.setForeground(Color.GREEN);
			  this.avglabel.setForeground(Color.GREEN);
				avgcolor=Color.GREEN;
		}
        barGauge.setValues( min, avg, max,max_bound,avgcolor);}

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Test Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        SensorGauge swingComponent = new SensorGauge("Test");
        frame.getContentPane().add(swingComponent);

        frame.setVisible(true);
		
		
	}
	
	
		 

}