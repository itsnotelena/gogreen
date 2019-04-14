package client.gui.controllers;

import static client.gui.tools.SceneNames.DRAWER_SIZE;
import static client.gui.tools.SceneNames.HISTORY;
import static client.gui.tools.SceneNames.SETTINGS;
import static client.gui.tools.SceneNames.TOOLBAR;

import client.gui.tools.AbstractController;
import client.gui.tools.DoughnutChart;
import client.services.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
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
import javafx.scene.text.TextFlow;
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

    private static final int minChartSlice = 5;

    private UserService service;


    @FXML
    private StackPane wrapper;

    @FXML
    private Pane myPane;

    @FXML
    private Pane pane1;

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
    private JFXSlider tempSliderSummer;

    @FXML
    private JFXSlider tempSliderWinter;

    @FXML
    private JFXButton infoIcon;

    @FXML
    private StackPane stackPaneChart;
    @FXML
    private StackPane stackPane;

    @FXML
    private Text usernameField;

    private JFXNodesList foodList;

    private JFXNodesList transportList;

    private JFXNodesList energyList;

    private JFXNodesList tempList;

    private DoughnutChart chart;

    @Autowired
    public MainController(UserService service) {
        this.service = service;
    }

    @FXML
    public void goToSettings() throws IOException {
        goToLarge(myPane, SETTINGS);
    }

    @FXML
    public void goToHistory() throws IOException {
        goToLarge( myPane, HISTORY );
    }

    /**
     * Shows user settings/history pane.
     */
    @FXML
    public void show() {
        if (pane1.isVisible()) {
            pane1.setVisible( false );
        } else {
            pane1.setVisible( true );
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {

        pane1.setVisible( false );
        int pointsForBackground = service.getPoints();
        if (pointsForBackground < 1000) {
            wrapper.setId("background2");
        } else if (pointsForBackground < 2500) {
            wrapper.setId("background3");
        } else if (pointsForBackground < 10000) {
            wrapper.setId("background4");
        } else {
            wrapper.setId("background5");
        }


        this.usernameField.setText(service.getUser().getUsername());
        try {
            myPane = FXMLLoader.load(getClass().getResource(TOOLBAR));
            drawer.setSidePane(myPane);
            drawer.setDefaultDrawerSize(DRAWER_SIZE);

            drawer.setResizableOnDrag(true);
            HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
            task.setRate(task.getRate() * -1);

            this.initializeHamburger(task, hamburger, drawer);

        } catch (IOException e) {
            e.printStackTrace();
        }


        stackPane = new StackPane();
        ObservableList<PieChart.Data> pieChartData = createData();
        this.chart = new DoughnutChart(pieChartData);
        stackPaneChart.setAlignment(Pos.CENTER);
        chartContainer.getChildren().add(chart);
        this.createPoints();
        if (service.getStateSolar().isEnabled()) {
            toggleButton(solarbtn);
        }

        infoIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> loadUserInfo());

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
            buttonPressed(Action.SOLAR, 1);
            toggleButton(solarbtn);
        });
        vegbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.VEGETARIAN, 1));
        bikebtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.BIKE, 1));
        tempbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            energyList.animateList();
            tempList.animateList();
        });
        publicbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.PUBLIC, 1));
        localbtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.LOCAL, 1));
        summerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.TEMP, (int) tempSliderSummer.getValue()));
        winterBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                buttonPressed(Action.TEMP, (int) tempSliderWinter.getValue()));
    }

    private void buttonPressed(Action action, int amount) {
        if (amount != 0) {
            this.service.madeAction(action, amount);
            int points = this.service.getPointsToday();
            this.pointsContainer.setText("Points\nearned\ntoday\n" + points);
            this.stackPaneChart.getChildren().remove(pointsContainer);
            this.updateChart();
            this.stackPaneChart.getChildren().add(pointsContainer);
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
                    values.put("Food", values.get("Food") + log.getPoints());
                    break;
                case BIKE:
                case PUBLIC:
                    values.put("Transport", values.get("Transport") + log.getPoints());
                    break;
                case TEMP:
                    values.put("Energy", values.get("Energy") + log.getPoints());
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
        if (this.chartContainer.getChildren().remove(this.chart)) {
            this.chart = new DoughnutChart(createData());
            this.chartContainer.getChildren().add(chart);
        }
        addListenerChart();
    }

    private void toggleButton(JFXButton button) {
        if (button.getStyleClass().contains("button-energy-off")) {
            button.getStyleClass().remove("button-energy-off");
            button.getStyleClass().add("button-energy");
        } else {
            button.getStyleClass().remove("button-energy");
            button.getStyleClass().add("button-energy-off");
        }
    }

    private void createPoints() {
        int point = service.getPointsToday();
        pointsContainer.setText("Points\nearned\ntoday\n" + point);
        pointsContainer.setBoundsType(TextBoundsType.VISUAL);
        pointsContainer.setFont(new Font(20));
        pointsContainer.setFill(Color.GREEN);
        pointsContainer.setTranslateY(-10);
        stackPaneChart.getChildren().add(pointsContainer);
    }

    private void loadUserInfo() {
        int points = service.getPoints();
        JFXDialogLayout content = new JFXDialogLayout();
        Text heading = new Text("Points information");
        heading.setFont(Font.font("Montserrat"));
        content.setHeading(new Text("Information"));
        Text totalPoints = new Text("Total Points: " + points + "\n");
        totalPoints.setFont(Font.font("Montserrat", 12));
        Text totalCO2 = new Text("Total CO2 saved: "
                + String.format("%.2f",points / 636.0) + " kgCO2");
        totalCO2.setFont(Font.font("Montserrat", 12));
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().addAll(totalPoints, totalCO2);
        content.setBody(textFlow);
        JFXDialog dialog = new JFXDialog(wrapper, content, JFXDialog.DialogTransition.CENTER);
        dialog.show();
    }
}