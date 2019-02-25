package abstractcontrollers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class AbstractController {
    /**
     * The method goes back to a provided window.
     *
     * @param fileName takes a fxml file name to go back to
     * @throws IOException when the file does not exist
     */
    public void goBack(String fileName) throws IOException {
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getResource(fileName));

        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);

    }

    /**
     * @param fileName
     * @param btn
     * @throws IOException
     */
    public void goBack(String fileName, Control btn) throws IOException {
        Stage stage = (Stage) btn.getScene().getWindow();
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource(fileName)));

        stage.setTitle("Go Green");
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(root, 600, 500));
    }

}
