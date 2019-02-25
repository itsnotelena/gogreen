package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class MenuController {
    @FXML
    private TabPane menupane;

    /**
     * Logs the user out and shows them the login screen.
     * @throws IOException Throws an exception when the main window cannot be found.
     */
    public void logOut() throws IOException {
        System.out.println("Logout called");

        Stage stage = (Stage) menupane.getScene().getWindow();
        Parent root = FXMLLoader.load(
            Objects.requireNonNull(getClass().getClassLoader().getResource("window1.fxml")));

        stage.setTitle("Go Green");
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(root, 600, 500));
        /* stage.show(); */
    }
}
