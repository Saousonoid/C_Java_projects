#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "MQTTClient.h"
#include <json-c/json.h>
#include <iostream>
#include <cmath>
#define ADDRESS     "tcp://172.16.2.33:1883"
#define CLIENTID    "sub1"
#define AUTHMETHOD  "saria"
#define AUTHTOKEN   "password"
/* Listen to messages published on sob subtopic*/
#define TOPIC       "ee513/sob"
#define PAYLOAD     "Hello World!"
#define QOS         1
#define TIMEOUT     10000L
#define LFT_LED     0
#define RGT_LED    2
#include <wiringPi.h>
using namespace std;
volatile MQTTClient_deliveryToken deliveredtoken;

void delivered(void *context, MQTTClient_deliveryToken dt) {
    printf("Message with token value %d delivery confirmed\n", dt);
    deliveredtoken = dt;
}

void connlost(void *context, char *cause) {
    printf("\nConnection lost\n");
    printf("     cause: %s\n", cause);
}
/* Structure to hold JSON payload data */
struct SensVal {
    float temperature;
    float pitch;
    float roll;
    short accelerationX;
    const char* systemTime;
};
/* Parse Incoming JSON to process individual  measurmenets in MotionTest()*/
struct SensVal parseJSON(char* json) {
    struct SensVal values;
    json_object* parsedJson = json_tokener_parse(json);
    json_object* tempObj = json_object_object_get(parsedJson, "temperature");
    values.temperature = (float)json_object_get_double(tempObj);

    tempObj = json_object_object_get(parsedJson, "pitch");
    values.pitch = (float)json_object_get_double(tempObj);

    tempObj = json_object_object_get(parsedJson, "roll");
    values.roll = (float)json_object_get_double(tempObj);

    tempObj = json_object_object_get(parsedJson, "acceleration_x");
    values.accelerationX = (short)json_object_get_int(tempObj);

    tempObj = json_object_object_get(parsedJson, "system_time");
    values.systemTime = json_object_get_string(tempObj);

    json_object_put(parsedJson);
    
    return values;
}




/* Sobriety Test to check  for any leaning higher than 24C, depending on the side an LED will light up*/
void MotionTest(const struct SensVal& data){
if(data.roll>=25 ){
   digitalWrite(LFT_LED, HIGH);
   digitalWrite(RGT_LED, LOW);
   cout<<"condition for lft satisfied"<<endl;
}
else if(data.roll<=-25){
        digitalWrite(LFT_LED, LOW);
	digitalWrite(RGT_LED,HIGH);
 cout<<"condition for rgt satisfied"<<endl;
}
else {
        digitalWrite(LFT_LED, LOW);
        digitalWrite(RGT_LED, LOW);

}

}


int msgarrvd(void *context, char *topicName, int topicLen, MQTTClient_message *message) {
    int i;
    char* payloadptr;
    printf("Message arrived\n");
    printf("     topic: %s\n", topicName);
    printf("   message: ");
    payloadptr = (char*) message->payload;
    struct SensVal data=parseJSON(payloadptr);
    MotionTest(data);
    for(i=0; i<message->payloadlen; i++) {
        putchar(*payloadptr++);
    }
    putchar('\n');
    MQTTClient_freeMessage(&message);
    MQTTClient_free(topicName);
    return 1;
}

int main(int argc, char* argv[]) {
    MQTTClient client;
    MQTTClient_connectOptions opts = MQTTClient_connectOptions_initializer;
    int rc;
    int ch;
/* Initialize reading and setting GPIO pins to output voltage, when failed return 0*/
   if (wiringPiSetup() != -1) { 
    pinMode(RGT_LED, OUTPUT);
    pinMode(LFT_LED, OUTPUT);
    digitalWrite(LFT_LED, LOW);
    digitalWrite(RGT_LED, LOW);
    MQTTClient_create(&client, ADDRESS, CLIENTID, MQTTCLIENT_PERSISTENCE_NONE, NULL);
    opts.keepAliveInterval = 20;
    opts.cleansession = 0;
    opts.username = AUTHMETHOD;
    opts.password = AUTHTOKEN;
    MQTTClient_setCallbacks(client, NULL, connlost, msgarrvd, delivered);
    if ((rc = MQTTClient_connect(client, &opts)) != MQTTCLIENT_SUCCESS) {
        printf("Failed to connect, return code %d\n", rc);
        exit(-1);
    }
    printf("Subscribing to topic %s\nfor client %s using QoS%d\n\n"
           "Press Q<Enter> to quit\n\n", TOPIC, CLIENTID, QOS);
    MQTTClient_subscribe(client, TOPIC, QOS);

    do {
        ch = getchar();
    } while(ch!='Q' && ch != 'q');
    MQTTClient_disconnect(client, 10000);
    MQTTClient_destroy(&client);
    return rc;
}
return 0;

}

