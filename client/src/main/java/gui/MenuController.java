package gui;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MenuController extends AbstractController {

    @FXML
    private TabPane menupane;


    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void goBack() throws IOException {
        String fileName = "/window1.fxml";
        goBack(fileName, menupane);
    }


}
