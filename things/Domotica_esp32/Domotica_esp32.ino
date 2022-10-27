

/*
IoT Course
Universidad de Antioquia
*/

#include <WiFi.h>
#include <PubSubClient.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <Servo.h>
#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"

#include "config.h"
#include "MQTT.hpp"
#include "ESP32_Utils.hpp"
#include "ESP32_Utils_MQTT.hpp"

unsigned long t_light = 0;
unsigned long t_temp = 0;

int motionSensor = 15;
int lightSensor = 12;
int led = 17;
int fan = 14;
Servo myservo;
const int oneWireBus = 4;

// Things States
int light_state = 0;
int automatic_light = 0;
int fan_state = 0;
int automatic_fan = 0;

char msg[50];

// Set the onewire interface
OneWire oneWire(oneWireBus);

// Set the temp sensor to use the onewire interface
DallasTemperature sensors(&oneWire);

void setup()
{
  WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0); //disable   detector
  pinMode(led, OUTPUT);   // BUILTIN_LED pin as an output
  digitalWrite(led, LOW); // Light initial state is OFF
  myservo.attach(13);     // attaches the servo on pin 13 to the servo object

  // Set the serial monitor baud rate
  Serial.begin(115200);

  sensors.begin();
  ConnectWiFi_STA(false);
  InitMqtt();

  pinMode(motionSensor, INPUT);
  pinMode(lightSensor, INPUT);
  pinMode(led, OUTPUT);
  pinMode(fan, OUTPUT);

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

void loop()
{

  HandleMqtt();

  if (millis() > t_temp + 5000)
  {

    // Read the temperature value
    sensors.requestTemperatures();

    // Temperature read in celcius
    float temperatureC = sensors.getTempCByIndex(0);

    // Write data to serial monitor
    Serial.print("Temperature sensor : ");
    Serial.print(temperatureC);
    Serial.println("°C");

    if (automatic_fan == 1)
    {

      if (temperatureC >= 25)
      {
        digitalWrite(fan, HIGH);
        if (fan_state == 0)
        {
          PublisMqttString("home/fan/0", "1");
        }

        fan_state = 1;
      }
      else
      {
        digitalWrite(fan, LOW);
        if (fan_state == 1)
        {
          PublisMqttString("home/fan/0", "0");
        }

        fan_state = 0;
      }
    }

    // Send temperature value to the MQTT server
    // Device ID for temperature : 2
    snprintf(msg, 50, " % .2f", temperatureC);
    Serial.print("Publish message: ");
    Serial.println(msg);
    PublisMqttString("home/temp", msg);

    t_temp = millis();
  }

  int motionSensorValue = digitalRead(motionSensor);
  int lightSensorValue = digitalRead(lightSensor);
  // Serial.println(lightSensorValue);

  if (automatic_light == 1)
  {
    if (light_state == 1 && millis() > t_light + 5000)
    {
      if (motionSensorValue == HIGH)
      {
        t_light = millis();
      }
      else
      {
        light_state = 0;
        digitalWrite(led, LOW);
        PublisMqttString("home/light/0", "0");
      }
    }
    else
    {
      if (motionSensorValue == HIGH && lightSensorValue == LOW)
      {
        light_state = 1;
        digitalWrite(led, HIGH);
        PublisMqttString("home/light/0", "1");
        t_light = millis();
      }
    }
  }
}
