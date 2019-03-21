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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class MainController extends AbstractController implements Initializable {

    public static final int minChartSlice = 5;

    private UserService service;

    private List<Log> logs;

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

    private JFXNodesList foodList;

    private JFXNodesList transportList;

    private JFXNodesList energyList;

    private DoughnutChart chart;

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
        this.chart = new DoughnutChart(pieChartData);
        this.chartContainer.getChildren().add(chart);

        int point = service.getPoints();
        pointsContainer.setText("P:" + point);

        this.foodList = createFoodList();
        this.transportList = createTransportList();
        this.energyList = createEnergyList();

        addListenerChart();

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
        int points = this.service.madeAction(action);
        System.out.println(points);
        this.pointsContainer.setText("P:" + points);
        this.logs = this.service.getLog();
        this.loglist.getItems().clear();
        this.logs.forEach(e -> this.loglist.getItems().add(0, new
                Label(e.getAction() + " " + e.getDate())));
        this.updateChart();
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

    //TODO: Move this to another class
    private ObservableList<PieChart.Data> createData() {

        Map<String, Integer> values = new HashMap<>();
        values.putIfAbsent("Food", 1);
        values.putIfAbsent("Energy", 1);
        values.putIfAbsent("Transport", 1);
        List<Log> logs = service.getLog();
        for (Log log : logs) {
            switch (log.getAction()) {
                case LOCAL:
                case VEGETARIAN:
                    values.put("Food", values.get("Food") + log.getAction().getPoints());
                    break;
                case BIKE:
                case PUBLIC:
                    values.put("Transport", values.get("Transport") + log.getAction().getPoints());
                    break;
                case TEMP:
                case SOLAR:
                    values.put("Energy", values.get("Energy") + log.getAction().getPoints());
                    break;
                default:
            }
        }

        int totalPoints = 3;
        for (String key : values.keySet()) {
            totalPoints += values.get(key);
        }

        double scale = 100.0 - 3 * minChartSlice;

        double foodPercentage = (double) values.get("Food") / totalPoints * scale;
        double transportPercentage = (double) values.get("Transport") / totalPoints * scale;
        double energyPercentage = (double) values.get("Energy") / totalPoints * scale;

        return FXCollections.observableArrayList(
                new PieChart.Data("Food", minChartSlice + foodPercentage),
                new PieChart.Data("Energy", minChartSlice + energyPercentage),
                new PieChart.Data("Transport", minChartSlice + transportPercentage)
        );
    }

    private void addListenerChart() {
        for (PieChart.Data chartData : this.chart.getData()) {
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
    }

    private void updateChart() {
        if (this.chartContainer.getChildren().remove(this.chart)) {
            this.chart = new DoughnutChart(createData());
            this.chartContainer.getChildren().add(chart);
        }
        addListenerChart();
    }

}