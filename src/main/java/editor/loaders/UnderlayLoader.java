package editor.loaders;

import editor.cache.CacheLoader;
import editor.rs.Overlay;
import editor.rs.Underlay;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnderlayLoader {

    public static List<Underlay> decode() {
        ByteBuffer buffer = ByteBuffer.wrap(Objects.requireNonNull(CacheLoader.cache.data(0, 2, "flo.dat")));
        List<Underlay> underlays = new ArrayList<>();
        int count = buffer.getShort();
        for (int i = 0; i < count; i++) {
            Underlay underlay = new Underlay();
            underlay.decode(buffer);
            underlays.add(underlay);
        }
        return underlays;
    }
}
