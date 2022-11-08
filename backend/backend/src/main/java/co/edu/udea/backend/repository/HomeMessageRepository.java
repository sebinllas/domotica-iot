package co.edu.udea.backend.repository;

import co.edu.udea.backend.model.HomeMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface HomeMessageRepository extends MongoRepository<HomeMessage, String> {

    @Query("{'homeName': ?0, 'deviceName': ?1}")
    List<HomeMessage> findByHomeNameAndDeviceName(String homeName, String deviceName);
}
