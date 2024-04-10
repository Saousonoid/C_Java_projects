// Based on the Paho C code example from www.eclipse.org/paho/
#include <iostream>
#include <sstream>
#include <fstream>
#include <string.h>
#include <unistd.h>
#include <ctime>
#include "MQTTClient.h"
#include "ADXL345.h"
#define  CPU_TEMP "/sys/class/thermal/thermal_zone0/temp"
using namespace std;
using namespace exploringRPi;
//Please replace the following address with the address of your server
#define ADDRESS    "tcp://172.16.2.33:1883"
#define CLIENTID   "pub2"
#define AUTHMETHOD "saria"
#define AUTHTOKEN  "password"
#define TOPIC      "ee513/conv"
/* QOS set to at most receive a message once */
#define QOS        0
#define TIMEOUT    10000L

float getCPUTemperature() {        // get the CPU temperature
   int cpuTemp;                    // store as an int
   fstream fs;
   fs.open(CPU_TEMP, fstream::in); // read from the file
   fs >> cpuTemp;
   fs.close();
   return (((float)cpuTemp)/1000);
}

/* Retrieve Sensor measurments for pitch, roll, current time and acceleration around the X axis*/
char* getJSON(ADXL345* sensor) {
    char* json = new char[500];
    sensor->readSensorState();

    time_t long_date = time(0);
    struct tm* tm_date = localtime(&long_date);

    snprintf(json, 500,
             "{\"cpu_temperature\": %f, \"pitch\": %f, \"roll\": %f, \"acceleration_x\": %f,\"time\": \"%d-%02d-%02d %02d:%02d:%02d\"}",
             getCPUTemperature(), sensor->getPitch(), sensor->getRoll(), (double)sensor->getAccelerationX(),
             tm_date->tm_year + 1900, tm_date->tm_mon + 1, tm_date->tm_mday,
             tm_date->tm_hour, tm_date->tm_min, tm_date->tm_sec);

    return json;
}

int main(int argc, char* argv[]) {
   char* str_payload;          
   MQTTClient client;
   MQTTClient_connectOptions opts = MQTTClient_connectOptions_initializer;
   MQTTClient_message pubmsg = MQTTClient_message_initializer;
   MQTTClient_deliveryToken token;
   MQTTClient_create(&client, ADDRESS, CLIENTID, MQTTCLIENT_PERSISTENCE_NONE, NULL);
   opts.keepAliveInterval = 20;
/* Peristent session to maintain state after disconnection  */ 
  opts.cleansession = 0;
   opts.username = AUTHMETHOD;
   opts.password = AUTHTOKEN;
 
// Configure will message parameters
   MQTTClient_willOptions will;
   will = MQTTClient_willOptions_initializer;
   will.message = "Connection Ended Abruptly";
   will.qos = QOS;
   will.retained = 0;
   will.topicName = TOPIC;
   opts.will = &will;   

   int rc;
   if ((rc = MQTTClient_connect(client, &opts)) != MQTTCLIENT_SUCCESS) {
      cout << "Failed to connect, return code " << rc << endl;
      return -1;
   }
   /* Instantiate a ADXL345 object pointer to continuously receive readings*/
   ADXL345* sensor = new ADXL345(1, 0x53);
   sensor->setResolution(ADXL345::NORMAL);
   sensor->setRange(ADXL345::PLUSMINUS_4_G);
/* Continuously publish JSON payload every 5 seconds */
      while (true) {
        str_payload = getJSON(sensor);
	cout<<str_payload<<endl;
        pubmsg.payload = str_payload;
        pubmsg.payloadlen = strlen(str_payload);
        pubmsg.qos = QOS;
        pubmsg.retained = 0;

        MQTTClient_publishMessage(client, TOPIC, &pubmsg, &token);
        cout << "Waiting for up to " << (int)(TIMEOUT/1000) <<
            " seconds for publication of " << str_payload <<
            " \non topic " << TOPIC << " for ClientID: " << CLIENTID << endl;
        rc = MQTTClient_waitForCompletion(client, token, TIMEOUT);
        cout << "Message with token " << (int)token << " delivered." << endl;

        //  5 seconds
        usleep(5000000);
    }
    /* delete payload memory allocation and sensor ptr*/
   free(str_payload);
   delete sensor;
   MQTTClient_disconnect(client, 10000);
   MQTTClient_destroy(&client);
   return rc;
}

