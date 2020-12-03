package editor.wrapper;


import editor.rs.RSSprite;

public class TextureWrapper {

    private final int id;
    private final RSSprite image;

    public TextureWrapper(int id, RSSprite image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public RSSprite getImage() {
        return image;
    }


}