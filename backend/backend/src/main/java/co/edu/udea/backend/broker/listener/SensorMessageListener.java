package co.edu.udea.backend.broker.listener;

import co.edu.udea.backend.broker.HomeBroker;
import co.edu.udea.backend.model.SensorMessage;
import co.edu.udea.backend.service.SensorMessageService;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class SensorMessageListener implements IMqttMessageListener {
    private final SensorMessageService sensorMessageService;

    public SensorMessageListener(HomeBroker homeBroker, SensorMessageService sensorMessageService) throws Exception {
        this.sensorMessageService = sensorMessageService;
        homeBroker.listen("home_outbound/#", this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        System.out.printf("%s -> %s\n", topic, mqttMessage);
        SensorMessage sensorMessage = new SensorMessage();

        sensorMessage.setId(UUID.randomUUID().toString());
        sensorMessage.setHomeName(topic.split("/")[1]);
        sensorMessage.setDeviceName(topic.split("/", 3)[2]);
        sensorMessage.setValue(mqttMessage.toString());
        sensorMessage.setLastUpdated(LocalDateTime.now());

        // TODO resend messages to frontend via webPublisher
        try {
            System.out.println(sensorMessage.toString());
            sensorMessageService.saveSensorMessage(sensorMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

