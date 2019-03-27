package shared.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Badge {

    BadgeType type;
    int level;

    public Badge(BadgeType type) {
        this.type = type;
    }

    /**
     * Calculats and set level according to given streak (in days).
     * @param streak the highest streak (in days)
     */
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
