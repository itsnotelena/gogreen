package shared.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
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


    public void calculateAndSetLevel(int streak) {
        if (streak >= 28) {
            this.level = 3;
        } else if (streak >= 7) {
            this.level = 2;
        } else if (streak >= 3) {
            this.level = 1;
        } else {
            this.level = 0;
        }
    }
}
