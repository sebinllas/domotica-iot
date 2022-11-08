package co.edu.udea.backend.repository;

import co.edu.udea.backend.model.SensorMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends MongoRepository<SensorMessage, String> {
    @Query("{'homeName': ?0, 'deviceName': ?1}")
    List<SensorMessage> findByHomeNameAndDeviceName(String homeName, String deviceName);

}
