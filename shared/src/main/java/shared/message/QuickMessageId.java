package shared.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum QuickMessageId {
    MSG_GG("gg", Kind.TEXT, null, null, null),
    MSG_HELLO("hello", Kind.TEXT, null, null, null),
    MSG_AFK("afk", Kind.TEXT, null, null, null),
    EMOJI_ZOMBIES("", Kind.EMOJI, "IMAGE_UI_STORE_TABICONS_ZOMBIES", null, null),
    EMOJI_PLANTS("", Kind.EMOJI, "IMAGE_UI_STORE_TABICONS_PLANTS", null, null),
    EMOJI_CROWN("", Kind.EMOJI, "IMAGE_UI_JOUST_ICONS_CROWNS_CROWNS_LARGE", null, null),
    ANIM_CLOCK("", Kind.ANIMATED, null,
            "768/FULL/UI/PENNY_PURSUITS/ZOMBOSS/CLOCK_ICON/CLOCK_ICON.PAM", "default"),
    ANIM_DIFFICULTY("", Kind.ANIMATED, null,
            "768/DEV/UI/QUESTS/DIFFICULTY_METER/DIFFICULTY_METER.PAM", "animation"),
    ANIM_EXPLOSION("", Kind.ANIMATED, null,
            "768/FULL/UI/LEVELOFTHEDAY/LOTD_PRESENTS_SUPER/LOTD_PRESENTS_SUPER.PAM",
            "enter");

    public enum Kind {
        TEXT,
        EMOJI,
        ANIMATED
    }

    public final String display;
    public final Kind kind;
    public final String imageId;
    public final String pamPath;
    public final String pamClip;

    QuickMessageId(String display, Kind kind, String imageId, String pamPath, String pamClip) {
        this.display = display;
        this.kind = kind;
        this.imageId = imageId;
        this.pamPath = pamPath;
        this.pamClip = pamClip;
    }

    public static List<QuickMessageId> catalog() {
        return Collections.unmodifiableList(Arrays.asList(values()));
    }

    public static QuickMessageId fromName(String name) {
        if (name == null) {
            return null;
        }
        try {
            return QuickMessageId.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
