package client.gui.controllers;

import static client.gui.tools.SceneNames.CONDITIONS;
import static client.gui.tools.SceneNames.LOGIN;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import shared.models.Gender;
import shared.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Controller
@NoArgsConstructor
public class SignUpController extends AbstractController implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField repeatPassword;

    @FXML
    private TextField email;

    @FXML
    private TextField confirmemail;

    @FXML
    private RadioButton man;

    @FXML
    private RadioButton woman;

    @FXML
    private Label validpass;

    @FXML
    private RadioButton other;

    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXCheckBox agree;

    private UserService userService;


    @Autowired
    SignUpController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method which takes the entered details and creates a user.
     */
    public void doSignUp() throws IOException {
        if (!email.getText().equals(confirmemail.getText()) || password.getText().isBlank()
                || email.getText().isBlank() || username.getText().isBlank()) {
            validpass.setText("Please input correct values.");
            return;
            //show error message in a modal
        }
        if (!agree.isSelected()) {
            validpass.setText("Please read and agree with TCs.");
            return;
        }

        User user = new User();
        user.setEmail(email.getText());

        if (!user.validateEmail()) {
            validpass.setText("Please input correct email address.");
            return;
        }
        if (repeatPassword.getText().isEmpty()
                || !repeatPassword.getText().equals(password.getText())) {
            validpass.setText("Passwords do not match.");
            return;
        }


        user.setGender(getGender());
        user.setUsername(username.getText());
        user.setPassword(password.getText());


        if (this.userService.createAccount(user)) {
            goToSmall(username, LOGIN);
            System.out.println("Signed up successfully.");
        } else {
            validpass.setText("Username or email already in use");
            System.err.println("Signed up is incorrect.");
        }

    }

    private Gender getGender() {
        return man.isSelected() ? Gender.MAN : (woman.isSelected() ? Gender.WOMAN : Gender.OTHER);
    }

    /**
     * Goes back to login screen.
     *
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void goToLogin() throws IOException {
        goToSmall(username, LOGIN);
    }

    @FXML
    public void goToConditions() throws IOException {
        goToSmall( pane, CONDITIONS );
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup toggleGroup = new ToggleGroup();
        man.setToggleGroup(toggleGroup);
        woman.setToggleGroup(toggleGroup);
        other.setToggleGroup(toggleGroup);
        signUpButton.setDefaultButton(true);

    }


}
