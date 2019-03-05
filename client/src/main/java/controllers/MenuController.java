package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

@Component
public class MenuController extends MainController {
    @FXML
    private AnchorPane menupane;

    /**
     * Logs the user out and shows them the login screen.
     * @throws IOException Throws an exception when the main window cannot be found.
     */
    public void logOut() throws IOException {
        System.out.println("Logout called");
        Stage stage = (Stage) menupane.getScene().getWindow();
        Parent root = FXMLLoader.load(getResource("/window1.fxml"));
        stage.setTitle("Go Green");
        stage.setScene(new Scene(root, 600, 500));
        /* stage.show(); */
    }


}
