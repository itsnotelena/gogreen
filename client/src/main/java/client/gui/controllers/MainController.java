package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.DoughnutChart;
import client.services.UserService;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
public class MainController implements Initializable {

    @FXML
    private AnchorPane menupane;

    @FXML
    private Pane myPane;

    @FXML
    private HBox profileInfo;

    @FXML
    private Text pointsContainer;

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

    @FXML
    private ListView loglist;

    private UserService service;

    private List<Log> logs;


    int points = 0;


    //TODO: find why the service is different after client restarts app.
    @Autowired
    public MainController(UserService service) {
        this.service = service;
    }

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
            this.logs = this.service.getLog();
            this.logs.forEach(e -> this.loglist.getItems().add(new Label(e.getAction()+" "+e.getDate())));
        } catch (IOException e) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObservableList<PieChart.Data> pieChartData = createData();
        DoughnutChart chart = new DoughnutChart(pieChartData);
        chartContainer.getChildren().add(chart);

        int point = service.getPoints();
        pointsContainer.setText("P:" + point);

        JFXNodesList foodList = new JFXNodesList();
        JFXNodesList transportList = new JFXNodesList();
        JFXNodesList energyList = new JFXNodesList();

        nodeListContainer.getChildren().add(foodList);
        nodeListContainer.getChildren().add(energyList);
        nodeListContainer.getChildren().add(transportList);

        foodList.addAnimatedNode(new Region());
        foodList.addAnimatedNode((Region) vegbtn.getParent());
        foodList.addAnimatedNode((Region) localbtn.getParent());
        transportList.addAnimatedNode(new Region());
        transportList.addAnimatedNode((Region) bikebtn.getParent());
        transportList.addAnimatedNode((Region) publicbtn.getParent());
        energyList.addAnimatedNode(new Region());
        energyList.addAnimatedNode((Region) tempbtn.getParent());
        energyList.addAnimatedNode((Region) solarbtn.getParent());

        for (PieChart.Data chartData : chart.getData()) {
            Node chartSlice = chartData.getNode();
            chartSlice.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                switch (chartData.getName()) {
                    case "Food":
                        foodList.animateList();
                        if (energyList.isExpanded()) {
                            energyList.animateList();
                        }
                        if (transportList.isExpanded()) {
                            transportList.animateList();
                        }
                        break;
                    case "Transport":
                        transportList.animateList();
                        if (foodList.isExpanded()) {
                            foodList.animateList();
                        }
                        if (energyList.isExpanded()) {
                            energyList.animateList();
                        }
                        break;
                    case "Energy":
                        energyList.animateList();
                        if (foodList.isExpanded()) {
                            foodList.animateList();
                        }
                        if (transportList.isExpanded()) {
                            transportList.animateList();
                        }

                        break;
                }
            });
        }

        //TODO: Decide if labels are needed
        vegLabel.setVisible(false);
        localLabel.setVisible(false);
        bikeLabel.setVisible(false);
        publicLabel.setVisible(false);
        tempLabel.setVisible(false);
        solarLabel.setVisible(false);


        addEventHandlers(vegbtn, vegLabel, localbtn, localLabel, bikebtn, bikeLabel);
        addEventHandlers(publicbtn, publicLabel, tempbtn, tempLabel, solarbtn, solarLabel);


        solarbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.SOLAR);
        });
        vegbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.VEGETARIAN);
        });
        bikebtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.BIKE);
        });
        tempbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.TEMP);
        });
        publicbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.PUBLIC);
        });
        localbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.LOCAL);
        });
    }


    private void buttonPressed(Action action) {
        int b = this.service.madeAction(action);
        System.out.println(b);
        this.pointsContainer.setText("P:" + b);
        this.logs = this.service.getLog();
        this.loglist.getItems().clear();
        this.logs.forEach(e -> this.loglist.getItems().add(new Label(e.getAction() + " " + e.getDate())));
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