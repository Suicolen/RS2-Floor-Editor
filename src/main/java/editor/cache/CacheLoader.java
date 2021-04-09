package editor.cache;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import javafx.stage.DirectoryChooser;


import java.io.File;

public class CacheLoader { // meh

    public static CacheLibrary cache;
    public static Archive configArchive;
    public static Archive textureArchive;

    public static void init() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Load Cache");
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/LuminiteCache/"));
        File folder = dirChooser.showDialog(null);
        initCache(folder);

    }

    public static void initCache(File folder) {
        if (folder != null) {
            cache = CacheLibrary.create(folder.getAbsolutePath());
            textureArchive = cache.index(0).archive(6);
            configArchive = cache.index(0).archive(2);
        }
    }

}
