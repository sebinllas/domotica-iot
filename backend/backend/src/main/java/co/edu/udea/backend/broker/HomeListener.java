package co.edu.udea.backend.broker;

import co.edu.udea.backend.model.Sensor;
import co.edu.udea.backend.service.SensorService;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class HomeListener {

    @Autowired
    SensorService sensorService;

    public HomeListener() throws MqttException {
        String listenerId = UUID.randomUUID().toString();
        IMqttClient listener = new MqttClient("tcp://localhost:1883", listenerId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        listener.connect(options);
        listener.subscribe("home_outbound/#", this::processMessage);
    }

    private void processMessage(String topic, MqttMessage message) {
        System.out.printf("%s -> %s", topic, message);
        Sensor sensor = new Sensor();
        sensor.setName(topic.toString().split("/")[1]);
        sensor.setValue(message.toString());
        sensor.setLastUpdated(LocalDateTime.now());
        sensorService.saveSensor(sensor);
    }


}