
/* 
-Temperature sensor
-Automatic (using a photoresistor and a PIR sensor) and Manual controlled lights
-
-
-
IoT Course
Universidad de Antioquia
*/


#include <WiFi.h> //Library for Wifi connection (included with board info)
#include <PubSubClient.h> // Library for MQTT connection (by Nick O'Leary)
#include <OneWire.h> // by Paul Stoffregen
#include <DallasTemperature.h> // by Miles Burton

#include "config.h"  // network  SSID and password in this file
#include "MQTT.hpp" // MQTT Broker IP in this file
#include "ESP32_Utils.hpp" // WIFI connection
#include "ESP32_Utils_MQTT.hpp" // MQTT connection

unsigned long t_light = 0;
unsigned long t_temp = 0;

//Sensors pins
int motionSensor = 15;
int lightSensor = 12;
const int oneWireBus = 4; //Temp sensor

int led = 17; // light pin

//light state variables
int light_state = 0;
int automatic_light = 0;

// Values captured by sensors
int motionSensorValue = digitalRead(motionSensor);
int lightSensorValue = digitalRead(lightSensor);

char msg[50];

//Set the onewire interface
OneWire oneWire(oneWireBus);

//Set the temp sensor to use the onewire interface
DallasTemperature sensors (&oneWire);

void setup() {
  pinMode(led, OUTPUT);   
  digitalWrite(led, LOW); 

  // Set the serial monitor baud rate
  Serial.begin(115200);

  sensors.begin();
  ConnectWiFi_STA(false);
  InitMqtt();

  pinMode(motionSensor, INPUT);
  pinMode(lightSensor, INPUT);
  pinMode(led, OUTPUT);

  Serial.println("Starting ...");
  digitalWrite(led, LOW);

  while (millis() < 3000)
  {
    digitalWrite(led, HIGH);
    delay(50);
    digitalWrite(led, LOW);
    delay(50);
  }
  Serial.println("Ready");
  digitalWrite(led, LOW);


}


void loop() {

  HandleMqtt();

  
  if (millis() > t_temp + 500) {

    // Read the temperature value
    sensors.requestTemperatures();

    // Temperature read in celcius
    float temperatureC = sensors.getTempCByIndex(0);

    // Write data to serial monitor
    Serial.print("Temperature sensor : ");
    Serial.print(temperatureC);
    Serial.println("°C");

    // Send temperature value to the MQTT server
    // Device ID for temperature : 2
    snprintf (msg, 50, "2,%.2f", temperatureC);
    Serial.print("Publish message: ");
    Serial.println(msg);
    PublisMqttString("home", msg);
    
    t_temp = millis();

  }

  if (automatic_light == 1) {
    if (light_state == 1 && millis() > t_light + 5000) {
      if (motionSensorValue == HIGH) {
        t_light = millis();
      }
      else {
        light_state = 0;
        digitalWrite(led, LOW);
        PublisMqttString("home/light", "0");
      }

    }
    else {
      if (motionSensorValue == HIGH  && lightSensorValue == LOW) {
        light_state = 1;
        digitalWrite(led, HIGH);
        PublisMqttString("home/light", "1");
        t_light = millis();
      }

    }
  }
}
