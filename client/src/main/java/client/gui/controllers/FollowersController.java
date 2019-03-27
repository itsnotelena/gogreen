package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


@Component
public class FollowersController implements Initializable {
    @FXML
    private Pane myPane;

    @FXML
    private JFXHamburger cheeseburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton leaderbtn;

    @FXML
    private JFXButton searchbtn;

    @FXML
    private Label infolabel;

    @FXML
    private Label errorlabel;

    @FXML
    private Label noUserLabel;

    @FXML
    private JFXTextField searchfield;

    @FXML
    private ListView leaderboard;

    private UserService service;

    private List<User> leaderlist;

    private Set<User> followlist;

    private List<User> searchlist;

    private User result;

    @Autowired
    public FollowersController(UserService service) {
        this.service = service;
    }

    @FXML
    private void sideBar() throws IOException {
        myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
        drawer.setSidePane(myPane);

    }


    @Override
    public void initialize(URL url, ResourceBundle rs) {
        errorlabel.setVisible(false);
        noUserLabel.setVisible(false);

        drawer.setDefaultDrawerSize(DRAWER_SIZE);
        //drawer.setOverLayVisible(true);

        drawer.setResizableOnDrag(true);
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(cheeseburger);
        task.setRate(task.getRate() * -1);


        // TODO: Extract duplicate code
        cheeseburger.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            task.setRate(task.getRate() * -1);
            task.play();

            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.open();
            }

        });
        infolabel.setText("Global Leaderboard");
        System.out.println("global leaderboard message printed");
        this.leaderlist = this.service.getLeaderBoard();
        this.leaderlist.forEach(e ->
                this.leaderboard.getItems().add(
                new Label("Username: " + e.getUsername()
                        + " Email: " + e.getEmail() + " Points: " + service.getFollowingPoints(e.getUsername())
                        + "       (press here to start following)")));
        this.leaderboard.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> this.service.addFollow(this.leaderlist.get(
                        this.leaderboard.getSelectionModel().getSelectedIndex())));

    }

    @FXML
    private void getLeaderBoard() {
        this.leaderboard.getItems().clear();
        this.leaderlist = this.service.getLeaderBoard();
        infolabel.setText("Global Leaderboard");
        this.leaderlist.forEach(e -> this.leaderboard.getItems().add(new Label("Username: "
                + e.getUsername() + " Email: " + e.getEmail() + " Points: " + service.getFollowingPoints(e.getUsername())
        )));
    }

    @FXML
    private void search() {
        if (!this.searchfield.getText().isBlank()) {
            errorlabel.setVisible(false);
            this.searchlist = this.service.search(this.searchfield.getText());
            this.leaderboard.getItems().clear();
            infolabel.setText("Search Results");
            if (!searchlist.isEmpty()) {
                noUserLabel.setVisible(false);
                this.searchlist.forEach(e ->
                        this.leaderboard.getItems().add(new Label("Username: " + e.getUsername()
                                + " Email: " + e.getEmail() + " Points: " + service.getFollowingPoints(e.getUsername()))));
            } else {
                noUserLabel.setVisible(true);
            }
        } else {
            noUserLabel.setVisible(false);
            errorlabel.setVisible(true);
        }
    }

    @FXML
    private void getFollowList() {
        this.followlist = this.service.viewFollowList();
        this.infolabel.setText("Following");
        this.leaderboard.getItems().clear();
        this.followlist.forEach(e ->
                this.leaderboard.getItems().add(new Label("Username: " + e.getUsername()
                        + " Email: " + e.getEmail() + " Points: " + service.getFollowingPoints(e.getUsername())
                        + "       (press here to unfollow)")));
        this.leaderboard.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> this.service.removeFollow(this.leaderlist.get(
                        this.leaderboard.getSelectionModel().getSelectedIndex())));
    }


}
