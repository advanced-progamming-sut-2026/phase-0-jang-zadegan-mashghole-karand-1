package view.gdx.ui.screens.map;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.ControllerManager;
import controller.PickPlantsController;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.plant.PlantUpgradeCosts;
import model.service.GameNavigationState;
import model.storage.user.User;
import view.gdx.AssetContext;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.PlantVisualDef;
import view.gdx.catalog.VisualCatalog;
import view.gdx.lawn.SeedPacketCardView;
import view.gdx.lawn.SeedPacketDefs;
import view.gdx.ui.UiSkin;
import view.gdx.ui.widgets.PamPreviewActor;
import view.gdx.ui.widgets.SeedPacketCardActor;
import view.gdx.ui.widgets.UiWidgets;

final class PlantSelectionPanel {
    private static final float LOADOUT_PACKET_H = 52f;
    private static final float GRID_PACKET_H = 80f;
    private static final int GRID_COLUMNS = 5;
    private static final float FALLBACK_PACKET_ASPECT = 119f / 75f;

    private static final String DETAIL_BG = "IMAGE_UI_ALMANAC_ALMANAC_STAT_BACKGROUND";
    private static final String START_UP = "IMAGE_UI_GENERIC_GREENBUTTON";
    private static final String START_DOWN = "IMAGE_UI_GENERIC_GREENBUTTON_DOWN";
    private static final String COIN_ICON = "IMAGE_UI_THYMED_EVENTS_ECS_CONVRT_COIN";
    private static final String GEM_ICON = "IMAGE_EFFECTS_COIN_DIAMOND_COIN_DIAMOND_141X146";

    private static final VisualCatalog CATALOG = new DefaultVisualCatalog();

    private PlantSelectionPanel() {
    }

    static void build(Table root, ControllerManager controller, AssetContext assets,
            GameNavigationState nav, PlantType focus, Consumer<PlantType> focusSetter) {
        root.clearChildren();
        root.pad(8f);

        User user = controller.getStorage().getCurrentUser();
        ChapterType chapter = nav.selectedChapter;
        int maxSlots = PickPlantsController.MAX_SELECTED_PLANTS;

        PlantType focused = resolveFocus(nav, focus);
        if (focusSetter != null && focus != focused) {
            focusSetter.accept(focused);
        }

        Table layout = new Table();
        layout.top().left();

        layout.add(buildDetailPanel(controller, assets, nav, user, chapter, maxSlots, focused, focusSetter))
                .growX().padBottom(8f).row();

        layout.add(buildGridSeparator()).growX().height(2f).padBottom(8f).row();

        ScrollPane grid = buildPlantGrid(controller, assets, nav, user, chapter, focused, focusSetter);
        grid.setForceScroll(false, true);
        layout.add(grid).grow().width(920f).height(360f).padBottom(8f).row();

        layout.add(buildFooter(controller, assets, nav, maxSlots)).growX().row();

        root.add(layout).grow();
    }

    private static Image buildGridSeparator() {
        Image line = new Image();
        line.setColor(0.35f, 0.45f, 0.55f, 0.8f);
        return line;
    }

    private static PlantType resolveFocus(GameNavigationState nav, PlantType focus) {
        if (focus != null) {
            return focus;
        }
        if (!nav.selectedPlants.isEmpty()) {
            return nav.selectedPlants.get(nav.selectedPlants.size() - 1);
        }
        List<PlantType> available = availablePlants(nav);
        return available.isEmpty() ? null : available.get(0);
    }

    private static List<PlantType> availablePlants(GameNavigationState nav) {
        List<PlantType> plants = new ArrayList<>();
        for (PlantType plant : nav.unlockedPlants) {
            if (plant == PlantType.Imitater || plant.isBowlingExclusive()) {
                continue;
            }
            plants.add(plant);
        }
        plants.sort(Comparator.comparing(p -> p.name));
        return plants;
    }

    private static Table buildDetailPanel(ControllerManager controller, AssetContext assets,
            GameNavigationState nav, User user, ChapterType chapter, int maxSlots,
            PlantType focused, Consumer<PlantType> focusSetter) {
        Table detail = new Table();
        TextureRegion detailBg = assets != null ? assets.region(DETAIL_BG) : null;
        if (detailBg != null) {
            detail.setBackground(new TextureRegionDrawable(detailBg));
        }
        detail.pad(12f);

        Table body = new Table();
        body.add(buildPreviewPane(assets, focused, nav)).width(200f).padRight(16f);

        Table info = new Table();
        info.top().left();
        if (focused != null) {
            int level = user != null ? user.getPlantLevel(focused) : PlantStats.DEFAULT_LEVEL;
            PlantStats stats = PlantStats.forLevel(focused, level);
            boolean inLoadout = nav.selectedPlants.contains(focused);
            boolean boosted = nav.boostedPlants.contains(focused);
            boolean storedBoost = user != null && user.storedBoosts.contains(focused);

            info.add(UiWidgets.title(focused.name)).left().padBottom(4f).row();
            info.add(statusLine(inLoadout, boosted, storedBoost)).left().padBottom(6f).row();

            var description = UiWidgets.body(describePlant(focused, stats));
            description.setWrap(true);
            info.add(description).width(420f).left().padBottom(8f).row();
            info.add(UiWidgets.body("Family: " + focused.category.name().replace('_', ' ')
                    + "  |  HP: " + stats.hp + "  |  Sun: " + stats.cost))
                    .left().padBottom(10f).row();

            Table actions = new Table();
            if (level < PlantStats.MAX_LEVEL && !focused.isBowlingExclusive()) {
                int coinCost = PlantUpgradeCosts.coinCostToReach(level + 1);
                actions.add(icon(assets, COIN_ICON, 28f)).padRight(4f);
                TextButton upgrade = UiWidgets.secondary("UPGRADE " + coinCost);
                upgrade.setDisabled(true);
                actions.add(upgrade).height(40f).padRight(12f);
            }
            if (inLoadout && !boosted) {
                actions.add(icon(assets, GEM_ICON, 28f)).padRight(4f);
                TextButton boost = UiWidgets.primary(storedBoost ? "BOOST (stored)" : "BOOST 2");
                UiWidgets.onChange(boost, () -> UiWidgets.apply(controller,
                        controller.getPickPlantsController().boostPlant(focused)));
                actions.add(boost).height(40f);
            }
            info.add(actions).left().row();
        } else {
            info.add(UiWidgets.body("Select a plant from the grid below.")).left();
        }
        body.add(info).growX();
        detail.add(body).left().padBottom(10f).row();

        detail.add(UiWidgets.body("Selected loadout:")).left().padBottom(6f).row();
        detail.add(buildLoadoutRow(controller, assets, nav, user, chapter, maxSlots, focused, focusSetter))
                .left();

        return detail;
    }

    private static Label statusLine(boolean inLoadout, boolean boosted, boolean storedBoost) {
        String status;
        if (inLoadout && boosted) {
            status = "Status: SELECTED  |  BOOSTED (golden packet)";
        } else if (inLoadout) {
            status = "Status: SELECTED for this level";
        } else if (boosted) {
            status = "Status: BOOSTED (add to loadout to use)";
        } else if (storedBoost) {
            status = "Status: Stored boost available in Greenhouse";
        } else {
            status = "Status: Not in loadout";
        }
        Label label = UiWidgets.body(status);
        if (boosted) {
            label.setColor(1f, 0.85f, 0.2f, 1f);
        } else if (inLoadout) {
            label.setColor(0.4f, 1f, 0.5f, 1f);
        } else if (storedBoost) {
            label.setColor(0.95f, 0.75f, 0.25f, 1f);
        }
        return label;
    }

    private static Table buildPreviewPane(AssetContext assets, PlantType focused, GameNavigationState nav) {
        Table preview = new Table();
        if (focused != null) {
            boolean boosted = nav.boostedPlants.contains(focused);
            Stack stack = new Stack();
            PlantVisualDef visual = CATALOG.plant(focused);
            if (visual != null && assets != null) {
                PamPreviewActor pam = new PamPreviewActor(assets, visual.pamPath, visual.idleClip, 0.85f);
                pam.setSize(180f, 180f);
                stack.add(pam);
            }
            preview.add(stack).size(180f, 180f).row();
            if (boosted) {
                Label ready = UiWidgets.body("Ready!");
                ready.setColor(1f, 0.9f, 0.3f, 1f);
                preview.add(ready).padTop(4f).row();
            }
        }
        return preview;
    }

    private static Table buildLoadoutRow(ControllerManager controller, AssetContext assets,
            GameNavigationState nav, User user, ChapterType chapter, int maxSlots,
            PlantType focused, Consumer<PlantType> focusSetter) {
        Table row = new Table();
        List<PlantType> selected = new ArrayList<>(nav.selectedPlants);
        for (int i = 0; i < maxSlots; i++) {
            float slotW = packetWidth(assets, chapter, LOADOUT_PACKET_H);
            if (i < selected.size()) {
                PlantType plant = selected.get(i);
                boolean isFocus = plant == focused;
                boolean boosted = nav.boostedPlants.contains(plant);
                SeedPacketCardActor actor = new SeedPacketCardActor(assets, chapter, LOADOUT_PACKET_H);
                actor.setCard(cardForPlant(plant, user, nav, true, true, false, isFocus, false));
                PlantType slotPlant = plant;
                actor.setClickAction(() -> {
                    if (focusSetter != null) {
                        focusSetter.accept(slotPlant);
                    }
                    controller.refreshView();
                });
                row.add(wrapCard(actor, true, boosted, isFocus, LOADOUT_PACKET_H))
                        .size(slotW, LOADOUT_PACKET_H).pad(3f);
            } else {
                SeedPacketCardActor empty = new SeedPacketCardActor(assets, chapter, LOADOUT_PACKET_H);
                empty.setCard(SeedPacketCardView.empty());
                row.add(wrapCard(empty, false, false, false, LOADOUT_PACKET_H))
                        .size(slotW, LOADOUT_PACKET_H).pad(3f);
            }
        }
        return row;
    }

    private static ScrollPane buildPlantGrid(ControllerManager controller, AssetContext assets,
            GameNavigationState nav, User user, ChapterType chapter, PlantType focused,
            Consumer<PlantType> focusSetter) {
        Table grid = new Table();
        grid.top().left();
        grid.add(UiWidgets.body("Available plants (tap to add/remove):")).left().colspan(GRID_COLUMNS)
                .padBottom(6f).row();

        int col = 0;
        for (PlantType plant : availablePlants(nav)) {
            boolean inLoadout = nav.selectedPlants.contains(plant);
            boolean boosted = nav.boostedPlants.contains(plant);
            boolean isFocus = plant == focused;
            float slotW = packetWidth(assets, chapter, GRID_PACKET_H);

            SeedPacketCardActor actor = new SeedPacketCardActor(assets, chapter, GRID_PACKET_H);
            actor.setCard(cardForPlant(plant, user, nav, inLoadout, true, false, isFocus, true));
            actor.setClickAction(() -> onGridClick(controller, plant, focusSetter));
            grid.add(wrapCard(actor, inLoadout, boosted, isFocus, GRID_PACKET_H))
                    .size(slotW, GRID_PACKET_H).pad(4f);
            col++;
            if (col >= GRID_COLUMNS) {
                col = 0;
                grid.row();
            }
        }
        if (col != 0) {
            grid.row();
        }
        ScrollPane scroll = new ScrollPane(grid, UiSkin.get());
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        return scroll;
    }

    private static Stack wrapCard(SeedPacketCardActor actor, boolean inLoadout, boolean boosted,
            boolean focused, float packetHeight) {
        float packetWidth = packetWidth(actor, packetHeight);
        actor.setSize(packetWidth, packetHeight);

        Stack stack = new Stack();
        stack.add(actor);

        Table badges = new Table();
        badges.top().left();
        if (inLoadout) {
            Label selected = UiWidgets.body("SELECTED");
            selected.setFontScale(0.65f);
            selected.setColor(0.3f, 1f, 0.4f, 1f);
            badges.add(selected).pad(2f);
        }
        badges.row().expandY();
        badges.bottom().right();
        if (boosted) {
            Label boost = UiWidgets.body("BOOST");
            boost.setFontScale(0.7f);
            boost.setColor(1f, 0.85f, 0.15f, 1f);
            badges.add(boost).pad(2f);
        }
        stack.add(badges);
        badges.setTouchable(Touchable.disabled);

        stack.setSize(packetWidth, packetHeight);
        return stack;
    }

    private static float packetWidth(SeedPacketCardActor actor, float packetHeight) {
        return actor.getPrefWidth() > 0f ? actor.getPrefWidth() : packetHeight * FALLBACK_PACKET_ASPECT;
    }

    private static float packetWidth(AssetContext assets, ChapterType chapter, float packetHeight) {
        if (assets == null) {
            return packetHeight * FALLBACK_PACKET_ASPECT;
        }
        TextureRegion frame = assets.region(SeedPacketDefs.worldBack(chapter));
        if (frame == null) {
            frame = assets.region(SeedPacketDefs.EMPTY);
        }
        if (frame == null || frame.getRegionHeight() <= 0) {
            return packetHeight * FALLBACK_PACKET_ASPECT;
        }
        return packetHeight * (frame.getRegionWidth() / (float) frame.getRegionHeight());
    }

    private static void onGridClick(ControllerManager controller, PlantType plant,
            Consumer<PlantType> focusSetter) {
        if (focusSetter != null) {
            focusSetter.accept(plant);
        }
        GameNavigationState nav = controller.getGameNavigation();
        if (nav.selectedPlants.contains(plant)) {
            UiWidgets.apply(controller, controller.getPickPlantsController().removePlant(plant));
        } else {
            UiWidgets.apply(controller, controller.getPickPlantsController().addPlant(plant, null));
        }
    }

    private static Table buildFooter(ControllerManager controller, AssetContext assets,
            GameNavigationState nav, int maxSlots) {
        Table footer = new Table();

        Label summary = UiWidgets.body("Selected: " + nav.selectedPlants.size() + "/" + maxSlots);
        if (!nav.boostedPlants.isEmpty()) {
            String boostedNames = nav.boostedPlants.stream()
                    .map(p -> p.name)
                    .collect(Collectors.joining(", "));
            summary.setText(summary.getText() + "   Boosted: " + boostedNames);
            summary.setColor(1f, 0.9f, 0.35f, 1f);
        }
        footer.add(summary).expandX().fillX().left().align(Align.left);

        footer.add(buildStartButton(controller, assets)).width(220f).height(52f).right();
        return footer;
    }

    private static SeedPacketCardView cardForPlant(PlantType plant, User user, GameNavigationState nav,
            boolean inLoadout, boolean showCost, boolean locked, boolean focused, boolean showFamilyIcon) {
        int level = user != null ? user.getPlantLevel(plant) : PlantStats.DEFAULT_LEVEL;
        PlantStats stats = PlantStats.forLevel(plant, level);
        boolean boosted = nav.boostedPlants.contains(plant);
        boolean storedBoost = user != null && user.storedBoosts.contains(plant);
        return new SeedPacketCardView(
                plant.name,
                stats.cost,
                level,
                showCost,
                true,
                0f,
                focused,
                inLoadout,
                boosted,
                storedBoost,
                true,
                locked,
                showFamilyIcon,
                false);
    }

    private static String describePlant(PlantType plant, PlantStats stats) {
        return switch (plant.category) {
            case SUN_PRODUCER -> "Produces sun to fuel your defenses.";
            case SHOOTER -> "Shoots projectiles at zombies.";
            case LOBBER -> "Lobs projectiles over obstacles.";
            case EXPLOSIVE -> "Explodes to damage nearby zombies.";
            case MELEE -> "Attacks zombies up close.";
            case DEFENDER -> "Blocks zombies with tough defenses.";
            case MODIFIER -> "Modifies other plants or zombies.";
            case STRIKE_THROUGH -> "Attacks through multiple targets.";
            case HOMING -> "Homes in on priority targets.";
            case MINT -> "Empowers plants of its family.";
        };
    }

    private static Image icon(AssetContext assets, String id, float size) {
        TextureRegion region = assets != null ? assets.region(id) : null;
        Image image = new Image();
        if (region != null) {
            image.setDrawable(new TextureRegionDrawable(region));
        }
        image.setSize(size, size);
        return image;
    }

    private static TextButton buildStartButton(ControllerManager controller, AssetContext assets) {
        TextButton start = UiWidgets.primary("Let's Rock!");
        styleStartButton(start, assets);
        UiWidgets.onChange(start, () -> UiWidgets.apply(controller,
                controller.getPickPlantsController().startGame()));
        return start;
    }

    private static void styleStartButton(TextButton button, AssetContext assets) {
        if (assets == null) {
            return;
        }
        TextureRegion up = assets.region(START_UP);
        TextureRegion down = assets.region(START_DOWN);
        if (up == null) {
            return;
        }
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(button.getStyle());
        style.up = new TextureRegionDrawable(up);
        if (down != null) {
            style.down = new TextureRegionDrawable(down);
        }
        button.setStyle(style);
        button.getLabel().setAlignment(Align.center);
    }
}
