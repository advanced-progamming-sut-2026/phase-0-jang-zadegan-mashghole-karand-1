package view.gdx.catalog;

public class CompanionVisual {
    public final String pamPath;
    public final String clipName;
    public final float offsetX;
    public final float offsetY;
    public final boolean onlyWhileArmored;

    public CompanionVisual(String pamPath, String clipName,  float offsetX, float offsetY, boolean onlyWhileArmored) {
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.onlyWhileArmored = onlyWhileArmored;
    }
}
