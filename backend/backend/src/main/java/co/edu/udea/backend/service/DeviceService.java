package co.edu.udea.backend.service;

import co.edu.udea.backend.exception.ResourceNotFoundException;
import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.Device;

import co.edu.udea.backend.repository.HomeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {
    private HomeRepository homeRepository;

    public DeviceService(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    public Home registerDevice(String homeName, Device device) {
        Optional<Home> homeOptional = homeRepository.findById(homeName);
        if (homeOptional.isPresent()){
            homeOptional.get().getDevices().add(device);
            return homeRepository.save(homeOptional.get());
        }
        System.err.println("HOME NOT FOUND");
        throw new ResourceNotFoundException("Home not found");
    }

}
