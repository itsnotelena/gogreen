package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.LOGIN;
import static client.gui.tools.SceneNames.SETTINGS;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.Action;
import shared.models.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;



@Component
public class HistoryController extends AbstractController implements Initializable {

    private UserService service;

    @FXML
    private Pane myPane;

    @FXML
    private Pane pane1;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private Text usernameField;

    @FXML
    private JFXListView historyView;

    private List<Log> logs;

    @Autowired
    public HistoryController(UserService service) {
        this.service = service;
    }

    @FXML
    public void logOut() throws IOException {
        goToSmall(myPane, LOGIN);
    }

    /**
     * Shows the pane with user options.
     */
    @FXML
    public void show() {
        if (pane1.isVisible()) {
            pane1.setVisible( false );
        } else {
            pane1.setVisible( true );
        }
    }

    @FXML
    public void goToSettings() throws IOException {
        goToLarge( myPane, SETTINGS );
    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {
        this.usernameField.setText( service.getUser().getUsername() );
        this.pane1.setVisible(false);
        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
            drawer.setSidePane(myPane);
            drawer.setDefaultDrawerSize(DRAWER_SIZE);
            //drawer.setOverLayVisible(true);

            drawer.setResizableOnDrag(true);
            HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
            task.setRate(task.getRate() * -1);

            this.initializeHamburger(task, hamburger, drawer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        createLogList();
    }

    /**
     * Creates the list of actions taken by the user.
     */
    private void createLogList() {
        this.logs = this.service.getLog();
        this.historyView.getItems().clear();

        int parity = 0;
        for (Log log : logs) {
            if (log.getAction().equals(Action.SOLAR)) {
                if (parity % 2 == 1) {
                    this.historyView.getItems().add(0,
                            new javafx.scene.control.Label("You removed your solar panels on "
                                    + log.getDate()));
                }
                parity++;
            }
            this.historyView.getItems().add(0,
                    new Label("You " + log.getAction().historyString() + " on "
                            + log.getDate()));
        }
    }


}