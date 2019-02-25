package gui;

import client.HelloWorldService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.awt.Checkbox;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.text.html.ImageView;


@Component
@ComponentScan({"client"})
public class LoginController implements Initializable {

    @FXML
    private Button signup;

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
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/window2.fxml")));

        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);

    }

    /**
     * Goes to reset password screen.
     *
     * @throws IOException throws exception when reset pass window is not found
     */
    @FXML
    public void resetPass() throws IOException {
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/window3.fxml")));

        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }

    /**
     * Logs the user in and goes to the main screen.
     *
     * @throws IOException throws exception when menu is not found
     */
    public void doLogin() throws IOException {
        Stage stage = (Stage) textfield.getScene().getWindow();
        if (textfield.getText().equals("user") && passwordField.getText().equals("pass")) {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/menu.fxml")));

            stage.setTitle("Go Green");
            Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
            stage.setScene(new Scene(root, screenSize.getWidth(), screenSize.getHeight() - 20));
            stage.setMaximized(true);

        } else {
            System.out.println("Login Failed");
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




