package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "user")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private @Id @GeneratedValue long id;

    @Column
    private String username;

    @Column
    @JsonIgnore
    private String password;

    // For jackson
    private User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
