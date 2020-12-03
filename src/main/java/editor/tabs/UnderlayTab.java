package editor.tabs;

import editor.controllers.OverlayController;
import editor.controllers.UnderlayController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UnderlayTab extends AnchorPane {

    private UnderlayController controller;

    public UnderlayController getController() {
        return controller;
    }

    public UnderlayTab() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/underlaytab.fxml"));
        loader.setRoot(this);
        controller = new UnderlayController();
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
