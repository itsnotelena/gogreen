package shared.models;

import lombok.Getter;
import lombok.Setter;


public enum Badges {
    Vegetarian,
    Local,
    Bike,
    Public,
    Solar,
    Temp;

    @Getter
    @Setter
    private int level;

}
