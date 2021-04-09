package editor.rs;

import com.displee.cache.index.archive.Archive;
import editor.utils.ByteBufferUtils;
import editor.utils.HashUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class RSSprite {

    private int width;
    private int height;
    private int offsetX;
    private int offsetY;
    private int resizeWidth;
    private int resizeHeight;
    private int[] pixels;
    private int format;

    /**
     * new stuff
     */

    private int[] palette;

    public int[] getPalette() {
        return palette;
    }

    public RSSprite() {

    }

    public RSSprite(BufferedImage bimage) {
        this.pixels = ((DataBufferInt)bimage.getRaster().getDataBuffer()).getData();
        this.width = bimage.getWidth();
        this.height = bimage.getHeight();
        this.resizeWidth = bimage.getWidth();
        this.resizeHeight = bimage.getHeight();
    }

    public RSSprite(int width, int height) {
        this.pixels = new int[width * height];
        this.width = this.resizeWidth = width;
        this.height = this.resizeHeight = height;
    }

    public RSSprite(int resizeWidth, int resizeHeight, int horizontalOffset, int verticalOffset, int width, int height, int format,
                    int[] pixels) {
        this.resizeWidth = resizeWidth;
        this.resizeHeight = resizeHeight;
        this.offsetX = horizontalOffset;
        this.offsetY = verticalOffset;
        this.width = width;
        this.height = height;
        this.format = format;
        this.pixels = pixels;
    }

    public static RSSprite decode(Archive archive, int hash, int id, boolean newFormat) throws IOException {
        ByteBuffer dataBuffer = ByteBuffer.wrap(Objects.requireNonNull(Objects.requireNonNull(archive.file(hash)).getData()));
        ByteBuffer indicesBuffer = ByteBuffer.wrap(Objects.requireNonNull(Objects.requireNonNull(archive.file("index.dat"))
                .getData()));

        RSSprite sprite = new RSSprite();

        // position of the current image archive within the archive
        int offset = newFormat ? dataBuffer.getInt() : dataBuffer.getShort() & 0xFFFF;
        indicesBuffer.position(offset);

        // the maximum width the images in this archive can scale to
        sprite.setResizeWidth(indicesBuffer.getShort() & 0xFFFF);

        // the maximum height the images in this archive can scale to
        sprite.setResizeHeight(indicesBuffer.getShort() & 0xFFFF);

        // the number of colors that are used in this image archive (limit is 256 if one of the rgb values is 0 else its 255)
        int colours = newFormat ? indicesBuffer.getShort() & 0xFFFF : indicesBuffer.get() & 0xFF;

        // the array of colors that can only be used in this archive
        sprite.palette = new int[colours];

        for (int index = 0; index < colours - 1; index++) {
            int colour = ByteBufferUtils.readU24Int(indicesBuffer);
            // + 1 because index = 0 is for transparency, = 1 is a flag for opacity. (BufferedImage#OPAQUE)
            sprite.palette[index + 1] = colour == 0 ? 1 : colour;
        }

        for (int i = 0; i < id; i++) {

            if (indicesBuffer.position() + 7 >= indicesBuffer.capacity()) {
                return null;
            }

            // skip the current offsetX and offsetY
            indicesBuffer.position(indicesBuffer.position() + 2);

            // skip the current array of pixels
            dataBuffer.position(dataBuffer.position() + ((indicesBuffer.getShort() & 0xFFFF) * (indicesBuffer.getShort() & 0xFFFF)));

            // skip the current format
            indicesBuffer.position(indicesBuffer.position() + 1);
        }

        // offsets are used to reposition the sprite on an interface.
        sprite.setOffsetX(indicesBuffer.get() & 0xFF);
        sprite.setOffsetY(indicesBuffer.get() & 0xFF);

        // actual width of this sprite
        sprite.setWidth(indicesBuffer.getShort() & 0xFFFF);

        // actual height of this sprite
        sprite.setHeight(indicesBuffer.getShort() & 0xFFFF);

        // there are 2 ways the pixels can be written (0 or 1, 0 means the position is read horizontally, 1 means vertically)
        sprite.setFormat(indicesBuffer.get() & 0xFF);

        if (sprite.getFormat() != 0 && sprite.getFormat() != 1) {
            throw new IOException(String.format("Detected end of archive=%d id=%d or wrong format=%d", hash, id, sprite.getFormat()));
        }

        if (sprite.getWidth() > 765 || sprite.getHeight() > 765 || sprite.getWidth() <= 0 || sprite.getHeight() <= 0) {
            throw new IOException(String.format("Detected end of archive=%d id=%d", hash, id));
        }

        int[] raster = new int[sprite.getWidth() * sprite.getHeight()];

        if (sprite.getFormat() == 0) { // read horizontally
            for (int index = 0; index < raster.length; index++) {
                raster[index] = sprite.palette[newFormat ? dataBuffer.getShort() & 0xFFFF : dataBuffer.get() & 0xFF];
                //System.err.println("Decoded color: " + raster[index]);
            }
        } else { // read vertically
            for (int x = 0; x < sprite.getWidth(); x++) {
                for (int y = 0; y < sprite.getHeight(); y++) {
                    raster[x + y * sprite.getWidth()] = sprite.palette[newFormat ? dataBuffer.getShort() & 0xFFFF : dataBuffer.get() & 0xFF];
                    // System.err.println("Decoded color here: " + raster[x + y * sprite.getWidth()]);
                }
            }
        }
        sprite.setPixels(raster);
        return sprite;
    }


    public static RSSprite decode(Archive archive, String name, int id, boolean newFormat) throws IOException {
        return decode(archive, HashUtils.hashName(name.contains(".dat") ? name : name + ".dat"), id, newFormat);
    }


    public BufferedImage toBufferedImage() {

        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

        final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(this.pixels, 0, pixels, 0, this.pixels.length);

        return image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getResizeWidth() {
        return resizeWidth;
    }

    public void setResizeWidth(int resizeWidth) {
        this.resizeWidth = resizeWidth;
    }

    public int getResizeHeight() {
        return resizeHeight;
    }

    public void setResizeHeight(int resizeHeight) {
        this.resizeHeight = resizeHeight;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

}
