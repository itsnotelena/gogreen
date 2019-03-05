package tools;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class AbstractController {

    /**
     * The method goes back to a provided window (small version).
     *
     * @param returnNode takes a fxml node as a reference for the stage
     * @throws IOException when the file does not exist
     */
    protected void goToSmall(Node returnNode, String fileName) throws IOException {
        //Stage stage = new Stage();
        //difference between Window and Stage
        Stage stage = (Stage) returnNode.getScene().getWindow();
        Parent root = FXMLLoader.load(getResource("/" + fileName));

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    /**
     * The method goes back to a provided window (fit to screen version).
     * @param returnNode fxml node as a reference for the stage.
     * @param fileName of where to go.
     * @throws IOException when file does not exist.
     */
    protected void goToLarge(Node returnNode, String fileName) throws IOException {
        Stage stage = (Stage) returnNode.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/" + fileName));

        stage.setScene(new Scene(root, 900, 600));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setX((screenSize.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenSize.getHeight() - stage.getHeight()) / 2);
    }

}
