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

    /**
     * String representation of an action.
     * @return returns a string representation of an action
     */
    public String historyString() {
        switch (this) {
            case VEGETARIAN:
                return "ate a vegetarian meal";
            case LOCAL:
                return "bought local produce";
            case TEMP:
                return "lowered the temperature in your home";
            case SOLAR:
                return "installed solar panels";
            case BIKE:
                return "used the bike instead of a car";
            case PUBLIC:
                return "used public transport instead of a car";
            default:
                return "";
        }
    }
}
