package co.edu.udea.backend.service;

import co.edu.udea.backend.model.Sensor;
import co.edu.udea.backend.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorService {
    @Autowired
    private SensorRepository sensorRepository;

    public void saveSensor(Sensor sensor){
         sensorRepository.save(sensor);
    }
}
