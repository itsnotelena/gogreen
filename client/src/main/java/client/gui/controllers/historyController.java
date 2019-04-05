package client.gui.controllers;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static client.gui.tools.SceneNames.*;

@Component
public class historyController extends AbstractController implements Initializable {

    UserService service;

    @FXML
    private Pane myPane;

    @FXML
    private Pane pane1;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawersStack drawer;

    @FXML
    private Text usernameField;

    @Autowired
    public historyController(UserService service){ this.service = service; }

    @FXML
    public void logOut() throws IOException {
        goToSmall(myPane, LOGIN);
    }

    @FXML
    public void show() throws IOException {
        if (pane1.isVisible()) {
            pane1.setVisible( false );
        } else {
            pane1.setVisible( true );
        }
    }

    @FXML
    public void goToSettings() throws IOException{
        goToLarge( myPane, SETTINGS );
    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {

        this.usernameField.setText( service.getUsername() );

        try{

            myPane = FXMLLoader.load( getClass().getResource( TOOLBAR ) );

        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeHamburger( myPane, hamburger, drawer );
        drawer.setVisible( false );
    }
}
