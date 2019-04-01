package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MyGoalsController extends AbstractController implements Initializable {

    @FXML
    private Pane myPane;

    @FXML
    private JFXHamburger cheeseburger;

    @FXML
    private JFXDrawersStack drawer;

    @Override
    public void initialize(URL url, ResourceBundle rs) {
        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));


        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeHamburger(myPane, cheeseburger, drawer);
        drawer.setVisible(false);
    }

}

