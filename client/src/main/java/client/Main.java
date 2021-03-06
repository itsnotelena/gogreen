package client;

import client.gui.tools.SceneNames;
import client.gui.tools.SpringFxmlLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan( {"client.gui.controllers"})
public class Main extends Application {
    private ConfigurableApplicationContext springContext;
    private Parent root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(Main.class);
        SpringFxmlLoader loader = new SpringFxmlLoader();
        root = (Parent) loader.load("/" + SceneNames.LOGIN);
    }

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene( root,600,500 );
        primaryStage.setTitle("GoGreen");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.stop();
    }
}
