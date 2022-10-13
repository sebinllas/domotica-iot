package co.edu.udea.backend.service;

import co.edu.udea.backend.exception.ResourceNotFoundException;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.SensorMessage;
import co.edu.udea.backend.repository.HomeRepository;
import co.edu.udea.backend.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SensorMessageService {

    private final SensorRepository sensorRepository;
    private final HomeRepository homeRepository;


    public SensorMessageService(SensorRepository sensorRepository, DeviceService deviceService, HomeRepository homeRepository) {
        this.sensorRepository = sensorRepository;
        this.homeRepository = homeRepository;
    }

    public void saveSensorMessage(SensorMessage sensorMessage) {
        // todo validate home and device exists and throw exception if not
//        Optional<Home> home = homeRepository.findById(sensorMessage.getHomeName());
//        if (home.isEmpty()) {
//            throw new RuntimeException("Home " + sensorMessage.getHomeName() + " does not exist");
//        }
//        if (home.get().getDevices().stream()
//                .noneMatch(device -> device.getName().equals(sensorMessage.getDeviceName()))) {
//            throw new RuntimeException("Device " + sensorMessage.getDeviceName() + " does not exist");
//        }
        sensorRepository.save(sensorMessage);

    }
}
