package view.gdx.catalog;

import java.util.List;

import model.data.content.chapter.ChapterType;

public final class MowerVisualDef {
    public final String pamPath;
    public final String idleClip;
    public final float pamCanvas;

    public MowerVisualDef(String pamPath, String idleClip, float pamCanvas) {
        this.pamPath = pamPath;
        this.idleClip = idleClip;
        this.pamCanvas = pamCanvas;
    }

    public static MowerVisualDef forChapter(ChapterType chapter) {
        if (chapter == null) {
            return tutorial();
        }
        return switch (chapter) {
            case ANCIENT_EGYPT -> egypt();
            case FROSTBITE_CAVES -> iceAge();
            case BIG_WAVE_BEACH -> beach();
            case DARK_AGES -> dark();
        };
    }

    private static MowerVisualDef tutorial() {
        return new MowerVisualDef(
                "768/INITIAL/MOWERS/MOWER_TUTORIAL/MOWER_TUTORIAL.PAM", "idle", 390f);
    }

    private static MowerVisualDef egypt() {
        return new MowerVisualDef(
                "768/INITIAL/MOWERS/MOWER_EGYPT/MOWER_EGYPT.PAM", "idle", 390f);
    }

    private static MowerVisualDef iceAge() {
        return new MowerVisualDef(
                "768/FULL/MOWERS/MOWER_ICEAGE/MOWER_ICEAGE.PAM", "idle", 390f);
    }

    private static MowerVisualDef beach() {
        return new MowerVisualDef(
                "768/FULL/MOWERS/MOWER_BEACH/MOWER_BEACH.PAM", "idle", 390f);
    }

    private static MowerVisualDef dark() {
        return new MowerVisualDef(
                "768/FULL/MOWERS/MOWER_DARK/MOWER_DARK.PAM", "idle", 390f);
    }

    public static List<MowerVisualDef> all() {
        return List.of(tutorial(), egypt(), iceAge(), beach(), dark());
    }
}
