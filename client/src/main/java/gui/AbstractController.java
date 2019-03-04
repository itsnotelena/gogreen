package gui;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractController {

    /**
     * A simplified version of: {@link #goBack(String, Control)}. Which opens a new window instead
     * of replacing the current scene
     */
    void goBack(String fileName) throws IOException {
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getResource(fileName));

        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }

    /**
     * The method goes back to the provided window and
     * uses the provided control to get the current scene.
     * @param fileName Window location to go back to
     * @param ctrl control to get the scene from
     * @throws IOException Throws an IOException when provided window can not be found
     */
    void goBack(String fileName, Control ctrl) throws IOException {
        Stage stage = (Stage) ctrl.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fileName));

        stage.setTitle("Go Green");
        // Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(root, 600, 500));
    }

}
