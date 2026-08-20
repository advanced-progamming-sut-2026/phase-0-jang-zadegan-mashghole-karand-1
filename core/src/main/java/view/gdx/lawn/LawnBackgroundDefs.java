package view.gdx.lawn;

import model.data.content.chapter.ChapterType;

final class LawnBackgroundDefs {
    private LawnBackgroundDefs() {
    }

    static BoardArt forChapter(ChapterType chapter) {
        if (chapter == null) {
            return frontLawn();
        }
        return switch (chapter) {
            case ANCIENT_EGYPT -> egypt();
            case FROSTBITE_CAVES -> iceAge();
            case BIG_WAVE_BEACH -> beach();
            case DARK_AGES -> dark();
        };
    }

    private static BoardArt egypt() {
        return new BoardArt(
                "IMAGE_BACKGROUNDS_EGYPT_TEXTURE_LEFT",
                "IMAGE_BACKGROUNDS_EGYPT_TEXTURE",
                "IMAGE_BACKGROUNDS_EGYPT_TEXTURE_RIGHT");
    }

    private static BoardArt iceAge() {
        return new BoardArt(
                "IMAGE_BACKGROUNDS_ICEAGE_TEXTURE_LEFT",
                "IMAGE_BACKGROUNDS_ICEAGE_TEXTURE",
                "IMAGE_BACKGROUNDS_ICEAGE_TEXTURE_RIGHT");
    }

    private static BoardArt beach() {
        return new BoardArt(
                "IMAGE_BACKGROUNDS_BEACH_TEXTURE_LEFT",
                "IMAGE_BACKGROUNDS_BEACH_TEXTURE",
                "IMAGE_BACKGROUNDS_BEACH_TEXTURE_RIGHT");
    }

    private static BoardArt dark() {
        return new BoardArt(
                "IMAGE_BACKGROUNDS_DARK_TEXTURE_LEFT",
                "IMAGE_BACKGROUNDS_DARK_TEXTURE",
                "IMAGE_BACKGROUNDS_DARK_TEXTURE_RIGHT");
    }

    private static BoardArt frontLawn() {
        return new BoardArt(
                "IMAGE_BACKGROUNDS_FRONTLAWN_TEXTURE_LEFT",
                "IMAGE_BACKGROUNDS_FRONTLAWN_TEXTURE",
                "IMAGE_BACKGROUNDS_FRONTLAWN_TEXTURE_RIGHT");
    }

    static final class BoardArt {
        final String leftId;
        final String centerId;
        final String rightId;

        BoardArt(String leftId, String centerId, String rightId) {
            this.leftId = leftId;
            this.centerId = centerId;
            this.rightId = rightId;
        }
    }
}
