package shared.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum QuickMessageId {
    MSG_GG("gg", Kind.TEXT),
    MSG_HELLO("hello", Kind.TEXT),
    MSG_AFK("afk", Kind.TEXT),
    EMOJI_LAUGH("😂", Kind.EMOJI),
    EMOJI_CRY("😢", Kind.EMOJI),
    EMOJI_SUNGLASSES("😎", Kind.EMOJI),
    ANIM_LAUGH("anim_laugh", Kind.ANIMATED),
    ANIM_CRY("anim_cry", Kind.ANIMATED),
    ANIM_SUNGLASSES("anim_sunglasses", Kind.ANIMATED);

    public enum Kind {
        TEXT,
        EMOJI,
        ANIMATED
    }

    public final String display;
    public final Kind kind;

    QuickMessageId(String display, Kind kind) {
        this.display = display;
        this.kind = kind;
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
