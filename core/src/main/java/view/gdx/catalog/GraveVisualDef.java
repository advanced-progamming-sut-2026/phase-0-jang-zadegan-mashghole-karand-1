package view.gdx.catalog;

import java.util.List;

import model.data.Grave.GraveContent;
import model.data.content.chapter.ChapterType;

public final class GraveVisualDef {
    public final String pamPath;
    public final String clip;
    public final float pamCanvas;

    public GraveVisualDef(String pamPath, String clip, float pamCanvas) {
        this.pamPath = pamPath;
        this.clip = clip;
        this.pamCanvas = pamCanvas;
    }

    public static GraveVisualDef forGrave(ChapterType chapter, GraveContent content) {
        if (chapter == ChapterType.DARK_AGES) {
            return switch (content) {
                case SUN_50 -> darkSun();
                case PLANT_FOOD -> darkPlantFood();
                case NONE -> darkNoop();
            };
        }
        if (chapter == ChapterType.ANCIENT_EGYPT) {
            return egypt();
        }
        return tutorial();
    }

    private static GraveVisualDef darkNoop() {
        return new GraveVisualDef(
                "768/FULL/GRAVESTONES/DARK_NOOP/DARK_NOOP.PAM", "undamaged", 390f);
    }

    private static GraveVisualDef darkSun() {
        return new GraveVisualDef(
                "768/FULL/GRAVESTONES/DARK_SUN/DARK_SUN.PAM", "undamaged", 390f);
    }

    private static GraveVisualDef darkPlantFood() {
        return new GraveVisualDef(
                "768/FULL/GRAVESTONES/DARK_PLANTFOOD/DARK_PLANTFOOD.PAM", "undamaged", 390f);
    }

    private static GraveVisualDef egypt() {
        return new GraveVisualDef(
                "768/INITIAL/GRAVESTONES/EGYPT_HIEROGLYPH/EGYPT_HIEROGLYPH.PAM", "undamaged", 390f);
    }

    private static GraveVisualDef tutorial() {
        return new GraveVisualDef(
                "768/INITIAL/GRAVESTONES/TUTORIAL_GRAVESTONE/TUTORIAL_GRAVESTONE.PAM", "undamaged", 390f);
    }

    public static List<GraveVisualDef> all() {
        return List.of(darkNoop(), darkSun(), darkPlantFood(), egypt(), tutorial());
    }
}
