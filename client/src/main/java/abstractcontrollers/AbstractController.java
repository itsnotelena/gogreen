package abstractcontrollers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AbstractController {
    /**
     * The method goes back to a provided window.
     *
     * @param returnNode takes a fxml node as a reference for the stage
     * @throws IOException when the file does not exist
     */
    protected void goTo(Node returnNode, String fileName) throws IOException {
        //Stage signup = new Stage();
        //difference between Window and Stage
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/" + fileName));

        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }


}
