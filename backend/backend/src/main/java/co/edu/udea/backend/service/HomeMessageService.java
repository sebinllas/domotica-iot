package co.edu.udea.backend.service;

import co.edu.udea.backend.broker.HomeBroker;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.HomeMessage;
import co.edu.udea.backend.repository.HomeRepository;
import co.edu.udea.backend.repository.HomeMessageRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class HomeMessageService {

    private final HomeMessageRepository homeMessageRepository;
    private final HomeRepository homeRepository;


    public HomeMessageService(HomeMessageRepository homeMessageRepository, HomeRepository homeRepository, HomeBroker homeBroker) {
        this.homeMessageRepository = homeMessageRepository;
        this.homeRepository = homeRepository;
        try{
            homeBroker.listen("home_outbound/#", this::messageArrived);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveSensorMessage(HomeMessage homeMessage) {
        Optional<Home> home = homeRepository.findById(homeMessage.getHomeName());
        if (home.isEmpty()) {
            System.err.printf("home %s does not exists\n", homeMessage.getHomeName());
            return;
        }
        if (home.get().getDevices().stream()
                .noneMatch(device -> device.getName().equals(homeMessage.getDeviceName()))) {
            System.err.printf("device %s does not exists in home %s\n", homeMessage.getDeviceName(), homeMessage.getHomeName());
            return;
        }
        homeMessageRepository.save(homeMessage);

    }

    public void messageArrived(String topic, MqttMessage mqttMessage) {
        System.out.printf("%s -> %s\n", topic, mqttMessage);
        HomeMessage homeMessage = new HomeMessage();

        homeMessage.setId(UUID.randomUUID().toString());
        homeMessage.setHomeName(topic.toString().split("/")[1]);
        homeMessage.setDeviceName(topic.toString().split("/", 3)[2]);
        homeMessage.setValue(mqttMessage.toString());
        homeMessage.setDateTime(LocalDateTime.now());
        try {
            this.saveSensorMessage(homeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
