package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.HISTORY;
import static client.gui.tools.SceneNames.LOGIN;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

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
    private JFXDrawer drawer;

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


        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
            drawer.setSidePane(myPane);
            drawer.setDefaultDrawerSize(DRAWER_SIZE);

            drawer.setResizableOnDrag(true);
            HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
            task.setRate(task.getRate() * -1);

            this.initializeHamburger(task, hamburger, drawer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}