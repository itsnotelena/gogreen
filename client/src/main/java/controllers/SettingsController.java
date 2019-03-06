package controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static tools.SceneNames.DRAWER_SIZE;
import static tools.SceneNames.TOOLBAR;

public class SettingsController implements Initializable {

    @FXML
    private Pane myPane;

    @FXML
    private JFXHamburger cheeseburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private void sideBar() throws IOException {
        myPane = FXMLLoader.load(getClass().getResource( TOOLBAR ) );
        drawer.setSidePane(myPane);

    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(cheeseburger);
        task.setRate( -1 );

        cheeseburger.addEventHandler( MouseEvent.MOUSE_CLICKED, e -> {
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
