package view.gdx.catalog;

public final class PlantVisualDef {
    public final String pamPath;
    public final String idleClip;
    public final String attackClip;
    public final String plantFoodClip;


    public PlantVisualDef(String pamPath, String idleClip, String attackClip, String plantFoodClip) {
        this.pamPath = pamPath;
        this.idleClip = idleClip;
        this.attackClip = attackClip;
        this.plantFoodClip = plantFoodClip;
    }
}
