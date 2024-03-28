#include"I2CDevice.h"
#ifndef APPLICATION_H_
#define APPLICATION_H_
namespace EE513{
class Application:I2CDevice{
	public:
		/* Initialise with I2C bus and device number*/ 
		Application(unsigned int bus, unsigned int device);
		/* Retrieve array of time measurements (Hours:Minutes:Seconds)*/
		int* getTime();
		/* Retrieve array of Date measurements (DayMonth/Year)*/
		int* getDate();
		/* Retrieve Temperature */
		float getTemp();
		/* Write the desired Time to the I2C (accepts decimal values of time)*/
		void setTime(unsigned int day,unsigned int month,unsigned int year);
		/* Write the desired Date to the I2C (accepts decimal values of dates)*/
		void setDate(unsigned int day,unsigned int month,unsigned int year);
		/* Set alarm parameters
		 * dayt could be either a day (1-7) or date (1-31) depending on the selected mode
		 * Mode comprises of the 11 states of registers for both Alarm #1 (Modes 1 to 6) and Alarm #2 (Modes 7 to 11). They're ordered according to the DS3231 documentation. */
		 
		void setAlarm( unsigned int dayt=0 ,unsigned int hour=0 , unsigned int minute=0 ,unsigned int second=0 , unsigned int mode=1,unsigned int num_iter=1);
		/* Function to Reset any Alarm set (Applies to both alarms 1 and 2)*/
		void DisableAlarm();
		/* Square Wave functionality
		 * Interrupt mode defines whether the INT/SQW outputs a Square Wave or work as a time-set interrupt square wave signal*/
		void Sq_Wave(bool is_interrupt,unsigned int mode);
		
	private:
	    /* This is the unique functionality of this class which logs measurements relating to Date, Time and Temperature into an external txt file upon set off an alarm. */
		void LogD();
		/* Sync function initializes the I2C time and date registers with the fetched current time and date (similar to setting the time using NTP).*/
		void Sync();
};

}

#endif /* APPLICATION_H_ */

