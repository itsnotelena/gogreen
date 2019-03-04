package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
