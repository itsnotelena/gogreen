package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.gui.tools.DoughnutChart;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXSlider;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
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
    private Pane myPane;

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
    private JFXButton summerBtn;

    @FXML
    private JFXButton winterBtn;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXListView loglist;

    @FXML
    private JFXSlider tempSliderSummer;

    @FXML
    private JFXSlider tempSliderWinter;

    private StackPane stackPane;

    private JFXNodesList foodList;

    private JFXNodesList transportList;

    private JFXNodesList energyList;

    private JFXNodesList tempList;

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

        stackPane = new StackPane();
        ObservableList<PieChart.Data> pieChartData = createData();
        this.chart = new DoughnutChart(pieChartData);
        this.chartContainer.getChildren().add(stackPane);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(chart);
        this.createPoints();
        if (service.getStateSolar().isEnabled()) {
            toggleButton(solarbtn);
        }

        this.foodList = createFoodList();
        this.transportList = createTransportList();
        this.energyList = createEnergyList();
        this.tempList = createTemperatureList();

        addListenerChart();

        tempSliderSummer.setValue(0);
        tempSliderWinter.setValue(0);
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
            toggleButton(solarbtn);
        });
        vegbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> buttonPressed(Action.VEGETARIAN));
        bikebtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> buttonPressed(Action.BIKE));
        tempbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            buttonPressed(Action.TEMP);
//            energyList.animateList();
//            tempList.animateList();
        });
        publicbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> buttonPressed(Action.PUBLIC));
        localbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> buttonPressed(Action.LOCAL));
        summerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.TEMP, (int)tempSliderSummer.getValue()));
        winterBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.TEMP, (int)tempSliderWinter.getValue()));
    }

    private void buttonPressed(Action action) {
        this.service.madeAction(action);
        int points = this.service.getPointsToday();
        this.pointsContainer.setText("Points\nearned\ntoday\n" + points);
        this.stackPane.getChildren().remove(pointsContainer);
        this.logs = this.service.getLog();
        this.loglist.getItems().clear();
        this.logs.forEach(e -> this.loglist.getItems().add(0, new
                Label(e.getAction() + " " + e.getDate())));
        this.updateChart();
        this.stackPane.getChildren().add(pointsContainer);
    }

    private void buttonPressed(Action action, int times) {
        for (int i = 0; i < times; i++) {
            buttonPressed(action);
        }
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

    private JFXNodesList createTemperatureList() {
        return createList(summerBtn, winterBtn);
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
        if (tempList.isExpanded()) {
            tempList.animateList();
        }
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
                    values.put("Energy", values.get("Energy") + log.getAction().getPoints());
                    break;
                default:
            }
        }

        values.put("Energy", values.get("Energy") + service.getStateSolar().getPoints());
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
        if (this.stackPane.getChildren().remove(this.chart)) {
            this.chart = new DoughnutChart(createData());
            this.stackPane.getChildren().add(chart);
        }
        addListenerChart();
    }

    private void toggleButton(JFXButton button) {
        if (button.getStyleClass().contains("toggle-button-off")) {
            button.getStyleClass().remove("toggle-button-off");
            button.getStyleClass().add("animated-option-button-sub");
        } else {
            button.getStyleClass().remove("animated-option-button-sub");
            button.getStyleClass().add("toggle-button-off");
        }
    }

    private void createPoints() {
        int point = service.getPointsToday();
        pointsContainer.setText("Points\nearned\ntoday\n" + point);
        pointsContainer.setBoundsType(TextBoundsType.VISUAL);
        pointsContainer.setFont(new Font(20));
        pointsContainer.setFill(Color.GREEN);
        pointsContainer.setTranslateY(-10);
        stackPane.getChildren().add(pointsContainer);
    }
}