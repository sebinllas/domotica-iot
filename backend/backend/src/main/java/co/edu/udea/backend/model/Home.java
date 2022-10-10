package co.edu.udea.backend.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Data
@Document("homes")
public class Home {
    @MongoId
    private String name;
    private String status;
    private Set<Device> devices;

    public Status getStatus() {
        return Status.valueOf(status);
    }
    public void setStatus(Status status) {
        this.status = status.name();
    }
    public enum Status {
        ONLINE, OFFLINE;
    }
}
