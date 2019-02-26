package abstractcontrollers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class AbstractController {
    /**
     * The method goes back to a provided window.
     *
     * @param returnButton takes a fxml button as a reference for the stage
     * @throws IOException when the file does not exist
     */
    public void goBack(Button returnButton) throws IOException {
//        Stage signup = new Stage();
        //difference between Window and Stage
        Stage signup = (Stage)returnButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getResource("/window1.fxml"));

        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);

    }

}
