package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.MY_GOALS;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
@Component
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
        myPane = FXMLLoader.load(getClass().getResource( TOOLBAR ) );
        drawer.setSidePane(myPane);


    }

    /**
     * Goes to GOALS Page.
     * @throws IOException Throws Exception when main page can't be found.
     */
    public void goToGoals() throws IOException {
        goToLarge(myPane, MY_GOALS);
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

        drawer.setDefaultDrawerSize(DRAWER_SIZE);
    }
}


