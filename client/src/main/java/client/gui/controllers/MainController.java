package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.DoughnutChart;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane menupane;

    @FXML
    private Pane myPane;

//    @FXML
//    private DoughnutChart chart;

    @FXML
    private HBox chartContainer;

    @FXML
    private VBox nodeListContainer;

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
    private JFXButton vegbtn;

    @FXML
    private JFXButton localbtn;

    @FXML
    private JFXButton bikebtn;

    @FXML
    private JFXButton publicbtn;

    @FXML
    private JFXButton tempbtn;

    @FXML
    private JFXButton solarbtn;

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
            task.setRate(task.getRate() * -1);


            // TODO: Extract duplicate code
            hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

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


        ObservableList<PieChart.Data> pieChartData = createData();
        DoughnutChart chart = new DoughnutChart(pieChartData);
        chartContainer.getChildren().add(chart);

//        chart.setData(pieChartData);
        JFXNodesList foodList = new JFXNodesList();
        JFXNodesList transportList = new JFXNodesList();
        JFXNodesList energyList = new JFXNodesList();

        nodeListContainer.setSpacing(100);
        nodeListContainer.getChildren().add(foodList);
        nodeListContainer.getChildren().add(energyList);
        nodeListContainer.getChildren().add(transportList);

        foodList.addAnimatedNode(new Region());
        foodList.addAnimatedNode((Region) vegbtn.getParent());
        foodList.addAnimatedNode((Region) localbtn.getParent());
        transportList.addAnimatedNode(new Region());
        transportList.addAnimatedNode((Region)bikebtn.getParent());
        transportList.addAnimatedNode((Region)publicbtn.getParent());
        energyList.addAnimatedNode(new Region());
        energyList.addAnimatedNode((Region)tempbtn.getParent());
        energyList.addAnimatedNode((Region)solarbtn.getParent());

        for (PieChart.Data chartData : chart.getData()) {
            Node chartSlice = chartData.getNode();
            chartSlice.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (chartData.getName().equals("Food")) {
                    foodList.animateList();
                } else if (chartData.getName().equals("Transport")) {
                    transportList.animateList();
                } else if (chartData.getName().equals("Energy")) {
                    energyList.animateList();
                }
            });
        }

        vegLabel.setVisible(false);
        localLabel.setVisible(false);
        bikeLabel.setVisible(false);
        publicLabel.setVisible(false);
        tempLabel.setVisible(false);
        solarLabel.setVisible(false);


        addEventHandlers(vegbtn, vegLabel, localbtn, localLabel, bikebtn, bikeLabel);
        addEventHandlers(publicbtn, publicLabel, tempbtn, tempLabel, solarbtn, solarLabel);


    }

    //TODO: Add MOUSE_CLICKED request for buttons that sends a JSON request.
    private void addEventHandlers(JFXButton vegbtn, Label vegLabel,
                                  JFXButton localbtn, Label localLabel,
                                  JFXButton bikebtn, Label bikeLabel) {

        vegbtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> vegLabel.setVisible(true));
        vegbtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> vegLabel.setVisible(false));
        localbtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> localLabel.setVisible(true));
        localbtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> localLabel.setVisible(false));

        bikebtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> bikeLabel.setVisible(true));
        bikebtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> bikeLabel.setVisible(false));
    }


    //TODO: Get PieChart.Data from user's history
    private ObservableList<PieChart.Data> createData() {
        return FXCollections.observableArrayList(
                new PieChart.Data("Food", 33),
                new PieChart.Data("Energy", 33),
                new PieChart.Data("Transport", 33)
        );
    }
}