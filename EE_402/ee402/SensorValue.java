//SensorValue.java
package ee402;
import java.io.*;
@SuppressWarnings("serial")
public class SensorValue implements Serializable
{
   private double co2=0,ch4=0,celsius=0;
   private String devName="";


   public SensorValue(double co2,double ch4,double celsius,String devName) 
   {
this.co2=co2;
this.ch4=ch4;
this.celsius=celsius;
this.devName=devName;

   }
   public double getCO2()
   {
 return this.co2;	
   }
   public double getCH4()
   {
 return this.ch4;	
   }
   public double getCelsius()
   {
 return this.celsius;	
   }
   public String getDevice() {return this.devName;}

   
   
}