#include <string.h>

const char* MQTT_BROKER_ADRESS = "192.168.25.125";
const uint16_t MQTT_PORT = 1883;
const char* MQTT_CLIENT_NAME = "ESPClient_1";

// Topics
const char* DEVICE_TYPE = "Light";
const char* DEVICE_ID = "0";
#define RELAY 17

extern int light;
extern int automatic_light;

WiFiClient espClient;
PubSubClient mqttClient(espClient);

void SuscribeMqtt()
{
  mqttClient.subscribe("Light/#");
}

String payload;
void PublisMqtt(unsigned long data)
{
  payload = "";
  payload = String(data);
  mqttClient.publish("hello/world", (char*)payload.c_str());
}

void PublisMqttString(char* topic, char* msg)
{
  mqttClient.publish(topic, msg);
}

String content = "";
void OnMqttReceived(char* topic, byte* payload, unsigned int length)
{

  Serial.print("Received on ");
  Serial.print(topic);
  Serial.print(": ");
  content = "";
  for (size_t i = 0; i < length; i++) {
    content.concat((char)payload[i]);
  }
  Serial.print(content);
  Serial.println();

  if (strcmp(topic, "Light/automatic")) {
    Serial.print("message recived on Light/automatic: ");
    Serial.println((char)payload[0]);

    if ((char)payload[0] == '1') {
      automatic_light = 1;
      Serial.print("Automatic mode enabled: ");
      Serial.println(automatic_light);

    }
    else {
      automatic_light = 0;
      digitalWrite(RELAY, HIGH);
      Serial.print("Automatic mode desabled: ");
      Serial.println(automatic_light);
    }
  }
  else if (strcmp(topic, "Light/0")) {
    automatic_light = 0;
    Serial.print("message recived on Light/0: ");
    Serial.println((char)payload[0]);
    
    if ((char)payload[0] == '1') {
      light = 1;
      Serial.println("recived signal to turn on light ");
      digitalWrite(RELAY, HIGH);
    }
    else if ((char)payload[0] == '0') {
      light = 0;
      Serial.println("recived signal to turn off light ");
      digitalWrite(RELAY, LOW);
    }
    else if ((char)payload[0] == '2') {

      automatic_light = 0;
      // Toggle light
      if (light == 1) {
        light = 0;
        digitalWrite(RELAY, LOW);
      }
      else {
        light = 1;
        digitalWrite(RELAY, HIGH);
      }
    }
  }
}
