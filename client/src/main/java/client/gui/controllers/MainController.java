package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.gui.tools.DoughnutChart;
import client.services.UserService;
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
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainController extends AbstractController implements Initializable {

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

    private UserService service;


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

            this.initializeHamburger(task, hamburger, drawer);

        } catch (IOException e) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObservableList<PieChart.Data> pieChartData = createData();
        DoughnutChart chart = new DoughnutChart(pieChartData);
        chartContainer.getChildren().add(chart);

        pointsContainer.setText(service.getPoints() + "");

        JFXNodesList foodList = createFoodList();
        JFXNodesList transportList = createTransportList();
        JFXNodesList energyList = createEnergyList();

        for (PieChart.Data chartData : chart.getData()) {
            Node chartSlice = chartData.getNode();
            chartSlice.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                switch (chartData.getName()) {
                    case "Food":
                        clickOnSlice(foodList, energyList, transportList);
                        break;
                    case "Transport":
                        clickOnSlice(transportList, energyList, foodList);
                        break;
                    case "Energy":
                        clickOnSlice(energyList, transportList, foodList);
                        break;
                    default:
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

        vegbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.pointsContainer.setText(this.service.ateVegMeal() + "");
        });
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

    private JFXNodesList createFoodList() {
        return createList(vegbtn, localbtn);
    }

    private JFXNodesList createTransportList() {
        return createList(bikebtn, publicbtn);
    }

    private JFXNodesList createEnergyList() {
        return createList(tempbtn, solarbtn);
    }

    private JFXNodesList createList(JFXButton button1, JFXButton button2) {
        JFXNodesList list = new JFXNodesList();
        nodeListContainer.getChildren().add(list);
        list.addAnimatedNode(new Region());
        list.addAnimatedNode((Region) button1.getParent());
        list.addAnimatedNode((Region) button2.getParent());
        return list;
    }

    private void clickOnSlice(JFXNodesList clickedOn, JFXNodesList other1, JFXNodesList other2) {
        clickedOn.animateList();
        if (other1.isExpanded()) {
            other1.animateList();
        }
        if (other2.isExpanded()) {
            other2.animateList();
        }
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