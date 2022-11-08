package co.edu.udea.backend.service;

import co.edu.udea.backend.model.Device;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.SensorMessage;
import co.edu.udea.backend.repository.HomeRepository;
import co.edu.udea.backend.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class SensorMessageService {

    private final SensorRepository sensorRepository;
    private final HomeRepository homeRepository;


    public SensorMessageService(SensorRepository sensorRepository, DeviceService deviceService, HomeRepository homeRepository) {
        this.sensorRepository = sensorRepository;
        this.homeRepository = homeRepository;
    }

    public void saveSensorMessage(SensorMessage sensorMessage) {
        Optional<Home> OptionalHome = homeRepository.findById(sensorMessage.getHomeName());
        if (OptionalHome.isEmpty()) {
            System.err.printf("home %s does not exist", sensorMessage.getHomeName());
            return;
        }
        Home home = OptionalHome.get();
        Set<Device> homeDevices = home.getDevices();
        try {
            homeDevices.stream()
                    .filter(d -> d.getName().equals(sensorMessage.getDeviceName()))
                    .findFirst().orElseThrow().setLastUpdated(LocalDateTime.now());
        } catch (NoSuchElementException e) {
            System.err.printf("device %s does not exist in home %s",
                    sensorMessage.getDeviceName(),
                    home.getName()
            );
            return;
        }
        home.setDevices(homeDevices);
        homeRepository.save(home);
        sensorRepository.save(sensorMessage);

    }

    public List<SensorMessage> getMessagesByHomeNameAndDeviceName(String homeName, String deviceName) {
        return this.sensorRepository.findByHomeNameAndDeviceName(homeName, deviceName);
    }
}
