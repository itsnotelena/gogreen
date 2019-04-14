package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.HISTORY;
import static client.gui.tools.SceneNames.LOGIN;
import static client.gui.tools.SceneNames.SETTINGS;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;



@Component
public class DiscoverPeopleController extends AbstractController implements Initializable {

    @FXML
    private Pane myPane;

    @FXML
    private Pane pane1;

    @FXML
    private Pane userPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton followbtn;

    @FXML
    private JFXButton unfollowbtn;

    @FXML
    private Label errorlabel;

    @FXML
    private Label noUserLabel;

    @FXML
    private Label userLabel;


    @FXML
    private Label selectULabel;

    @FXML
    private TextField searchfield;

    @FXML
    private Text usernameField;

    @FXML
    private ListView leaderboard;

    @FXML
    private ListView followView;

    @FXML
    private ListView searchView;

    private UserService service;

    private List<User> leaderlist;

    private List<User> followlist;

    private List<User> searchlist;

    private Map<User, Integer> map;

    private User result;

    private List<Label> labellist;

    private User selectedUser;

    @Autowired
    public DiscoverPeopleController(UserService service) {
        this.service = service;
    }

    @FXML
    public void logOut() throws IOException {
        goToSmall(myPane, LOGIN);
    }

    /**
     * Shows user settings /history pane.
     */
    @FXML
    public void show() {
        if (pane1.isVisible()) {
            pane1.setVisible(false);
        } else {
            pane1.setVisible(true);
        }
    }

    @FXML
    public void goToSettings() throws IOException {
        goToLarge(myPane, SETTINGS);
    }

    @FXML
    public void goToHistory() throws IOException {
        goToLarge(myPane, HISTORY);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userPane.setVisible(false);

        pane1.setVisible(false);
        this.usernameField.setText(service.getUser().getUsername());

        this.map = new HashMap<User, Integer>();
        this.labellist = new ArrayList<>();

        errorlabel.setVisible(false);
        noUserLabel.setVisible(false);
        selectULabel.setVisible(false);


        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
            drawer.setSidePane(myPane);
            drawer.setDefaultDrawerSize(DRAWER_SIZE);

            drawer.setResizableOnDrag(true);
            HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
            task.setRate(task.getRate() * -1);

            this.initializeHamburger(task, hamburger, drawer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        getLeaderBoard();

    }


    @FXML
    private void getFollowList() {
        selectULabel.setVisible(false);
        leaderboard.setVisible(false);
        searchView.setVisible(false);
        followView.setVisible(true);
        this.followlist = this.service.viewFollowList();
        this.followView.getItems().clear();
        this.map.clear();
        this.followlist.forEach(e -> map.put(e,
                service.getFollowingPoints(e.getUsername())));
        this.map = sortByValue(this.map);
        this.followlist.clear();
        this.followlist.addAll(map.keySet());
        Collections.reverse(followlist);
        this.followlist.forEach(e -> followView.getItems().add(getUserLabel(e)));
        addListListeners(this.followView);
    }

    @FXML
    private void getLeaderBoard() {
        selectULabel.setVisible(false);
        leaderboard.setVisible(true);
        followView.setVisible(false);
        searchView.setVisible(false);
        this.leaderboard.getItems().clear();
        this.leaderlist = this.service.getLeaderBoard();
        this.map.clear();
        this.leaderlist.forEach(e -> map.put(e,
                service.getFollowingPoints(e.getUsername())));
        this.map = sortByValue(this.map);
        this.labellist.clear();
        this.leaderlist.clear();
        this.leaderlist.addAll(map.keySet());
        Collections.reverse(leaderlist);
        this.leaderlist.forEach(e -> leaderboard.getItems().add(getUserLabel(e)));
        addListListeners(this.leaderboard);
    }

    /**
     * The method sorts a map in an ascending order.
     *
     * @param map a map to sort.
     * @param <K> a key.
     * @param <V> a value.
     * @return a sorted map.
     */
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


    private Label getUserLabel(User user) {
        Label result = new Label(
                "Username: " + user.getUsername() + " Email: " + user.getEmail()
                        + " Points: " + service.getFollowingPoints(user.getUsername()));
        if (user.getUsername().equals(this.service.getUser().getUsername())) {
            result.setTextFill(Color.rgb(18, 214, 8));
        }
        if (this.service.viewFollowList().contains(user)) {
            result.setTextFill(Color.rgb(239, 150, 16));
        }
        return result;
    }

    private void addListListeners(ListView list) {

        list.getSelectionModel().selectedIndexProperty().addListener(
            (observableValue, number, t1) -> {
                if (t1.intValue() != -1) {
                    if (list.equals(followView)) {
                        selectedUser = followlist.get(t1.intValue());
                    } else if (list.equals(leaderboard)) {
                        selectedUser = leaderlist.get(t1.intValue());
                    } else {
                        selectedUser = searchlist.get(t1.intValue());
                    }
                    if (service.viewFollowList().contains(selectedUser)) {
                        unfollowbtn.setVisible(true);
                        followbtn.setVisible(false);
                    } else {
                        followbtn.setVisible(true);
                        unfollowbtn.setVisible(false);

                    }
                    userPane.setVisible(true);
                    userLabel.setText(selectedUser.getUsername());
                    if (service.getUser().getUsername().equals(selectedUser.getUsername())) {
                        userLabel.setText("That's You!");
                        followbtn.setVisible(false);
                        unfollowbtn.setVisible(false);
                    }
                }
            });
    }


    @FXML
    private void search() {
        selectULabel.setVisible(false);
        searchView.setVisible(true);
        if (!this.searchfield.getText().isBlank()) {
            errorlabel.setVisible(false);
            this.searchlist = this.service.search(this.searchfield.getText());
            this.searchView.getItems().clear();
            if (!searchlist.isEmpty()) {
                noUserLabel.setVisible(false);

                this.map.clear();
                this.searchlist.forEach(e -> map.put(e,
                        service.getFollowingPoints(e.getUsername())));
                this.map = sortByValue(this.map);
                this.searchlist.clear();
                this.searchlist.addAll(map.keySet());
                Collections.reverse(searchlist);
                this.searchlist.forEach(e -> searchView.getItems().add(getUserLabel(e)));
            } else {
                noUserLabel.setVisible(true);
            }
        } else {
            noUserLabel.setVisible(false);
            errorlabel.setVisible(true);
        }
        addListListeners(this.searchView);
    }

    @FXML
    private void addFollow() {
        this.service.addFollow(this.selectedUser);
        this.userPane.setVisible(false);
        if (this.followView.isVisible()) {
            getFollowList();
        } else {
            getLeaderBoard();
        }
    }

    @FXML
    private void removeFollow() {
        this.service.removeFollow(this.selectedUser);
        this.userPane.setVisible(false);
        if (this.followView.isVisible()) {
            getFollowList();
        } else {
            getLeaderBoard();
        }
    }
}