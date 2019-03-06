package controllers;

import static tools.SceneNames.DRAWER_SIZE;
import static tools.SceneNames.TOOLBAR;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane menupane;

    @FXML
    private Pane myPane;


    @FXML
    private Label vegLabel;

    @FXML
    private Label localLabel;

    @FXML
    private Label bikeLabel;

    @FXML
    private Label publicLabel;

    @FXML
    private Label tempLabel;

    @FXML
    private Label solarLabel;

    @FXML
    private JFXButton foodbtn;

    @FXML
    private JFXButton vegbtn;

    @FXML
    private JFXButton localbtn;

    @FXML
    private JFXButton transbtn;

    @FXML
    private JFXButton bikebtn;

    @FXML
    private JFXButton publicbtn;

    @FXML
    private JFXButton energybtn;

    @FXML
    private JFXButton tempbtn;

    @FXML
    private JFXButton solarbtn;

    @FXML
    private JFXNodesList foodList;

    @FXML
    private JFXNodesList energyList;

    @FXML
    private JFXNodesList transList;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;


    @Override
    public void initialize(URL url, ResourceBundle rs) {
        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
            drawer.setSidePane(myPane);
            drawer.setDefaultDrawerSize(DRAWER_SIZE);
            //drawer.setOverLayVisible(true);
            drawer.setResizableOnDrag(true);
            HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
            task.setRate(task.getRate() * -1 );


            hamburger.addEventHandler( MouseEvent.MOUSE_CLICKED, event -> {

                task.setRate(task.getRate() * -1);
                task.play();

                if (drawer.isOpened()) {
                    drawer.close();
                } else {
                    drawer.open();
                }

            });
        } catch (IOException e) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }



        vegLabel.setVisible(false);
        localLabel.setVisible(false);
        bikeLabel.setVisible(false);
        publicLabel.setVisible(false);
        tempLabel.setVisible(false);
        solarLabel.setVisible(false);

        foodList.addAnimatedNode(foodbtn);
        foodList.addAnimatedNode(vegbtn);
        foodList.addAnimatedNode(localbtn);

        vegbtn.addEventHandler(MouseEvent.MOUSE_ENTERED,e -> vegLabel.setVisible(true));
        vegbtn.addEventHandler(MouseEvent.MOUSE_EXITED,e -> vegLabel.setVisible(false));
        localbtn.addEventHandler(MouseEvent.MOUSE_ENTERED,e -> localLabel.setVisible(true));
        localbtn.addEventHandler(MouseEvent.MOUSE_EXITED,e -> localLabel.setVisible(false));

        bikebtn.addEventHandler(MouseEvent.MOUSE_ENTERED,e -> bikeLabel.setVisible(true));
        bikebtn.addEventHandler(MouseEvent.MOUSE_EXITED,e -> bikeLabel.setVisible(false));
        publicbtn.addEventHandler(MouseEvent.MOUSE_ENTERED,e -> publicLabel.setVisible(true));
        publicbtn.addEventHandler(MouseEvent.MOUSE_EXITED,e -> publicLabel.setVisible(false));

        tempbtn.addEventHandler(MouseEvent.MOUSE_ENTERED,e -> tempLabel.setVisible(true));
        tempbtn.addEventHandler(MouseEvent.MOUSE_EXITED,e -> tempLabel.setVisible(false));
        solarbtn.addEventHandler(MouseEvent.MOUSE_ENTERED,e -> solarLabel.setVisible(true));
        solarbtn.addEventHandler(MouseEvent.MOUSE_EXITED,e -> solarLabel.setVisible(false));

        transList.addAnimatedNode(transbtn);
        transList.addAnimatedNode(bikebtn);
        transList.addAnimatedNode(publicbtn);

        energyList.addAnimatedNode(energybtn);
        energyList.addAnimatedNode(tempbtn);
        energyList.addAnimatedNode(solarbtn);

        foodList.setSpacing(5);
        foodList.setRotate(180);

        transList.setSpacing(25);
        transList.setRotate(45);

        energyList.setSpacing(25);
        energyList.setRotate(-45);


    }


}
