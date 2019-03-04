package controllers;

import abstractcontrollers.AbstractController;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ToolBarController extends AbstractController {

    @FXML
    private Pane myPane;
    @FXML
    private JFXButton settings;

    @FXML
    private JFXButton discover;

    @FXML
    private JFXButton mypage;

    @FXML
    private JFXButton logout;

    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void goTo() throws IOException{
        Stage newstage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("personalpage.fxml"));

        Scene scene = new Scene(root, 700, 900);
        newstage.setScene(scene);
        newstage.show();
        newstage.setResizable(false);
    }

    /**
     * Logs the user out and shows them the login screen.
     * @throws IOException Throws an exception when the main window cannot be found.
     */
    public void logOut() throws IOException {
        System.out.println("Logout called");
        Stage stage = (Stage) myPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/window1.fxml"));
        stage.setTitle("Go Green");
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(root, 600, 500));
        /* stage.show(); */
    }

}
