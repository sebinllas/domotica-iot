package co.edu.udea.backend.repository;

import co.edu.udea.backend.model.SensorMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorRepository extends MongoRepository<SensorMessage, String> {

}
