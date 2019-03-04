package controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

@Component
public class MenuController implements Initializable {
    @FXML
    private TabPane menupane;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Pane myPane;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    /**
     * Logs the user out and shows them the login screen.
     * @throws IOException Throws an exception when the main window cannot be found.
     */
    public void logOut() throws IOException {
        System.out.println("Logout called");
        Stage stage = (Stage) menupane.getScene().getWindow();
        Parent root = FXMLLoader.load(getResource("/window1.fxml"));
        stage.setTitle("Go Green");
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(root, 600, 500));
        /* stage.show(); */
    }

    @FXML
    private void sideBar() throws IOException {
        myPane = FXMLLoader.load(getClass().getResource( "/toolbar.fxml" ) );

        drawer.setSidePane(myPane);

        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate( -1 );

        hamburger.addEventHandler( MouseEvent.MOUSE_CLICKED, (event) -> {
                task.setRate(task.getRate() * -1 );
                task.play();
                drawer.close();
        });



    }

    @Override
    public void initialize(URL url, ResourceBundle rs){
        drawer.close();
    }

    }




