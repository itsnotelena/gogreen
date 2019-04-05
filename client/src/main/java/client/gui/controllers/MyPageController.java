package client.gui.controllers;

import static client.gui.tools.SceneNames.HISTORY;
import static client.gui.tools.SceneNames.LOGIN;
import static client.gui.tools.SceneNames.SETTINGS;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.services.BadgeService;
import client.services.UserService;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.models.Badge;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


@Component
public class MyPageController extends AbstractController implements Initializable {


    @FXML
    Pane myPane;

    @FXML
    Pane pane1;

    @FXML
    Pane badgePane;

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
    private Text usernameField;

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

        this.usernameField.setText(userService.getUsername());
        pane1.setVisible( false );

        if (userService.getPoints() >= 0) {
            BackgroundImage myBI = new BackgroundImage( new Image( "/images/backgroundlevel2.png", 900, 600, false, true ),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT );

            myPane.setBackground( new Background( myBI ) );

        } else if (userService.getPoints() >= 5000) {
            BackgroundImage myBI = new BackgroundImage( new Image( "/images/image_background.png", 900, 600, false, true ),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT );

            myPane.setBackground( new Background( myBI ) );
        }

        badgePane.toBack();

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

    }

    @FXML
    public void logOut() throws IOException {
        goToSmall( myPane, LOGIN );
    }

    /**
     * Shows pane1.
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
        goToLarge(myPane, HISTORY );
    }

}

