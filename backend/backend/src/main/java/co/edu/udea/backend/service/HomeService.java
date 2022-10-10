package co.edu.udea.backend.service;

import co.edu.udea.backend.broker.HomePublisher;
import co.edu.udea.backend.model.Device;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.Message;
import co.edu.udea.backend.repository.HomeRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class HomeService {
    private HomeRepository homeRepository;
    private HomePublisher homePublisher;

    public HomeService(HomeRepository homeRepository, HomePublisher homePublisher) {

        this.homeRepository = homeRepository;
        this.homePublisher = homePublisher;
    }

    public List<Home> findAllHomes() {
        return homeRepository.findAll();
    }

    public Home registerHome(Home home) {
        return homeRepository.save(home);
    }

    public void sendMessage(String homeName, String deviceName, String payload) {
        Message message = new Message(deviceName, payload);
        this.sendMessage(homeName, Arrays.asList(message));
    }

    public void sendMessage(String homeName, List<Message> messages) {
        //TODO query homerepository to verify whether or not the home exists
        Optional<Home> homeOptional = homeRepository.findById(homeName);

        if (!homeOptional.isPresent()) {
            System.err.println("A MESSAGE TO AN UNKNOWN HOME HAS BEEN RECEIVED {" + homeName + "}");
            return;
        }
        Home home = homeOptional.get();

        if (Home.Status.OFFLINE.equals(home.getStatus())) {
            System.err.println("HOME IS OFFLINE (CANNOT RECEIVE MESSAGES) {" + homeName + "}");
            return;
        }

        //TODO query devicerepository to verify devices existence

        StringBuilder sb = new StringBuilder();
        messages.forEach(message -> sb.append(message.getDeviceName()).append(",").append(message.getPayload()));
    }

    public void sendMessage(String message) {
        try {
            this.homePublisher.publish(message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
