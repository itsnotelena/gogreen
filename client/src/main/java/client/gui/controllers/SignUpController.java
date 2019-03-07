package client.gui.controllers;

import client.gui.tools.AbstractController;
import client.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import shared.models.Gender;
import shared.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static client.gui.tools.SceneNames.LOGIN;


@Component
@Controller
public class SignUpController extends AbstractController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField email;

    @FXML
    private TextField confirmemail;

    @FXML
    private RadioButton man;

    @FXML
    private RadioButton woman;

    @FXML
    private RadioButton other;

    private UserService userService;

    @Autowired
    SignUpController(UserService userService) {
        this.userService = userService;
    }

    public SignUpController() {}

    public void doSignUp() {
        if (!email.getText().equals(confirmemail.getText()) || password.getText().isBlank()
                || email.getText().isBlank() || username.getText().isBlank()) {
            return;
            //show error message
        }

        User user = new User();
        user.setGender(getGender());
        user.setEmail(email.getText());
        user.setUsername(username.getText());
        user.setPassword(password.getText());

        if(this.userService.createAccount(user)) {
            System.out.println("Signed up successfully.");
        } else {
            System.err.println("Signed up is incorrect.");
        }

    }

    private Gender getGender() {
        if (man.isPressed()) {
            return Gender.MAN;
        } else if (woman.isPressed()) {
            return Gender.WOMAN;
        } else {
            return Gender.OTHER;
        }
    }

    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void goToLogin() throws IOException {
        goToSmall(username, LOGIN);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
