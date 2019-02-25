package abstractcontrollers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

}
