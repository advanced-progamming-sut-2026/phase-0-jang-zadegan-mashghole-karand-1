package view.gdx.catalog;

public final class ArmorVisualRecipe {
    public final String groupPart;
    public final String intactPart;
    public final String midDamagePart;
    public final String lowDamagePart;

    public ArmorVisualRecipe(String groupPart, String intactPart, String midDamagePart, String lowDamagePart) {
        this.groupPart = groupPart;
        this.intactPart = intactPart;
        this.midDamagePart = midDamagePart;
        this.lowDamagePart = lowDamagePart;
    }
}
