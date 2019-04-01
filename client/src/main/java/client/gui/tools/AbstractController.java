package client.gui.tools;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class AbstractController {

    private static final SpringFxmlLoader loader = new SpringFxmlLoader();

    /**
     * The method goes back to a provided window (small version).
     *
     * @param returnNode takes fxml node as a reference for the stage
     * @throws IOException when the file does not exist
     */
    protected void goToSmall(Node returnNode, String fileName) throws IOException {
        //Stage stage = new Stage();
        //difference between Window and Stage
        Stage stage = (Stage) returnNode.getScene().getWindow();
        Parent root = (Parent) loader.load("/" + fileName);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    /**
     * The method goes back to a provided window (fit to screen version).
     *
     * @param returnNode takes fxml node as a reference for the stage.
     * @param fileName   of where to go.
     * @throws IOException when file does not exist.
     */
    protected void goToLarge(Node returnNode, String fileName) throws IOException {
        Stage stage = (Stage) returnNode.getScene().getWindow();
        // Parent root = FXMLLoader.load(getClass().getResource("/" + fileName));
        Parent root = (Parent) loader.load("/" + fileName);

        stage.setScene(new Scene(root, 900, 600));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setX((screenSize.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenSize.getHeight() - stage.getHeight()) / 2);
    }

    /**
     * The method displays the toolbar
     * @param myPane
     * @param hamburger
     * @param drawer
     */
    protected void initializeHamburger(Pane myPane,
                                       JFXHamburger hamburger, JFXDrawersStack drawer) {
        drawer.setContent(myPane);
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate( -1 );

        hamburger.addEventHandler( MouseEvent.MOUSE_CLICKED, e -> {
            task.setRate(task.getRate() * -1 );
            task.play();

            if (drawer.isVisible()) {
                drawer.setVisible(false);
            } else {
                drawer.setVisible(true);
            }
        });
    }

}
