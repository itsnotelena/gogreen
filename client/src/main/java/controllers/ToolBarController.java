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
    public void goToPersonal() throws IOException {
        goTo(myPane, "personalpage.fxml");
    }

    /**
     * Goes back to Main Page.
     * @throws IOException Throws Exception when main page can't be found.
     */
    public void goToMain() throws IOException {
        Stage newstage = (Stage) myPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/menu.fxml"));

        newstage.setScene(new Scene(root, 900, 600));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        newstage.setX((screenSize.getWidth() - newstage.getWidth()) / 2);
        newstage.setY((screenSize.getHeight() - newstage.getHeight()) / 2);
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

