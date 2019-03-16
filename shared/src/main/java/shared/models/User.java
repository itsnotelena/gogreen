package shared.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private @Id @GeneratedValue long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String email;

    @Column
    private Boolean hasSolarPanels = false;

    @ManyToMany
    private Set<User> friends;

    @Column
    private long foodPoints;
}
