package controllers;

import client.HelloWorldService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import static tools.SceneNames.FORGOT;
import static tools.SceneNames.MAIN;
import static tools.SceneNames.SIGNUP;

import tools.AbstractController;

import java.awt.Checkbox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.text.html.ImageView;


@Component
@ComponentScan({"client"})
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

    private HelloWorldService helloWorldService;


    public LoginController() {

    }

    @Autowired
    public LoginController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    /**
     * Goes to the create account.
     *
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
        this.helloWorld.setText(helloWorldService.getHello());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}




