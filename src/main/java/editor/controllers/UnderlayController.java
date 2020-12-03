package editor.controllers;

import editor.loaders.UnderlayLoader;
import editor.rs.Overlay;
import editor.rs.Underlay;
import editor.utils.ColorUtils;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;

public class UnderlayController implements Initializable {

    @FXML
    private ListView<Underlay> underlayView;

    @FXML
    private ListView<Color> generatedColorsView;

    private List<Underlay> underlays = new ArrayList<>();

    @FXML
    private TextField colorField;

    @FXML
    private ColorPicker colorPicker, firstColorPicker, secondColorPicker;

    @FXML
    private Slider paletteCountSlider;

    @FXML
    private ChoiceBox<Interpolator> interpolatorChoiceBox;
    @FXML
    private Button addButton, packButton, addColorsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Underlay> data = UnderlayLoader.decode();
        underlays.addAll(data);
        data.sort(Comparator.comparingInt(Underlay::getRgb));
        underlayView.getItems().setAll(data);
        interpolatorChoiceBox.getItems()
                .addAll(Interpolator.LINEAR, Interpolator.DISCRETE, Interpolator.EASE_IN, Interpolator.EASE_OUT, Interpolator.EASE_BOTH);
        interpolatorChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Interpolator interpolator) {
                String str = interpolator.toString();
                return str.substring(str.indexOf(".") + 1);
            }

            @Override
            public Interpolator fromString(String string) {
                return null;
            }
        });
        setupCellFactory();
        initListeners();
    }

    private List<Color> generateColors() {
        if (firstColorPicker.getValue() == null || secondColorPicker.getValue() == null) {
            return Collections.emptyList();
        }
        List<Color> colors = new ArrayList<>();
        Color firstColor = firstColorPicker.getValue();
        Color secondColor = secondColorPicker.getValue();
        int count = (int) paletteCountSlider.getValue();
        double step = 1D / count;
        Interpolator interpolator = interpolatorChoiceBox.getValue();
        for (double factor = 0; factor <= 1; factor += step) {
            int r1 = (int) (firstColor.getRed() * 255D);
            int g1 = (int) (firstColor.getGreen() * 255D);
            int b1 = (int) (firstColor.getBlue() * 255D);

            int r2 = (int) (secondColor.getRed() * 255D);
            int g2 = (int) (secondColor.getGreen() * 255D);
            int b2 = (int) (secondColor.getBlue() * 255D);

            int interpolatedRed = interpolator.interpolate(r1, r2, factor);
            int interpolatedGreen = interpolator.interpolate(g1, g2, factor);
            int interpolatedBlue = interpolator.interpolate(b1, b2, factor);

            int resultRGB = (interpolatedRed << 16 | interpolatedGreen << 8 | interpolatedBlue);

            Color color = ColorUtils.getColor(resultRGB);
            colors.add(color);
        }
        return colors;
    }


    private void initListeners() {
        colorPicker.valueProperty().addListener(x -> {
            Color color = colorPicker.getValue();
            int rgb = ColorUtils.colorToRgb(color);
            colorField.setText(String.valueOf(rgb));
        });

        addButton.setOnMouseClicked(x -> {
            Underlay underlay = new Underlay();
            int color = Integer.parseInt(colorField.getText());
            underlay.setRgb(color);
            underlayView.getItems().add(underlay);
            underlays.add(underlay);
        });

        packButton.setOnMouseClicked(x -> {
            Underlay.encode(underlays);
        });

        firstColorPicker.valueProperty().addListener(x -> {
            //  generateColors();
        });

        secondColorPicker.valueProperty().addListener(x -> {
            // generateColors();
        });

        paletteCountSlider.valueProperty().addListener(x -> {
            updateColors();
        });

        interpolatorChoiceBox.valueProperty().addListener(x -> {
            updateColors();
        });

        addColorsButton.setOnMouseClicked(x -> {
            for (Color color : generatedColorsView.getItems()) {
                Underlay overlay = new Underlay();
                int rgb = ColorUtils.colorToRgb(color);
                overlay.setRgb(rgb);
                underlayView.getItems().add(overlay);
                underlays.add(overlay);
            }
        });

    }

    private void updateColors() {
        List<Color> colors = generateColors();
        generatedColorsView.getItems().setAll(colors);
    }

    private void setupCellFactory() {
        underlayView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Underlay item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Rectangle rectangle = new Rectangle();
                    rectangle.setWidth(32);
                    rectangle.setHeight(32);
                    int rgb = item.getRgb();
                    rectangle.setFill(ColorUtils.getColor(rgb));
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setStrokeWidth(1);
                    setGraphic(rectangle);
                    setText("RGB: " + ColorUtils.getRed(rgb) + " | " + ColorUtils.getGreen(rgb) + " | " + ColorUtils.getBlue(rgb));
                }

            }
        });

        generatedColorsView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Rectangle rectangle = new Rectangle(32, 32);
                    rectangle.setStrokeWidth(1);
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setFill(item);
                    setGraphic(rectangle);
                }
            }
        });
    }
}
