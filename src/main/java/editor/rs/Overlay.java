package editor.rs;

import editor.cache.CacheLoader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Overlay {

    private int texture = -1;
    private int color;

    public int getTexture() {
        return texture;
    }

    public int getColor() {
        return color;
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public static void encode(List<Overlay> overlays) throws IOException {
        DataOutputStream buffer = new DataOutputStream(new FileOutputStream(new java.io.File("./flo2.dat")));
        buffer.writeShort(overlays.size());
        for (Overlay overlay : overlays) {

            if (overlay.getColor() != -1) {
                buffer.write(1);
                int rgb = overlay.getColor();
                buffer.write((rgb >> 16) & 0xFF);
                buffer.write((rgb >> 8) & 0xFF);
                buffer.write(rgb & 0xFF);
            }

            if (overlay.getTexture() != -1) {
                if (overlay.getTexture() > 127) {
                    buffer.write(3);
                    buffer.writeShort(overlay.getTexture());
                } else {
                    buffer.write(2);
                    buffer.write(overlay.getTexture());

                }
            }

            buffer.write(0);
        }
        byte[] data = Files.readAllBytes(Path.of("./flo2.dat"));
        CacheLoader.configArchive.add("flo2.dat", data);
        CacheLoader.cache.update();
    }


    public void decode(ByteBuffer byteBuffer) {
        for (; ; ) {
            int opcode = byteBuffer.get();
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                color = ((byteBuffer.get() & 0xff) << 16) + ((byteBuffer.get() & 0xff) << 8) + (byteBuffer.get() & 0xff);
            } else if (opcode == 2) {
                texture = byteBuffer.get() & 0xff;
            } else if (opcode == 3) {
                texture = byteBuffer.getShort() & 0xffff;
                if (texture == 65535) {
                    texture = -1;
                }
            } else if (opcode == 4) {

            } else if (opcode == 5) {

            } else if (opcode == 6) {

            } else if (opcode == 7) {
                byteBuffer.get();
                byteBuffer.get();
                byteBuffer.get();
            } else if (opcode == 8) {

            } else if (opcode == 9) {
                byteBuffer.getShort();
            } else if (opcode == 10) {

            } else if (opcode == 11) {
                byteBuffer.get();
            } else if (opcode == 12) {

            } else if (opcode == 13) {
                byteBuffer.get();
                byteBuffer.get();
                byteBuffer.get();

            } else if (opcode == 14) {
                byteBuffer.get();
            } else if (opcode == 15) {
                byteBuffer.getShort();
            } else if (opcode == 16) {
                byteBuffer.get();
            } else {
                System.err.println("[OverlayFloor] Missing opcode: " + opcode);
            }
        }
    }


}