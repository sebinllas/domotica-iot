package co.edu.udea.backend.repository;

import co.edu.udea.backend.model.HomeMessage;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface HomeMessageRepository extends MongoRepository<HomeMessage, String> {

    @Aggregation(pipeline = {
            "{ '$match': {'homeName':  ?0, 'deviceName': ?1} }",
            "{ '$sort':  {'dateTime':  -1}}",
            "{ '$limit':  20}"
    })
    List<HomeMessage> findByHomeNameAndDeviceName(String homeName, String deviceName);
}
