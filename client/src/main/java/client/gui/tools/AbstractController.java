package client.gui.tools;

import com.jfoenix.controls.JFXDrawer;
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

    protected void initializeHamburger(HamburgerSlideCloseTransition task,
                                       JFXHamburger hamburger, JFXDrawer drawer) {
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            task.setRate(task.getRate() * -1);
            task.play();

            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });
    }


    /**
     * The method allows to display new buttons (used in Discover People fxml file).
     * @param pane Pane to show.
     */
    protected void showBtn(Pane pane) {
        if(pane.isVisible()) {

            pane.setVisible(false);
            pane.setVisible(false);
            pane.setVisible(false);

        } else {
            pane.setVisible(true);
            pane.setVisible(true);
            pane.setVisible(true);

        }
    }

}
