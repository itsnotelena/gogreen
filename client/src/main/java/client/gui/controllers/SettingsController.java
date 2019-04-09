package client.gui.controllers;

import static client.gui.tools.SceneNames.HISTORY;
import static client.gui.tools.SceneNames.LOGIN;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;

import client.services.UserService;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class SettingsController extends AbstractController implements Initializable {

    private UserService service;

    @FXML
    private Pane myPane;

    @FXML
    private Pane pane1;

    @FXML
    private Pane passPane;

    @FXML
    private JFXTextField passfield;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawersStack drawer;

    @FXML
    private Text usernameField;

    @FXML
    private Text username;

    @FXML
    private Text firstname;

    @FXML
    private Text emailField;

    @Autowired
    public SettingsController(UserService service) {
        this.service = service;
    }

    /**
     * Shows or closes the change password pane.
     */
    @FXML
    public void passwordPane() {
        if (passPane.isVisible()) {
            passPane.setVisible( false );
        } else {
            passPane.setVisible( true );
        }
    }

    /**
     * Changes the password based on user input.
     */
    @FXML
    public  void setPass() {
        if (!passfield.getText().isEmpty()) {
            service.setPassword(passfield.getText());
        }
        passPane.setVisible(false);
    }

    @FXML
    public void logOut() throws IOException {
        goToSmall(myPane, LOGIN);
    }

    /**
     * Shows the pane with user options.
     */
    @FXML
    public void show() {
        if (pane1.isVisible()) {
            pane1.setVisible( false );
        } else {
            pane1.setVisible( true );
        }
    }

    @FXML
    public void goToHistory() throws IOException {
        goToLarge( myPane, HISTORY );
    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {

        pane1.setVisible( false );
        passPane.setVisible( false );

        this.usernameField.setText(service.getUser().getUsername());

        this.username.setText( service.getUser().getUsername() );
        this.emailField.setText( service.getUser().getEmail() );

        if (service.getPoints() >= 5000) {
            BackgroundImage myBI = new BackgroundImage(
                    new Image( "/images/backgroundlevel2.png", 900, 600, false, true ),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT );

            myPane.setBackground( new Background( myBI ) );

        } else if (service.getPoints() >= 10000) {
            BackgroundImage myBI = new BackgroundImage(
                    new Image( "/images/image_background.png", 900, 600, false, true ),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT );

            myPane.setBackground( new Background( myBI ) );
        }

        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));

        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeHamburger(myPane, hamburger, drawer);
        drawer.setVisible(false);
    }

}