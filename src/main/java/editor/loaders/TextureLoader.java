package editor.loaders;

import com.displee.cache.index.archive.file.File;
import editor.cache.CacheLoader;
import editor.rs.RSSprite;
import editor.utils.HashUtils;
import editor.wrapper.TextureWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextureLoader {

    public static List<TextureWrapper> loadTextures() {

        int indexHash = HashUtils.hashName("index.dat");
        List<TextureWrapper> textures = new ArrayList<>();
        int count = 0;
        int remaining = CacheLoader.textureArchive.files().length - 1;


        while (remaining > 1) {
            for (File entry : CacheLoader.textureArchive.files()) {
                if (entry.getHashName() == indexHash) {
                    continue;
                }

                int hash = HashUtils.hashName(count + ".dat");

                if (entry.getHashName() == hash) {
                    try {

                        RSSprite textureImage = RSSprite.decode(CacheLoader.textureArchive, entry.getId(), 0, true); // use false if u haven't modified ur texture format
                        textures.add(new TextureWrapper(count,textureImage));
                        remaining--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
            count++;
        }

        //textures.sort(Comparator.comparing(TextureWrapper::getId).reversed());

        return textures;
    }

}
