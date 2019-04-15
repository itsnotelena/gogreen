package shared.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Log implements Serializable {

    private @Id @GeneratedValue long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Column
    private int amount = 1;

    @Column
    private LocalDate date;

    public int getPoints() {
        return action.getPoints() * amount;
    }
}
