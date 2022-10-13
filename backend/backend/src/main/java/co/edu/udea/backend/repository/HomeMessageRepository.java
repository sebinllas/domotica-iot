package co.edu.udea.backend.repository;

import co.edu.udea.backend.model.HomeMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HomeMessageRepository extends MongoRepository<HomeMessage, String> {

}
