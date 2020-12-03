package editor.loaders;

import editor.cache.CacheLoader;
import editor.rs.Overlay;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OverlayLoader {

    public static List<Overlay> decode() {
        ByteBuffer buffer = ByteBuffer.wrap(Objects.requireNonNull(CacheLoader.cache.data(0, 2, "flo2.dat")));

        List<Overlay> overlays = new ArrayList<>();
        int count = buffer.getShort();
        for (int i = 0; i < count; i++) {
            Overlay overlay = new Overlay();
            overlay.decode(buffer);
            if (i == 113) {
                overlay.setTexture(25);
            }
            overlays.add(overlay);
        }
        return overlays;
    }
}
