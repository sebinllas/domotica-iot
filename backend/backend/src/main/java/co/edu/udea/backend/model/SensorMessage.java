package co.edu.udea.backend.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document("messages")
public class SensorMessage {
    @MongoId
    private String id;

    private String deviceName;
    private String homeName;
    private String value;
    private LocalDateTime lastUpdated;
}
