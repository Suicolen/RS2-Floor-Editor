package editor;

import editor.controllers.OverlayController;
import editor.tabs.OverlayTab;
import editor.tabs.UnderlayTab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class Editor extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        OverlayTab overlayTab = new OverlayTab();
        UnderlayTab underlayTab = new UnderlayTab();
        Tab overlay = new Tab("Overlay tab", overlayTab);
        Tab underlay = new Tab("Underlay tab", underlayTab);
        TabPane tabPane = new TabPane(overlay, underlay);
        Scene scene = new Scene(tabPane);
        scene.getStylesheets().add(this.getClass().getResource("/css/darktheme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Floor editor");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
