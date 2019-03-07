package client.gui.controllers;

import static client.gui.tools.SceneNames.FORGOT;
import static client.gui.tools.SceneNames.MAIN;
import static client.gui.tools.SceneNames.SIGNUP;

import client.gui.tools.AbstractController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.text.html.ImageView;


@Component
@ComponentScan({"client"})
@NoArgsConstructor
public class LoginController extends AbstractController implements Initializable {

    @FXML
    private Button signup;

    @FXML
    private Label validpass;

    @FXML
    private Button login;

    @FXML
    private TextField textfield;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Checkbox remenberMe;

    @FXML
    private Hyperlink forgotPassword;

    @FXML
    private ImageView logo;

    @FXML
    private Button helloWorld;

    /**
     * Goes to the create account.
     * @throws IOException Throws exception when create account window cannot be found
     */
    @FXML
    public void createAccount() throws IOException {

        goToSmall(textfield, SIGNUP);

    }

    /**
     * Goes to reset password screen.
     *
     * @throws IOException throws exception when reset pass window is not found
     */
    @FXML
    public void resetPass() throws IOException {
        goToSmall(textfield, FORGOT);
    }

    /**
     * Logs the user in and goes to the main screen.
     *
     * @throws IOException throws exception when menu is not found
     */
    public void doLogin() throws IOException {
        if (textfield.getText().equals("user") && passwordField.getText().equals("pass")) {
            goToLarge(textfield, MAIN);
        } else {
            validpass.setText("Invalid Credentials");
        }
    }

    /**
     * Gets the data from the hello endpoint and sets the text of the goBack to the response.
     */
    public void hello() {
        this.helloWorld.setText("hello");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}




