package co.edu.udea.backend.service;

import co.edu.udea.backend.broker.HomeBroker;
import co.edu.udea.backend.exception.ResourceNotFoundException;
import co.edu.udea.backend.model.Device;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.HomeMessage;
import co.edu.udea.backend.repository.HomeRepository;
import co.edu.udea.backend.repository.HomeMessageRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public void saveMessage(HomeMessage homeMessage) {
        Optional<Home> OptionalHome = homeRepository.findById(homeMessage.getHomeName());
        if (OptionalHome.isEmpty()) {
            System.err.printf("home %s does not exist", homeMessage.getHomeName());
            return;
        }
        Home home = OptionalHome.get();
        Set<Device> homeDevices = home.getDevices();
        try {
            homeDevices.stream()
                    .filter(d -> d.getName().equals(homeMessage.getDeviceName()))
                    .findFirst().orElseThrow().setLastUpdated(LocalDateTime.now());
        } catch (NoSuchElementException e) {
            System.err.printf("device %s does not exist in home %s",
                    homeMessage.getDeviceName(),
                    home.getName()
            );
            return;
        }
        home.setDevices(homeDevices);
        homeRepository.save(home);
        homeMessageRepository.save(homeMessage);

    }

    public List<HomeMessage> getMessagesByHomeNameAndDeviceName(String homeName, String deviceName) {
        Optional<Home> OptionalHome = homeRepository.findById(homeName);
        if(OptionalHome.isEmpty()){
            System.err.printf("home %s does not exist", homeName);
            throw new ResourceNotFoundException(String.format("home %s does not exist", homeName));
        } else if (OptionalHome.get().getDevices().stream().noneMatch(d -> d.getName().equals(deviceName))) {
            System.err.printf("device %s does not exist in home %s", deviceName, homeName);
            throw new ResourceNotFoundException(String.format("requested device does not exist in home %s", homeName));
        }
        return this.homeMessageRepository.findByHomeNameAndDeviceName(homeName, deviceName);
    }

    public void messageArrived(String topic, MqttMessage mqttMessage) {
        System.out.printf("%s -> %s\n", topic, mqttMessage);
        HomeMessage homeMessage = new HomeMessage();

        homeMessage.setId(UUID.randomUUID().toString());
        homeMessage.setHomeName(topic.split("/")[1]);
        homeMessage.setDeviceName(topic.split("/", 3)[2]);
        homeMessage.setValue(mqttMessage.toString());
        homeMessage.setDateTime(LocalDateTime.now());

        // TODO resend messages to frontend via webPublisher
        try {
            System.out.println(homeMessage.toString());
            this.saveMessage(homeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
