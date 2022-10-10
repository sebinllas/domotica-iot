package co.edu.udea.backend.repository;

import co.edu.udea.backend.model.Device;
import co.edu.udea.backend.model.Home;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends MongoRepository<Home, String> {

}
