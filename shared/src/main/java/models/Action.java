package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "action")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action implements Serializable {

    private @Id @GeneratedValue long id;

    @Column
    private String name;

    @Column
    private int points;
}
