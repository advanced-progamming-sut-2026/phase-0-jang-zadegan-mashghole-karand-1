package view.gdx.ui.screens.map;

import model.data.content.chapter.ChapterType;

final class WorldMapDefs {
    static final String LOCK_ICON = "IMAGE_UI_CARDS_LOCK_MEDIUM_GOLD";

    private WorldMapDefs() {
    }

    static String universeIcon(ChapterType chapter) {
        return switch (chapter) {
            case ANCIENT_EGYPT -> "IMAGE_UI_UNIVERSE_WORLDS_EGYPT";
            case FROSTBITE_CAVES -> "IMAGE_UI_UNIVERSE_WORLDS_ICEAGE";
            case BIG_WAVE_BEACH -> "IMAGE_UI_UNIVERSE_WORLDS_BEACH";
            case DARK_AGES -> "IMAGE_UI_UNIVERSE_WORLDS_DARK";
        };
    }

    static IslandArt[] levelIslands(ChapterType chapter) {
        return switch (chapter) {
            case ANCIENT_EGYPT -> new IslandArt[] {
                    IslandArt.single("IMAGE_WORLDMAP_EGYPT_ISLAND1"),
                    IslandArt.single("IMAGE_WORLDMAP_EGYPT_ISLAND5"),
                    IslandArt.single("IMAGE_WORLDMAP_EGYPT_ISLAND4"),
                    IslandArt.single("IMAGE_WORLDMAP_EGYPT_ISLAND5"),
            };
            case FROSTBITE_CAVES -> new IslandArt[] {
                    IslandArt.single("IMAGE_WORLDMAP_ICEAGE_ANIM3_ANIM3_1307X1318"),
                    IslandArt.single("IMAGE_WORLDMAP_ICEAGE_ANIM12_ANIM12_400X500"),
                    IslandArt.single("IMAGE_WORLDMAP_ICEAGE_ANIM11_ANIM11_400X500"),
                    IslandArt.single("IMAGE_WORLDMAP_ICEAGE_ANIM10_ANIM10_400X500"),
            };
            case BIG_WAVE_BEACH -> new IslandArt[] {
                    IslandArt.layered(
                            "IMAGE_WORLDMAP_BEACH_ANIM27_ANIM27_1362X953",
                            "IMAGE_WORLDMAP_BEACH_ANIM27_ANIM27_875X481",
                            "IMAGE_WORLDMAP_BEACH_ANIM27_ANIM27_877X488"),
                    IslandArt.single("IMAGE_WORLDMAP_DANGER_NODE_BEACH_DANGER_NODE_BEACH_383X480"),
                    IslandArt.single("IMAGE_WORLDMAP_BEACH_ANIM16_ANIM16_339X318"),
                    IslandArt.single("IMAGE_WORLDMAP_BEACH_ANIM17_ANIM17_321X255"),
            };
            case DARK_AGES -> new IslandArt[] {
                    IslandArt.single("IMAGE_WORLDMAP_DARK_ANIM1_ANIM1_1201X1413"),
                    IslandArt.single("IMAGE_WORLDMAP_DANGER_NODE_DARK_DANGER_NODE_DARK_384X459"),
                    IslandArt.single("IMAGE_WORLDMAP_DARK_ANIM9_ANIM9_373X659"),
                    IslandArt.single("IMAGE_WORLDMAP_DANGER_NODE_DARK_DANGER_NODE_DARK_389X448"),
            };
        };
    }

    static String zombossIsland(ChapterType chapter) {
        return switch (chapter) {
            case ANCIENT_EGYPT -> "IMAGE_WORLDMAP_ZOMBOSS_NODE_EGYPT_ZOMBOSS_NODE_EGYPT_914X994";
            case FROSTBITE_CAVES -> "IMAGE_WORLDMAP_ZOMBOSS_NODE_ICEAGE_ZOMBOSS_NODE_ICEAGE_1055X1280";
            case BIG_WAVE_BEACH -> "IMAGE_WORLDMAP_ZOMBOSS_NODE_BEACH_ZOMBOSS_NODE_BEACH_905X1096";
            case DARK_AGES -> "IMAGE_WORLDMAP_ZOMBOSS_NODE_DARK_ZOMBOSS_NODE_DARK_905X1096";
        };
    }

    static final class IslandArt {
        final String[] layers;

        private IslandArt(String... layers) {
            this.layers = layers;
        }

        static IslandArt single(String imageId) {
            return new IslandArt(imageId);
        }

        static IslandArt layered(String... layersBottomToTop) {
            return new IslandArt(layersBottomToTop);
        }

        boolean isLayered() {
            return layers.length > 1;
        }
    }
}
