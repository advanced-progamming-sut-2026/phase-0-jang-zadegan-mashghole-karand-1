package view.gdx.catalog;

import java.util.List;

import model.data.sun.SunType;

public final class SunVisualDef {
    public static final String NORMAL_PAM = "768/INITIAL/EFFECTS/SUN/SUN.PAM";
    public static final String SPECIAL_PAM = "768/FULL/EFFECTS/SUN_BOMB/SUN_BOMB.PAM";

    public final String pamPath;
    public final String clipName;
    public final float pamCanvas;
    public final float sizeFactor;

    public SunVisualDef(String pamPath, String clipName, float pamCanvas, float sizeFactor) {
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.pamCanvas = pamCanvas;
        this.sizeFactor = sizeFactor;
    }

    public static SunVisualDef forType(SunType type) {
        return switch (type) {
            case SPECIAL -> new SunVisualDef(NORMAL_PAM, "animation", 390f, 4.5f);
            case RADIO_ACTIVE -> new SunVisualDef(SPECIAL_PAM, "animation", 390f, 1.5f);
            default -> new SunVisualDef(NORMAL_PAM, "animation", 200f, 1.5f);
        };
    }

    public static List<SunVisualDef> all() {
        return List.of(
                forType(SunType.NORMAL),
                forType(SunType.SPECIAL),
                forType(SunType.RADIO_ACTIVE));
    }
}
