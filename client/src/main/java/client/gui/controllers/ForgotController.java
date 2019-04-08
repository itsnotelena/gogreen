package client.gui.controllers;

import static client.gui.tools.SceneNames.LOGIN;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@ComponentScan({"client"})
public class ForgotController extends AbstractController implements Initializable {

    private UserService service;
    @FXML
    private ImageView imageView;

    @FXML
    private Label message;

    @FXML
    private JFXTextField textField;

    @FXML
    private JFXButton send;

    @FXML
    private Button returnButton;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Hyperlink hyperlink;

    @Autowired
    public ForgotController(UserService service) {
        this.service = service;
    }

    /**
     * Goes back to login screen.
     *
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void goToLogin() throws IOException {
        goToSmall(returnButton, LOGIN);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        send.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String email = textField.getText();
            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    String response = service.sendForgot(email);
                    System.out.println(response);
                    return response;
                }
            };
            task.setOnSucceeded(e -> {
                String result = task.getValue();
                if (result == null) {
                    message.setText("No user with that email exists");
                } else {
                    message.setText("Sent a message to: " + email);
                }
            });
            new Thread(task).start();
        });
    }
}
