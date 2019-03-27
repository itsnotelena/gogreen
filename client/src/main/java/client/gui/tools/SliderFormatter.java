package client.gui.tools;

import javafx.util.StringConverter;

public class SliderFormatter extends StringConverter<Double> {

    public Double fromString(String string) {
        return Double.parseDouble(string);
    }

    /**
     * Formats the ticks on the slider.
     * @param value The value of the number on the slider
     * @return The number if it's different than 4, "Off" otherwise
     */
    public String toString(Double value) {
        if (value.equals(4.0)) {
            return "Off";
        }
        return Integer.toString(value.intValue());
    }
}
