package view.gdx.catalog;

public final class ZombieVisualDef {
    public final String pamPath;
    public final String idleClip;
    public final String walkClip;
    public final String eatClip;
    public final String dieClip;
    public final ArmorVisualRecipe armor;

    public ZombieVisualDef(String pamPath, String idleClip, String walkClip, String eatClip, String dieClip,
            ArmorVisualRecipe armor) {
        this.pamPath = pamPath;
        this.idleClip = idleClip;
        this.walkClip = walkClip;
        this.eatClip = eatClip;
        this.dieClip = dieClip;
        this.armor = armor;
    }

    public static ZombieVisualDef plain(String pamPath, String idle, String walk, String eat, String die) {
        return new ZombieVisualDef(pamPath, idle, walk, eat, die, null);
    }
}
