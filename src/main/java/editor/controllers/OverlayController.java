package editor.controllers;

import editor.cache.CacheLoader;
import editor.loaders.OverlayLoader;
import editor.loaders.TextureLoader;
import editor.rs.Overlay;
import editor.utils.ColorUtils;
import editor.wrapper.TextureWrapper;
import javafx.animation.Interpolator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class OverlayController implements Initializable {

    @FXML
    private ListView<Overlay> overlayView;

    @FXML
    private ListView<TextureWrapper> texturesView;

    @FXML
    private ListView<Color> generatedColorsView;

    @FXML
    private TextField textureField, colorField;

    @FXML
    private ColorPicker colorPicker, firstColorPicker, secondColorPicker;

    @FXML
    private Slider paletteCountSlider;

    @FXML
    private ChoiceBox<Interpolator> interpolatorChoiceBox;


    private List<TextureWrapper> textures;

    @FXML
    private Button addButton, packButton, addColorsButton;

    List<Overlay> items = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CacheLoader.init();
        List<Overlay> overlays = OverlayLoader.decode();
        items.addAll(overlays);
        textures = TextureLoader.loadTextures();
        overlays.forEach(overlayView.getItems()::add);
        textures.forEach(texturesView.getItems()::add);
        interpolatorChoiceBox.getItems()
                .addAll(Interpolator.LINEAR, Interpolator.DISCRETE, Interpolator.EASE_IN, Interpolator.EASE_OUT, Interpolator.EASE_BOTH);
        initListeners();
        setupCellFactory();
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

        texturesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        texturesView.setOnMouseClicked(x -> {
            if (texturesView.getSelectionModel().getSelectedItem() != null) {
                textureField.setText(String.valueOf(texturesView.getSelectionModel()
                        .getSelectedItem()
                        .getId()));
            }
        });

        colorPicker.valueProperty().addListener(x -> {
            Color color = colorPicker.getValue();
            int rgb = ColorUtils.colorToRgb(color);
            colorField.setText(String.valueOf(rgb));
        });

        addButton.setOnMouseClicked(x -> {
            if (texturesView.getSelectionModel().getSelectedItems() != null) {
                texturesView.getSelectionModel().getSelectedItems().forEach(texture -> {
                    Overlay overlay = new Overlay();
                    overlay.setTexture(texture.getId());
                    overlayView.getItems().add(overlay);
                    items.add(overlay);
                    System.out.println("added " + overlay.getTexture());
                });
            } else {
                Overlay overlay = new Overlay();
                int color = Integer.parseInt(colorField.getText());
                overlay.setColor(color);
                overlayView.getItems().add(overlay);
                items.add(overlay);
            }

        });

        addColorsButton.setOnMouseClicked(x -> {
            for (Color color : generatedColorsView.getItems()) {
                Overlay overlay = new Overlay();
                int rgb = ColorUtils.colorToRgb(color);
                overlay.setColor(rgb);
                overlayView.getItems().add(overlay);
                items.add(overlay);
            }
        });

        packButton.setOnMouseClicked(x -> {
            try {
                Overlay.encode(items);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        firstColorPicker.valueProperty().addListener(x -> {
            updateColors();
        });

        secondColorPicker.valueProperty().addListener(x -> {
            updateColors();
        });

        paletteCountSlider.valueProperty().addListener(x -> {
            //System.out.println("Current slider value: " + (int) paletteCountSlider.getValue());
            updateColors();
        });

        interpolatorChoiceBox.valueProperty().addListener(x -> {
            updateColors();
        });
    }

    private void updateColors() {
        if (interpolatorChoiceBox.getValue() == null) {
            return;
        }
        List<Color> colors = generateColors();
        generatedColorsView.getItems().setAll(colors);
    }

    private void setupCellFactory() {
        overlayView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Overlay item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    if (textures.size() > item.getTexture() && item.getTexture() != -1 && item.getTexture() == textures.get(item
                            .getTexture()).getId()) {
                        int id = item.getTexture();
                        BufferedImage img = textures.get(id).getImage().toBufferedImage();
                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(32);
                        imageView.setFitHeight(32);
                        imageView.setImage(SwingFXUtils.toFXImage(img, null));
                        setGraphic(imageView);
                        setText("Texture " + item.getTexture());
                    } else {
                        Rectangle rectangle = new Rectangle();
                        rectangle.setWidth(32);
                        rectangle.setHeight(32);
                        int rgb = item.getColor();
                        rectangle.setFill(ColorUtils.getColor(rgb));
                        rectangle.setStroke(Color.BLACK);
                        rectangle.setStrokeWidth(1);
                        setGraphic(rectangle);
                        setText("RGB: " + ColorUtils.getRed(rgb) + " | " + ColorUtils.getGreen(rgb) + " | " + ColorUtils
                                .getBlue(rgb));
                    }
                }

            }
        });

        texturesView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TextureWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    BufferedImage img = item.getImage().toBufferedImage();
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(64);
                    imageView.setFitHeight(64);
                    imageView.setImage(SwingFXUtils.toFXImage(img, null));
                    setGraphic(imageView);
                    setText("Texture " + item.getId());
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
