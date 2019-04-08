package client.gui.controllers;

import static client.gui.tools.SceneNames.HISTORY;
import static client.gui.tools.SceneNames.LOGIN;
import static client.gui.tools.SceneNames.SETTINGS;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.services.UserService;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXNodesList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.User;

import java.awt.event.MouseEvent;
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
    private JFXDrawersStack drawer;

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

    private Map<Label, Integer> map;

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
            pane1.setVisible( false );
        } else {
            pane1.setVisible( true );
        }
    }

    @FXML
    public void goToSettings() throws IOException {
        goToLarge(myPane, SETTINGS );
    }

    @FXML
    public void goToHistory() throws IOException {
        goToLarge( myPane, HISTORY );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userPane.setVisible(false);
        addListListeners(this.leaderboard);

        if (service.getPoints() >= 5000) {
            BackgroundImage myBI = new BackgroundImage(
                    new Image(
                            "/images/backgroundlevel2.png", 900, 600, false, true ),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT );

            myPane.setBackground( new Background( myBI ) );

        } else if (service.getPoints() >= 10000) {
            BackgroundImage myBI = new BackgroundImage(
                    new Image( "/images/image_background.png", 900, 600, false, true ),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT );

            myPane.setBackground( new Background( myBI ) );
        }

        pane1.setVisible( false );
        this.usernameField.setText(service.getUsername());

        this.map = new HashMap<Label, Integer>();
        this.labellist = new ArrayList<>();

        errorlabel.setVisible(false);
        noUserLabel.setVisible(false);
        selectULabel.setVisible(false);


        try {
            myPane = FXMLLoader.load(getClass().getResource( TOOLBAR ));

            getLeaderBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeHamburger( myPane, hamburger, drawer);
        drawer.setVisible(false);

    }

    @FXML
    private void getLeaderBoard() {
        selectULabel.setVisible(false);
        leaderboard.setVisible(true);
        followView.setVisible(false);
        searchView.setVisible(false);
        this.leaderboard.getItems().clear();
        this.leaderlist = this.service.getLeaderBoard();
        this.leaderlist.forEach(e -> this.leaderboard.getItems().add(getUserLabel(e)));
        infolabel.setText("Global Leaderboard");
        this.leaderlist.forEach(e -> map.put(getUserLabel(e),
                service.getFollowingPoints(e.getUsername())));
        this.map = sortByValue(this.map);
        this.labellist.addAll(map.keySet());
        Collections.reverse(labellist);
        this.leaderboard.getItems().addAll(labellist);

    }

    /**
     * The method sorts a map accendingly.
     * @param map a map to sort.
     * @param <K> a key.
     * @param <V> a value.
     * @return a sorted map.
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
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
                        + " Points: "  + service.getFollowingPoints(user.getUsername()));
        if (user.getUsername().equals(this.service.getUser().getUsername())) {
            result.setTextFill(Color.rgb(18, 214, 8));
        }
        if (this.service.viewFollowList().contains(user)) {
            result.setTextFill(Color.rgb(239, 150, 16));
        }
        return result;
    }

    private void addListListeners(ListView list) {
        list.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() != -1) {
                    if (list.equals(followView)) {
                        selectedUser = followlist.get(t1.intValue());
                    } else if (list.equals(leaderboard)) {
                        selectedUser = leaderlist.get(t1.intValue());
                    } else {
                        selectedUser = searchlist.get(t1.intValue());
                    }
                    System.out.println(t1.intValue());
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
            }
        });
    }


    @FXML
    private void getFollowList() {
        selectULabel.setVisible(false);
        leaderboard.setVisible(false);
        searchView.setVisible(false);
        followView.setVisible(true);
        this.followlist = this.service.viewFollowList();
        this.followView.getItems().clear();
        this.followlist.forEach(e ->
                this.followView.getItems().add(getUserLabel(e)));
        addListListeners(this.followView);
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
                this.searchlist.forEach(e ->
                        this.searchView.getItems().add(getUserLabel(e)));
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