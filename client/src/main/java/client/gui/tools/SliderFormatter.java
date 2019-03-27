package client.gui.tools;

import javafx.util.StringConverter;

public class SliderFormatter extends StringConverter<Double> {

    public Double fromString(String string) {
        return Double.parseDouble(string);
    }

    public String toString(Double value) {
        if (value.equals(-2.0)) {
            return "Off";
        }
        return Integer.toString(value.intValue());
    }
}
