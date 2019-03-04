package controllers;

import abstractcontrollers.AbstractController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class PersonalPageController extends AbstractController {

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

        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(cheeseburger);
        task.setRate(-1);

        cheeseburger.addEventHandler( EventType.ROOT, event -> {
            task.setRate(task.getRate() * -1);
            task.play();

            if (!drawer.isOpened()) {
                drawer.open();
            } else {
                drawer.open();
            }
        } );
    }

    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void back() throws IOException{
        Stage newstage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("menu.fxml"));

        Scene scene = new Scene(root, 700, 900);
        newstage.setScene(scene);
        newstage.show();
        newstage.setResizable(false);
    }
}
