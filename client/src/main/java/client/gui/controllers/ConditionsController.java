package client.gui.controllers;

import client.gui.tools.AbstractController;
import client.gui.tools.SceneNames;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ConditionsController extends AbstractController implements Initializable {

    @FXML
    private Pane myPane;

    @FXML
    private ImageView img;

    @FXML
    private TextArea textArea;

    public void goToSignup() throws IOException {
        goToSmall(myPane, SceneNames.SIGNUP);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textArea.editableProperty().setValue(false);
    }
}
