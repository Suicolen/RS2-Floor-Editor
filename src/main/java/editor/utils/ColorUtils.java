package editor.utils;

import javafx.scene.paint.Color;

public final class ColorUtils {

    public static int colorToRgb(Color color) {
        int r = (int) (color.getRed() * 255D);
        int g = (int) (color.getGreen() * 255D);
        int b = (int) (color.getBlue() * 255D);
        return (r << 16 | g << 8 | b);
    }

    public static Color getColor(int rgb) {
        return Color.color(getRed(rgb) / 255.0, getGreen(rgb) / 255.0, getBlue(rgb) / 255.0);
    }


    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

}
