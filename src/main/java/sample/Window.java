package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ghita on 5/19/2016.
 */
public class Window  implements Initializable {
    public Button start;
    public ComboBox portSet;
    public Button stop;
    public ComboBox set3dObject;
    public MenuItem about;
    public ObservableList<String> listPorts;
    public ObservableList<String> listObject;
    public SerialPort[] ports;
    public ExecutorService exec;
    public Button stosp;
    public Stage stage;
    public Shape3D shape3D;
    public DataThread thread;
    public MenuItem Close;

    public void initialize(URL location, ResourceBundle resources) {
        listPorts = FXCollections.observableArrayList();
        listObject = FXCollections.observableArrayList();
        listObject.add("Cube");
        listObject.add("Sphere");
        set3dObject.setItems(listObject);
        set3dObject.getSelectionModel().select("Cube");
        ports = SerialPort.getCommPorts();
        int i = 0;
        for(SerialPort port : ports) {
            System.out.println(i++ + ": " + port.getSystemPortName().toString());
            listPorts.add(port.getSystemPortName().toString());
        }
        portSet.setItems(listPorts);
        portSet.getSelectionModel().select(0);
        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        Image ico = new Image(String.valueOf(getClass().getResource("/3D_256.png")));
        stage.getIcons().add(ico);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                if(thread != null){
                    thread.setContinueRead(false);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    thread.closeThread();
                }
            }
        });
        about.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("GiroApp");
                alert.setHeaderText("GiroApp v1.0");
                alert.setContentText("Licensed to Mihai Ghita. \nFor more information contact as at ghitamihai@hotmail.com" +
                        " \nCopyright©2015-2016, GhitaCompany - All Rights Reserved");
                alert.showAndWait();
            }
        });
        Close.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                closeWindow();
                Platform.exit();
            }
        });
    }


    public void stop(ActionEvent actionEvent) {
        this.stopAction();
    }

    public void stopAction(){
        System.out.println("Stop");
        if(thread != null){
            thread.setContinueRead(false);
        }
        if(exec != null){
            exec.shutdown();
        }
        stage.close();
    }

    public void start(ActionEvent actionEvent) {
        if(portSet.getSelectionModel().getSelectedItem() != null) {
            stopAction();
            exec = Executors.newSingleThreadExecutor();
            System.out.println("Start");
            String port = (String) portSet.getSelectionModel().getSelectedItem();
            System.out.println(listPorts.indexOf(port) + " " + portSet.getSelectionModel().getSelectedItem());
            int portIndex = listPorts.indexOf(port);
            shape3D = null;
            if (set3dObject.getSelectionModel().getSelectedItem().equals("Cube")) {
                shape3D = createCube();
            }

            if (set3dObject.getSelectionModel().getSelectedItem().equals("Sphere")) {
                shape3D = createSphere();
            }
            System.out.println("Selected port: " + ports[portIndex].getSystemPortName());
            open3dObjectWindow();
            thread = new DataThread(ports[portIndex], shape3D);
            thread.setContinueRead(true);
            exec.execute(thread);
        }
    }

    private Shape3D createSphere() {
        Sphere sphere = new Sphere(200);
        sphere.setTranslateX(50);
        sphere.setTranslateY(50);
        //System.out.printf(getClass().getResource("").toString());
        Image earthImage = new Image(getClass().getResource("earth-map-down.jpg").toExternalForm());
        BooleanProperty diffuseMap = new SimpleBooleanProperty(true);
        PhongMaterial earthPhong = new PhongMaterial();
        earthPhong.diffuseMapProperty().bind(
                Bindings.when(diffuseMap).then(earthImage).otherwise((Image) null));
        sphere.setMaterial(earthPhong);
        return sphere;
    }


    public void open3dObjectWindow(){
        HBox hBox = new HBox();
        Group group = new Group();
        group.getChildren().addAll(shape3D);
        hBox.getChildren().add(shape3D);
        Scene scene = new Scene(hBox, 500, 500);
        stage.setTitle("3D Animation");
        stage.setScene(scene);
        stage.show();
    }

    public Shape3D createCube(){
        Box myBox = new Box(300, 300, 300);
        PhongMaterial blueStuff = new PhongMaterial();
        blueStuff.setDiffuseColor(Color.LIGHTBLUE);
        blueStuff.setSpecularColor(Color.BLUE);
        myBox.setMaterial(blueStuff);
        myBox.setTranslateX(100);
        myBox.setTranslateY(100);
        myBox.setTranslateZ(100);
        return myBox;
    }

    public void closeWindow(){
        stage.close();
        if(thread != null){
            thread.setContinueRead(false);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.closeThread();
        }
        if(exec != null){
            exec.shutdown();
        }

        System.out.println("Stage is closing");
    }

    public boolean stageIsOpen() {
        return stage.isShowing();
    }
}
