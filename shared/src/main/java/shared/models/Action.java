package shared.models;

public enum Action {
    VEGETARIAN(465),
    LOCAL(33),
    TEMP(325),
    BIKE(120),
    PUBLIC(50),
    SOLAR(215);

    private int points;

    Action(int points) {
        this.points = points;
    }
    
    public int getPoints() {
        return this.points;
    }


}
