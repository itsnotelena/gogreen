package controllers;

import abstractcontrollers.AbstractController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PersonalPageController extends AbstractController implements Initializable {

    @FXML
    private Pane myPane;

    @FXML
    private JFXButton mygoals;

    @FXML
    private JFXButton myprogress;

    @FXML
    private JFXButton connect;

    @FXML
    private JFXButton returnMain;

    @FXML
    private JFXHamburger cheeseburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private void sideBar() throws IOException {
        myPane = FXMLLoader.load(getClass().getResource( "/toolbar.fxml" ) );
        drawer.setSidePane(myPane);


    }

    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void back() throws IOException {
        Stage newstage = (Stage) myPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/menu.fxml"));

        newstage.setScene(new Scene(root, 900, 600));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        newstage.setX((screenSize.getWidth() - newstage.getWidth()) / 2);
        newstage.setY((screenSize.getHeight() - newstage.getHeight()) / 2);

    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(cheeseburger);
        task.setRate( -1 );

        cheeseburger.addEventHandler( MouseEvent.MOUSE_CLICKED,e -> {
            task.setRate(task.getRate() * -1 );
            task.play();

            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });
        drawer.setOnDrawerOpened(event -> {
            AnchorPane.setRightAnchor(drawer, 0.0);
            AnchorPane.setLeftAnchor(drawer, 0.0);
            AnchorPane.setTopAnchor(drawer, 0.0);
            AnchorPane.setBottomAnchor(drawer, 0.0);
        });

        drawer.setOnDrawerClosed(event -> {
            AnchorPane.clearConstraints(drawer);
            AnchorPane.setLeftAnchor(drawer, -1000.0);
            AnchorPane.setTopAnchor(drawer, 0.0);
            AnchorPane.setBottomAnchor(drawer, 0.0);
        });
    }
}


