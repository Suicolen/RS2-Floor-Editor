package editor.rs;


import com.sun.prism.impl.BufferUtil;
import editor.cache.CacheLoader;
import editor.utils.ByteBufferUtils;
import editor.utils.ColorUtils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

public final class Underlay {

    public void decode(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.get();
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                int rgb = ByteBufferUtils.readU24Int(buffer);
                setRgb(rgb);
            } else if (opcode == 2) {
                int texture = buffer.get();
                setTexture(texture);
            }
        }
    }

    public static void encode(List<Underlay> underlays) {
        ByteBuffer buffer = ByteBuffer.allocate(2 + (7 * underlays.size()));
        buffer.putShort((short) underlays.size());
        underlays.forEach(underlay -> {
            if (underlay.getRgb() != -1) {
                buffer.put((byte) 1);
                ByteBufferUtils.write24Int(buffer, underlay.getRgb());
            }

            if (underlay.getTexture() != -1) {
                buffer.put((byte) 2);
                buffer.put((byte) underlay.getTexture());
            }

            buffer.put((byte) 0);
        });
        CacheLoader.configArchive.add("flo.dat", buffer.array());
        CacheLoader.cache.update();
    }


    private int rgb = -1;
    private int texture = -1;

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }

    public int getTexture() {
        return texture;
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }
}
