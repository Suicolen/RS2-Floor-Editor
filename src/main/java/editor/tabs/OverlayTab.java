package editor.tabs;

import editor.controllers.OverlayController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class OverlayTab extends AnchorPane {

    private OverlayController controller;

    public OverlayController getController() {
        return controller;
    }

    public OverlayTab() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/overlaytab.fxml"));
        loader.setRoot(this);
        controller = new OverlayController();
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
