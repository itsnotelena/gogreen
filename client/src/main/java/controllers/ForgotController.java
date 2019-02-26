package controllers;

import abstractcontrollers.AbstractController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@ComponentScan({"client"})
public class ForgotController extends AbstractController implements Initializable {

    private String fileName = "/window1.fxml";

    @FXML
    private ImageView imageView;

    @FXML
    private TextField textField;

    @FXML
    private Button send;

    @FXML
    private Button returnButton;

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

    public void goBack() throws IOException {
        goBack(returnButton);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
