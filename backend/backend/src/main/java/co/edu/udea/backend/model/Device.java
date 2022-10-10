package co.edu.udea.backend.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
public class Device {
    @MongoId
    private String name;
    private String status;
    private LocalDateTime lastUpdated;
}
