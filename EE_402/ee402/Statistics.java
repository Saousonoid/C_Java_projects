//Statistics.java
package ee402;
import java.util.HashSet;
import java.util.Set;

public class Statistics extends Thread{
private int historycount=0,count=0;
private double co2_avg=0,ch4_avg=0,temp_avg=0;
private static int timeWindow=0;
private SensorValue[] histReadings= new SensorValue[10];
private int[] histWindows= new int[10];
private SensorValue[] instReadings= new SensorValue[30];
private double [] min= new double[3];
private double []max=new double[3];
private Set<String> deviceNames=new HashSet<>();
public Statistics()
{

this.init();

}

public synchronized void setInstant(SensorValue sens) {
this.instReadings[count]=sens;
this.deviceNames.add(sens.getDevice());
this.count+=1;

}
public synchronized void getInstantAvg()
{
if(count>0) {
for(int i=0;i<count;i++) {
this.co2_avg+= this.instReadings[i].getCO2();
this.ch4_avg += this.instReadings[i].getCH4();
this.temp_avg+= this.instReadings[i].getCelsius();}
this.co2_avg=co2_avg/count;
this.ch4_avg=ch4_avg/count;
this.temp_avg=temp_avg/count;
calculateMinMax();
}
histReadings[historycount]=new SensorValue(co2_avg,ch4_avg,temp_avg,"");
timeWindow++;
histWindows[historycount]=timeWindow;
historycount++;
if(historycount>9) {
historycount=0;}

}

private void calculateMinMax() {
double co2_min=0,ch4_min=0,temp_min=0,co2_max=0,ch4_max=0,temp_max=0;
co2_max=co2_min=instReadings[0].getCO2();
ch4_max=ch4_min=instReadings[0].getCH4();
temp_max=temp_min=instReadings[0].getCelsius();
System.out.println(count);
for (int j=0;j<count;j++) {
if(co2_min>instReadings[j].getCO2()) {
co2_min=instReadings[j].getCO2();
}
if(ch4_min>instReadings[j].getCH4()) {
ch4_min=instReadings[j].getCH4();
}
if(temp_min>instReadings[j].getCelsius()) {
temp_min=instReadings[j].getCelsius();
}
if(co2_max<instReadings[j].getCO2()) {
co2_max=instReadings[j].getCO2();
}
if(ch4_max<instReadings[j].getCH4()) {
ch4_max=instReadings[j].getCH4();
}
if(temp_max<instReadings[j].getCelsius()) {
temp_max=instReadings[j].getCelsius();
}
}

this.min[0]=co2_min;
this.min[1]=ch4_min;
this.min[2]=temp_min;
this.max[0]=co2_max;
this.max[1]=ch4_max;
this.max[2]=temp_max;
System.out.println(this.min[0]);
System.out.println(this.max[0]);
}

public void resetAll() {
this.count=0;
this.instReadings=new SensorValue[10];
this.init();
this.deviceNames=new HashSet<>();
}

public double getCO2Avg() {
return this.co2_avg;

}
public double getCh4Avg() {
return this.ch4_avg;

}
public double getCelsiusAvg() {
return this.temp_avg;

}
public double [] getMin() {
return this.min;
}
public double [] getMax() {
return this.max;
}
public Set<String> getConnected() {
return this.deviceNames;
}
public SensorValue[] getTenReadings() {
if(this.histReadings[9]!=null) {
return this.histReadings;}
return null;

}
private void init() {
this.ch4_avg=0;
this.co2_avg=0;
this.temp_avg=0;
this.min=new double[]{0.0,0.0,0.0};
this.max=new double[]{0.0,0.0,0.0};
this.deviceNames.clear();;
}

public int [] getWindow() {return histWindows;}

}