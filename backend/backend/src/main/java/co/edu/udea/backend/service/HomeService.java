package co.edu.udea.backend.service;

import co.edu.udea.backend.broker.HomeBroker;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.Message;
import co.edu.udea.backend.repository.HomeRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HomeService {
    private final HomeRepository homeRepository;
    private final HomeBroker homeBroker;

    public HomeService(HomeRepository homeRepository, HomeBroker homeBroker) {

        this.homeRepository = homeRepository;
        this.homeBroker = homeBroker;
    }

    public List<Home> findAllHomes() {
        return homeRepository.findAll();
    }

    public Home registerHome(Home home) {
        return homeRepository.save(home);
    }

    public void sendMessage(String homeName, String deviceName, String payload) {
        Message message = new Message(deviceName, payload);
        //this.sendMessage(homeName, List.of(message));
        String topic = String.format("home_inbound/%s/%s", homeName, deviceName);
        try{
            this.homeBroker.publish(topic, payload);
        }catch (MqttException e){
            System.err.println("Mqtt Exception");
            e.printStackTrace();
        }

    }

    public void sendMessage(String homeName, List<Message> messages) {
        Optional<Home> homeOptional = homeRepository.findById(homeName);

        if (homeOptional.isEmpty()) {
            System.err.println("A MESSAGE TO AN UNKNOWN HOME HAS BEEN RECEIVED {" + homeName + "}");
            return;
        }
        Home home = homeOptional.get();

        if (Home.Status.OFFLINE.equals(home.getStatus())) {
            System.err.println("HOME IS OFFLINE (CANNOT RECEIVE MESSAGES) {" + homeName + "}");
            return;
        }

        StringBuilder sb = new StringBuilder();
        messages.forEach(message -> sb.append(message.getDeviceName()).append(",").append(message.getPayload()));
    }

    public void sendMessage(String message) {
        try {
            this.homeBroker.publish(message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
