package client.gui.controllers;

import static client.gui.tools.SceneNames.FORGOT;
import static client.gui.tools.SceneNames.MAIN;
import static client.gui.tools.SceneNames.SIGNUP;

import client.Main;
import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import shared.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


@Component
@ComponentScan({"client"})
@NoArgsConstructor
public class LoginController extends AbstractController implements Initializable {

    @FXML
    private Stage stage;

    @FXML
    private Button signup;

    @FXML
    private AnchorPane pane;

    @FXML
    private Label validpass;

    @FXML
    private JFXButton login;

    @FXML
    private TextField username;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView closeImg;

    @FXML
    private CheckBox remenberMe;

    @FXML
    private Hyperlink forgotPassword;


    private UserService service;

    @Autowired
    public LoginController(UserService service) {
        this.service = service;
    }

    /**
     * Closes the Application
     * @throws IOException
     */
    @FXML
    public void closeApplication() throws IOException{

        Stage stage = (Stage) closeImg.getScene().getWindow();
        stage.close();

    }

    /**
     * Goes to the create account.
     *
     * @throws IOException Throws exception when create account window cannot be found
     */
    @FXML
    public void createAccount() throws IOException {

        goToSmall(username, SIGNUP);

    }


    /**
     * Goes to reset password screen.
     *
     * @throws IOException throws exception when reset pass window is not found
     */
    @FXML
    public void resetPass() throws IOException {
        goToSmall(username, FORGOT);
    }

    /**
     * Logs the user in and goes to the main screen.
     *
     * @throws IOException throws exception when menu is not found
     */
    public void doLogin() throws IOException {

        User user = new User();

        if (username.getText().isEmpty() || passwordField.getText().isEmpty()) {
            validpass.setText("Invalid Credentials");
        } else {

            user.setUsername(username.getText());
            user.setPassword(passwordField.getText());
            if (service.login(user)) {
                goToLarge(username, MAIN);
            } else {
                validpass.setText("Invalid Credentials");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        login.setDefaultButton(true);
    }

}




