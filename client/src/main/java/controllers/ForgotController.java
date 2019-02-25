package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

@Component
@ComponentScan({"client"})
public class ForgotController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private TextField textField;

    @FXML
    private Button send;

    @FXML
    private Button back;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Hyperlink hyperlink;

    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    @FXML
    public void goBack() throws IOException {
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getResource("/window1.fxml"));
        Scene scene = new Scene(root, 600, 500);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
