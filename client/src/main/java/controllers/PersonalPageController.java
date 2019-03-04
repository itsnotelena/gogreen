package controllers;

import gui.AbstractController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PersonalPageController extends AbstractController implements Initializable {

    @FXML
    private Pane myPane;

    @FXML
    private JFXButton mygoals;

    @FXML
    private JFXButton myprogress;

    @FXML
    private JFXButton connect;

    @FXML
    private JFXButton returnMain;

    @FXML
    private JFXHamburger cheeseburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private void sideBar() throws IOException {
        myPane = FXMLLoader.load(getClass().getResource( "/toolbar.fxml" ) );

        drawer.setSidePane(myPane);

        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(cheeseburger);
        task.setRate( -1 );

        cheeseburger.addEventHandler( MouseEvent.MOUSE_CLICKED, (event) -> {
            task.setRate(task.getRate() * -1 );
            task.play();

            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.open();
            } });
        drawer.setOnDrawerOpened(event -> {
            AnchorPane.setRightAnchor(drawer, 0.0);
            AnchorPane.setLeftAnchor(drawer, 0.0);
            AnchorPane.setTopAnchor(drawer, 0.0);
            AnchorPane.setBottomAnchor(drawer, 0.0);
        });

        drawer.setOnDrawerClosed(event ->
        {
            AnchorPane.clearConstraints(drawer);
            AnchorPane.setLeftAnchor(drawer, -150.0);
            AnchorPane.setTopAnchor(drawer, 0.0);
            AnchorPane.setBottomAnchor(drawer, 0.0);
        });
    }

    /**
     * Goes back to login screen.
     * @throws IOException Throws exception when login window cannot be found.
     */
    public void back() throws IOException{
        Stage newstage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));

        Scene scene = new Scene(root, 700, 900);
        newstage.setScene(scene);
        newstage.show();
        newstage.setResizable(false);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rs){
        drawer.close();
    }
}

