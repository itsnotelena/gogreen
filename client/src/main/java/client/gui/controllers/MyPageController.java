package client.gui.controllers;

import client.gui.tools.AbstractController;
import client.services.BadgeService;
import client.services.UserService;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.Badge;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

@Component
public class MyPageController extends AbstractController implements Initializable {

    @FXML
    Pane myPane;

    @FXML
    Pane badgePane;

    @FXML
    Pane passPane;

    @FXML
    JFXHamburger hamburger;

    @FXML
    JFXDrawersStack drawer;

    @FXML
    private ImageView veg1;

    @FXML
    private ImageView local1;

    @FXML
    private ImageView bike1;

    @FXML
    private ImageView public1;

    @FXML
    private ImageView solar1;

    @FXML
    private ImageView temp1;

    @FXML
    private ImageView veg2;

    @FXML
    private ImageView local2;

    @FXML
    private ImageView bike2;

    @FXML
    private ImageView public2;

    @FXML
    private ImageView solar2;

    @FXML
    private ImageView temp2;

    @FXML
    private ImageView veg3;

    @FXML
    private ImageView local3;

    @FXML
    private ImageView bike3;

    @FXML
    private ImageView public3;

    @FXML
    private ImageView solar3;

    @FXML
    private ImageView temp3;

    @FXML
    private Label username;

    @FXML
    private Label email;

    @FXML
    private JFXTextField passfield;

    private BadgeService badgeService;

    private UserService userService;

    private Badge[] badgeLevels;


    private ImageView[][] badges;

    @Autowired
    public MyPageController(BadgeService badgeService, UserService userService) {
        this.badgeService = badgeService;
        this.userService = userService;
    }

    private void initBadges() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                badges[i][j].opacityProperty().setValue(0.25);
            }
        }
    }


    private void getBadges() {
        badgeLevels = badgeService.getBadges();
        for (int i = 0; i < badgeLevels.length; i++) {
            for (int j = 0; j < badgeLevels[i].getLevel(); j++) {
                badges[i][j].opacityProperty().setValue(1);
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        badgePane.toBack();
        passPane.setVisible(false);

        try {
            this.badges =  new ImageView[][] {{veg1, veg2, veg3},
                {local1, local2, local3},
                {bike1, bike2, bike3}, {public1, public2, public3},
                {solar1, solar2, solar3}, {temp1, temp2, temp3}};

                myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));

            initBadges();
            getBadges();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeHamburger(myPane, hamburger, drawer);
        drawer.setVisible( false );
        username.setText(userService.getUser().getUsername());
        email.setText(userService.getUser().getEmail());

    }

    @FXML
    public void changePass() {
        passPane.setVisible(true);
    }

    @FXML
    public  void setPass() {
        if (!passfield.getText().isEmpty()) {
            userService.setPassword(passfield.getText());
        }
        passPane.setVisible(false);
    }
}
