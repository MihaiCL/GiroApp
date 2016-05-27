package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by ghita on 5/19/2016.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(final Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Window.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        final Window window = loader.getController();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("GiroApp");
        Image ico = new Image(String.valueOf(getClass().getResource("/3D_256.png")));
        primaryStage.getIcons().add(ico);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                window.closeWindow();
                Platform.exit();
            }
        });
    }
}
