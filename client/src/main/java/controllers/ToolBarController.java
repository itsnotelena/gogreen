package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import static tools.SceneNames.LOGIN;
import static tools.SceneNames.PERSONAL;
import static tools.SceneNames.MAIN;

import tools.AbstractController;

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
        goToLarge(myPane, PERSONAL);
    }

    /**
     * Goes back to Main Page.
     * @throws IOException Throws Exception when main page can't be found.
     */
    public void goToMain() throws IOException {
        goToLarge(myPane, MAIN);
    }

    /**
     * Logs the user out and shows them the login screen.
     * @throws IOException Throws an exception when the main window cannot be found.
     */
    public void logOut() throws IOException {
        goToSmall(myPane, LOGIN);
    }

}

