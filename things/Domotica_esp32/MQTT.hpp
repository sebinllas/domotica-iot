#include <string.h>

const char *MQTT_BROKER_ADRESS = "192.168.25.241";
const uint16_t MQTT_PORT = 1883;
const char *MQTT_CLIENT_NAME = "ESPClient_1";

// Topics
const char *DEVICE_TYPE = "Light";
const char *DEVICE_ID = "0";

extern int led;
extern int light;
extern int automatic_light;
extern Servo myservo;
extern int fan;
extern int fan_state;
extern int automatic_fan;

int pos = 0;

WiFiClient espClient;
PubSubClient mqttClient(espClient);

void SuscribeMqtt()
{
  mqttClient.subscribe("room/#");
}

String payload;
void PublisMqtt(unsigned long data)
{
  payload = "";
  payload = String(data);
  mqttClient.publish("hello/world", (char *)payload.c_str());
}

void PublisMqttString(char *topic, char *msg)
{
  mqttClient.publish(topic, msg);
}

String content = "";
void OnMqttReceived(char *topic, byte *payload, unsigned int length)
{

  Serial.print("Received on ");
  Serial.print(topic);
  Serial.print(": ");
  content = "";
  for (size_t i = 0; i < length; i++)
  {
    content.concat((char)payload[i]);
  }
  Serial.print(content);
  Serial.println();

  if (strcmp(topic, "room/light/0/automatic") == 0)
  {
    Serial.print("message recived on room/light/0/automatic: ");
    Serial.println((char)payload[0]);

    if ((char)payload[0] == '1')
    {
      automatic_light = 1;
      Serial.print("Automatic mode enabled: ");
      Serial.println(automatic_light);
    }
    else
    {
      automatic_light = 0;
      digitalWrite(led, LOW);
      Serial.print("Automatic mode desabled: ");
      Serial.println(automatic_light);
    }
  }
  else if (strcmp(topic, "room/light/0") == 0)
  {
    automatic_light = 0;
    Serial.print("message recived on room/light/0: ");
    Serial.println((char)payload[0]);

    if ((char)payload[0] == '1')
    {
      light = 1;
      Serial.println("recived signal to turn on light ");

      digitalWrite(led, HIGH);
      PublisMqttString("home/light/0", "1");
    }
    else if ((char)payload[0] == '0')
    {
      light = 0;
      Serial.println("recived signal to turn off light ");
      digitalWrite(led, LOW);
      PublisMqttString("home/light/0", "0");
    }
    else if ((char)payload[0] == '2')
    {

      automatic_light = 0;
      if (light == 1)
      {
        light = 0;
        digitalWrite(led, LOW);
      }
      else
      {
        light = 1;
        digitalWrite(led, HIGH);
      }
    }
  }
  else if (strcmp(topic, "room/fan/0") == 0)
  {
    if ((char)payload[0] == '1')
    {
      fan_state = 1;
      Serial.println("recived signal to turn on the fan ");
      digitalWrite(fan, HIGH);
      PublisMqttString("home/fan/0", "1");
    }
    else if ((char)payload[0] == '0')
    {
      fan_state = 0;
      Serial.println("recived signal to turn off the fan ");
      digitalWrite(fan, LOW);
      PublisMqttString("home/fan/0", "0");
    }
  }
  else if (strcmp(topic, "room/fan/0/automatic") == 0)
  {
    if ((char)payload[0] == '1')
    {
      automatic_fan = 1;
      Serial.print("recived signal to turn on the automatic behavior of fan: ");
      Serial.println(automatic_fan);
      digitalWrite(fan, LOW);
    }
    else if ((char)payload[0] == '0')
    {
      automatic_fan = 0;
      Serial.print("recived signal to turn off the automatic behavior of fan: ");
      Serial.println(automatic_fan);
      digitalWrite(fan, LOW);
    }
  }
  else if (strcmp(topic, "room/door/0") == 0)
  {
    if ((char)payload[0] == '1')
    {
      Serial.println("recived signal to open the door ");
      PublisMqttString("home/door/0", "1");
      myservo.write(183);
    }
    else if ((char)payload[0] == '0')
    {
      Serial.println("recived signal to close the door ");
      PublisMqttString("home/door/0", "0");
      myservo.write(18);
    }
  }
}