package client.gui.controllers;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

@Component
public class DiscoverPeopleController extends AbstractController implements Initializable {

    @FXML
    Pane myPane;

    @FXML
    JFXHamburger hamburger;

    @FXML
    JFXDrawer drawer;


    @FXML
    private JFXNodesList friendsList;

    @FXML
    private JFXNodesList rankingList;

    @FXML
    private JFXButton friendsbtn;

    @FXML
    private JFXButton addbtn;

    @FXML
    private JFXButton deletebtn;

    @FXML
    private JFXButton leaderbtn;

    @FXML
    private JFXButton searchbtn;

    @FXML
    private JFXButton globalLeadBtn;

    @FXML
    private JFXButton friendLeadBtn;

    @FXML
    private Label infolabel;

    @FXML
    private Label errorlabel;

    @FXML
    private Label noUserLabel;

    @FXML
    private Label selectULabel;

    @FXML
    private TextField searchfield;

    @FXML
    private ListView leaderboard;

    private UserService service;

    private List<User> leaderlist;

    private Set<User> followlist;

    private List<User> searchlist;

    private User result;

    @Autowired
    public DiscoverPeopleController(UserService service) {
        this.service = service;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorlabel.setVisible(false);
        noUserLabel.setVisible(false);
        selectULabel.setVisible(false);
        addbtn.setVisible(false);
        deletebtn.setVisible(false);
        friendsbtn.setVisible(false);
        leaderbtn.setVisible(false);
        globalLeadBtn.setVisible(false);
        friendLeadBtn.setVisible(false);

        friendsList.addAnimatedNode(friendsbtn);
        friendsList.addAnimatedNode(addbtn);
        friendsList.addAnimatedNode(deletebtn);
        friendsList.setSpacing(2);

        rankingList.addAnimatedNode(leaderbtn);
        rankingList.addAnimatedNode(globalLeadBtn);
        rankingList.addAnimatedNode(friendLeadBtn);
        rankingList.setSpacing(2);

        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
            drawer.setSidePane(myPane);
            drawer.setDefaultDrawerSize(DRAWER_SIZE);
            //drawer.setOverLayVisible(true);

            drawer.setResizableOnDrag(true);
            HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
            task.setRate(task.getRate() * -1);

            this.initializeHamburger(task, hamburger, drawer);

            infolabel.setText("Global Leaderboard");
            System.out.println("global leaderboard message printed");
            this.leaderlist = this.service.getLeaderBoard();
            this.leaderlist.forEach(e ->
                    this.leaderboard.getItems().add(
                            new Label("Username: " + e.getUsername()
                                    + " Email: " + e.getEmail() + " Points: "
                                    + service.getFollowingPoints(e.getUsername()))));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void getLeaderBoard() {
        selectULabel.setVisible(false);
        this.leaderboard.getItems().clear();
        this.leaderlist = this.service.getLeaderBoard();
        infolabel.setText("Global Leaderboard");
        this.leaderlist.forEach(e -> this.leaderboard.getItems().add(new Label(
                "Username: " + e.getUsername() + " Email: " + e.getEmail()
                        + " Points: " + service.getFollowingPoints(e.getUsername())
        )));
    }

    @FXML
    private void getFollowList() {
        selectULabel.setVisible(false);
        this.followlist = this.service.viewFollowList();
        this.infolabel.setText("Following");
        this.leaderboard.getItems().clear();
        this.followlist.forEach(e ->
                this.leaderboard.getItems().add(new Label("Username: " + e.getUsername()
                        + " Email: " + e.getEmail() + " Points: "
                        + service.getFollowingPoints(e.getUsername()))));
    }

    @FXML
    private void search() {
        selectULabel.setVisible(false);
        if (!this.searchfield.getText().isBlank()) {
            errorlabel.setVisible(false);
            this.searchlist = this.service.search(this.searchfield.getText());
            this.leaderboard.getItems().clear();
            infolabel.setText("Search Results");
            if (!searchlist.isEmpty()) {
                noUserLabel.setVisible(false);
                this.searchlist.forEach(e ->
                        this.leaderboard.getItems().add(new Label("Username: " + e.getUsername()
                                + " Email: " + e.getEmail() + " Points: "
                                + service.getFollowingPoints(e.getUsername()))));
            } else {
                noUserLabel.setVisible(true);
            }
        } else {
            noUserLabel.setVisible(false);
            errorlabel.setVisible(true);
        }
    }

    @FXML
    private void addFollow() {
        if (this.leaderboard.getSelectionModel().getSelectedIndex() != -1) {
            this.service.addFollow(this.leaderlist.get(
                    this.leaderboard.getSelectionModel().getSelectedIndex()));
        } else {
            selectULabel.setVisible(true);
        }
    }

    @FXML
    private void removeFollow() {
        if (this.leaderboard.getSelectionModel().getSelectedIndex() != -1) {
            this.service.removeFollow(this.leaderlist.get(
                    this.leaderboard.getSelectionModel().getSelectedIndex()));
        } else {
            selectULabel.setVisible(true);
        }
    }


}

