#include"Application.h"
#include<iostream>
#include<sstream>
#include<fcntl.h>
#include<stdio.h>
#include<iomanip>
#include<unistd.h>
#include<sys/ioctl.h>
#include<linux/i2c.h>
#include<linux/i2c-dev.h>
#include<fstream>
#include<ctime>
using namespace std;


namespace EE513{
	Application::Application(unsigned int bus, unsigned int device):I2CDevice(bus,device){
		Sync();
		}

	int* Application::getTime() {
		/* Time Array [Seconds, Minutes, Hours] */
		int* TimeGrid= new int[3];
		/* Fetch time register bytes from 0x00 to 0x02 */
		unsigned char* T_Buffer= readRegisters(3,0x00);
		/* Convert from BCD to decimal int */
		TimeGrid[0]=(10*(T_Buffer[0]>>4))+(T_Buffer[0]&0xF);
		TimeGrid[1]=(10*(T_Buffer[1]>>4))+(T_Buffer[1]&0xF);
		unsigned char Hr= T_Buffer[2];
		/* Convert Hours to decimal values */
		TimeGrid[2] = 10 * (Hr >> 4 & 0x1) + (Hr & 0xF);
		/* Hours will always be set to the 24-hour format*/
		/* In case 12 hour format is selected convert 12 to 0 (AM/PM differentiation comes later) */
		if ((Hr >> 6 & 0x1) == 1) {
		    if (TimeGrid[2] == 12) {
		    	TimeGrid[2] = 0;
		    }
		/* Add 12 hours depending on the time of day (AM/PM)*/
		    TimeGrid[2] += (Hr >> 5 & 0x1) * 12;
		}
		else{
			/* In case the system is already set to 24-hour format*/
			TimeGrid[2]+=(20*(Hr>>5 &0x1));
		}
		return TimeGrid;
	}
	
	int* Application::getDate() {
		/* Date Array [Day, Month, Year] */
		int* DateGrid= new int[3];
		/* Read from registers 0x04, 0x05 ,0x06 the set day, month, and year */
		unsigned char* D_Buffer= readRegisters(3,0x04);
		/* Convert Day and month to decimal */
		DateGrid[0]=(10*(D_Buffer[0]>>4))+(D_Buffer[0]&0xF);
		DateGrid[1]=(10*(D_Buffer[1]>>4 &0x1))+(D_Buffer[1]&0xF);
		/* Retrieve century bit from month register, it is assumed that the low state of this bit means the current date is set into the 21st Century (2000s), High state to (2100s)*/
		int Cent=(D_Buffer[1]>>7)+20;
		/* Calculate Years and add up century value *100)*/
		DateGrid[2]=(100*Cent) +(10*(D_Buffer[2]>>4))+(D_Buffer[2]&0xF);
		return DateGrid;
	}
	
	float Application::getTemp() {
		unsigned char* C_Buffer= readRegisters(2,0x11);
		/* Concatenate the temperature's 10 bits, convert and multiply by resolution (0.25)*/
		float temp= (C_Buffer[0]<<2)|(C_Buffer[1]>>6);
		return temp*0.25;
	}

	void  Application::setDate(unsigned int day,unsigned int month,unsigned int year){
		// Validate day and month values
		if(day<=31 & month<=12 ){
			// Convert from decimal int to char Byte BCD representation
			day=static_cast<unsigned char>((day/10)<<4 |(day%10));
			// Derive Century bit
			int Cent=(year/100)-20;
			// Set month register value
			month=static_cast<unsigned char>((Cent<<7 |(month/10)<<4 |(month%10)));
			// set year value to tens and ones only 
			year=year%100;
			// Byte Char BCD
			year=static_cast<unsigned char>((year/10)<<4 |(year%10));
			// Write to registers
			writeRegister(0x04, day);
			writeRegister(0x05, month);
			writeRegister(0x06, year);}
	}

	void   Application::setTime(unsigned int hour,unsigned int minute,unsigned int second){
		// Validate time
		if(second<60 &second<60 & hour<24){
			// Convert to BCD
			second=static_cast<unsigned char>((second/10)<<4 |(second%10));
			minute=static_cast<unsigned char>((minute/10)<<4 |(minute%10));
			hour=static_cast<unsigned char>((hour/10)<<4 |(hour%10));
			// Write to Register
			writeRegister(0x00, second);
			writeRegister(0x01, minute);
			writeRegister(0x02, hour);}
	}
		/* Alarm #1 States:
			 * Mode #1: set off once per second.
			 * Mode #2: Only take seconds into consideration when matching set alarm time and time registers.
			 * Mode #3: Only take seconds and minutes into consideration when matching set alarm time and time registers.
			 * Mode #4: Require second, minute and hour matching  between set alarm time and time registers.
			 * Mode #5: Require second, minute, hour, and day (1-7) matching  between set alarm time and time registers.
			 * Mode #6: Require second, minute, hour, and date (1-31) matching  between set alarm time and time registers.
		* Alarm #2 States:
			 * Mode #7: set off Alarm every minute.
			 * Mode #8: Only take minutes into consideration when matching set alarm time and time registers.
			 * Mode #9: Only take minutes and hours into consideration when matching set alarm time and time registers.
			 * Mode #10: Require  minute, hour, and day (1-7) matching  between set alarm time and time registers.
			 * Mode #11: Require minute, hour, and date (1-31) matching  between set alarm time and time registers. */
		/* num_iter specifies how many cycles of the Alarm are required to execute until the function finishes */
 
	void Application::setAlarm( unsigned int dayt,unsigned int hour, unsigned int minute,unsigned int second , unsigned int mode,unsigned int num_iter){
			/* in case a mode value does not match the expected dayt input (mode 5 should not have dayt values beyond 7 and so does 10)*/
			if(dayt>7 & ( (mode==5)|(mode==10))){mode--;}
			/* input validation */
			if(dayt<=31 & hour<24 & minute<60 & second<60 ){
			/* Initialize Sqaure Wave output at 1Hz for better testability */
			Sq_Wave(1,1);
			/* Initialize time variables to their corresponding BCD values */
			second=((second/10)<<4 |(second%10));
			minute=((minute/10)<<4 |(minute%10));
			hour=((hour/10)<<4 |(hour%10));
			dayt=((dayt/10)<<4 |(dayt%10));
			/* Tackle mode cases where depending on the case the relevant AxMx (e.g. A1M1) bits are set to high */
			switch (mode){
			case 1:
			case 7:
				second= (1<<7);
				minute= (1<<7);
				hour= (1<<7);
				dayt= (1<<7);
				break;
			case 2:
				minute= (1<<7);
				hour= (1<<7);
				dayt= (1<<7);
				break;
			case 3:
			case 8:
				hour= (1<<7);
				dayt= (1<<7);
				break;
			case 4:
			case 9:
				dayt= (1<<7);
				break;
			case 5:
			case 10:
				break;
			case 6:
			case 11:
				dayt|=(1<<6);
				break;
			default:
				break;
			}
			/* convert time variables to Byte Chars*/
			second=static_cast<unsigned char>(second);
			minute=static_cast<unsigned char>(minute);
			hour=static_cast<unsigned char>(hour);
			dayt=static_cast<unsigned char>(dayt);
			/* Get current bits values for register 0x0E for later modification of enable bits while maintaining other bits*/
			unsigned char reg_E =  readRegister(0x0E);
			/* Write Alarm data to relevant register addresses*/
			if(mode>0 & mode<=6){
				/* set A2IE to high*/
				reg_E |=0x01;
				writeRegister(0x07, second);
				writeRegister(0x08, minute);
				writeRegister(0x09, hour);
				writeRegister(0x0A, dayt);}

			if(mode>6 & mode<=11){
				/* set A2IE to high*/
				reg_E |=0x02;
				writeRegister(0x0B, minute);
				writeRegister(0x0C, hour);
				writeRegister(0x0D, dayt);}
			/* Write changes to either A1IE or A2IE*/
			writeRegister(0x0E,reg_E);
			
			/*The external for loop iterates the alarm setting off process according to the defined number of alarm cycles in the input */
			for(int i=1;i<=num_iter; i++){
				/* Waiting loop, continuously check for any changes made to either alarm flags (A1F, A2F). */
				while(true){
					unsigned char reg_val=readRegister(0x0F);
					/* Either alarms was activated then sleep for 2 seconds (switches LED to low) then reset Flag values*/
					if(((reg_val&1) | (reg_val&2 >>1))==1  ){
						sleep(2);
						reg_val&=0xFC;
						writeRegister(0x0F,reg_val);
						break;
						}
					else{continue;}	
						}
				/* Write measurements to logfile*/
				this->LogD();
			
			}
			/* After finishing all rounds, reset all alarm flags and registers back to 0*/
			if(num_iter>0){
			this->DisableAlarm();}
			}	}


		void Application::DisableAlarm(){
			/* Read current values for 0x0E and 0x0F*/
			unsigned char* reg_values=readRegisters(2,0x0E);
			/* Reset alarm time registers*/
			for(int i=0;i<7;i++){
				writeRegister(0x07+i, 0);
			}
			/* Set Interrupt Enable bits and Flag bits to 0*/
			reg_values[0] &=0xFC;
			reg_values[1]&=0xFC;
			writeRegister(0x0E,reg_values[0]);
			writeRegister(0x0F,reg_values[1]);


		}

		void Application::Sq_Wave(bool is_interrupt,unsigned int mode){
			/* Read 0x0E then enable time-matching interrupt control when is_interrupt is set to 1 otherwise switch to square wave */
			unsigned char reg_E=	readRegister(0x0E);
			if(is_interrupt){
			reg_E |=(is_interrupt<<2);}
			else {reg_E &=is_interrupt<<2;}
			/* Specify mode ( mode 1 set frequency to 1Hz, 2 to 1.024kHz, 3 to 4.096kHz, 4 to 8.192kHz) */
			switch(mode){
			case 1:
				reg_E &=0xE7;
				break;
			case 2:
				reg_E &=0xEF;
				break;
			case 3:
				reg_E &=0xF7;
				break;
			case 4:
				reg_E &=0xFF;
				break;
			default:
				reg_E &=0xE7;
				break;

			}
			/* Write changes to 0x0E*/
			writeRegister(0x0E,reg_E);

		}
		void Application::LogD(){
			/* Create or open Alarm_Log in append mode (do not overwrite data)*/
			ofstream logged_alarms("Alarm_Log.txt",ios::app);
			if(logged_alarms.is_open()){
			/* get current clock and temperature values */
			int *Dat=this->getDate();
			int *Tim=this->getTime();
			int temp=this->getTemp();
			/* Write to log file */
			logged_alarms<<"Current Date: "<<Dat[0]<<"/"<<Dat[1]<<"/"<<Dat[2]<<endl;
			logged_alarms<<"Alarm	Time: "<<Tim[2]<<":"<<Tim[1]<<":"<<Tim[0]<<endl;
			logged_alarms<<"Temperature: "<<temp<<endl;
			logged_alarms<<"---------------------------------"<<endl;
			delete Dat;
			delete Tim;
			logged_alarms.close();
			
			}
			else{return;}

			}
		void Application::Sync(){
			/* Create time_t structure and set it to current date-time */
			time_t long_date=time(0);
			/* convert to tm struct to access individual time and date variables */
			tm* tm_date=localtime(&long_date);
			/* set I2C date and time to tm_date values, note that tim() library fetches years as 0 starting from 1900 and months start at 0 as well ) */
			setDate(tm_date->tm_mday ,tm_date->tm_mon+1,tm_date->tm_year+1900);
			setTime(tm_date->tm_hour,tm_date->tm_min,tm_date->tm_sec);
			
			}

}

int main(){

	EE513::Application Testing(1,0x68);
	Testing.setAlarm(5, 1, 1, 10 ,1, 5 );
	Testing.setDate(30,5,2009);
	Testing.setTime(18,35,10);
	int* time=Testing.getTime();
	cout<<time[2]<<":"<<time[1]<<":"<<time[0]<<endl;
	cout<<"************"<<endl;
	int* date=Testing.getDate();
	cout<<date[0]<<"/"<<date[1]<<"/"<<date[2]<<endl;
	cout<<"************"<<endl;
	cout<<Testing.getTemp()<<" C"<<endl;
	Testing.Sq_Wave(1,1);
	delete time;
	delete date;
	return 0;

	}



