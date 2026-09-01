package view.gdx.lawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import controller.GameMechanismController;
import model.core.ReadOnlyGameState;
import model.data.content.minigame.IZombieShop;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.rule.SessionConfig;
import model.service.HudViewState;
import shared.izombie.IZombiePlayMode;
import shared.izombie.MatchRole;
import view.MenuType;
import view.gdx.AssetContext;
import view.gdx.ui.HudOverlayRenderer;

public final class LawnPlantInput extends InputAdapter {
    private final Viewport worldViewport;
    private final LawnLayout lawnLayout;
    private final SeedTrayRenderer seedTray;
    private final HudOverlayRenderer hudOverlay;
    private final Vector3 touch = new Vector3();
    private final int[] cell = new int[2];

    private ControllerManager controller;
    private AssetContext assets;
    private HudViewState hud;
    private ChapterWorldHeight worldHeightProvider;
    private ConveyorTrayAnimator conveyorAnimator;
    private float hudTopReserve;
    private int sunAmount;
    private int rightSunAmount;
    private float worldWidth;
    private String selectedPlantName;
    private int selectedConveyorIndex = -1;
    private boolean plantFoodMode;
    private boolean hoverCellValid;
    private int hoverRow;
    private int hoverCol;
    private boolean pointerWorldValid;
    private float pointerWorldX;
    private float pointerWorldY;

    private ZombieType selectedZombie;
    private int zombieCursorRow = 2;
    private int zombieCursorCol = 6;

    @FunctionalInterface
    public interface ChapterWorldHeight {
        float worldHeight();
    }

    public LawnPlantInput(Viewport worldViewport, LawnLayout lawnLayout, SeedTrayRenderer seedTray,
            HudOverlayRenderer hudOverlay) {
        this.worldViewport = worldViewport;
        this.lawnLayout = lawnLayout;
        this.seedTray = seedTray;
        this.hudOverlay = hudOverlay;
    }

    public void bind(ControllerManager controller, AssetContext assets,
            HudViewState hud, int sunAmount, ChapterWorldHeight worldHeightProvider,
            ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        bind(controller, assets, hud, sunAmount, sunAmount, 1280f, worldHeightProvider,
                conveyorAnimator, hudTopReserve);
    }

    public void bind(ControllerManager controller, AssetContext assets,
            HudViewState hud, int leftSun, int rightSun, float worldWidth,
            ChapterWorldHeight worldHeightProvider, ConveyorTrayAnimator conveyorAnimator,
            float hudTopReserve) {
        this.controller = controller;
        this.assets = assets;
        this.hud = hud;
        this.sunAmount = leftSun;
        this.rightSunAmount = rightSun;
        this.worldWidth = worldWidth;
        this.worldHeightProvider = worldHeightProvider;
        this.conveyorAnimator = conveyorAnimator;
        this.hudTopReserve = hudTopReserve;
        if (hud != null && !hud.showPlantFood && plantFoodMode) {
            clearPlantFoodMode();
        }
        if (plantFoodMode && plantFoodAmount() <= 0) {
            clearPlantFoodMode();
        }
        if (hud != null && hud.trayIsConveyorRow) {
            if (selectedConveyorIndex >= 0 && !isConveyorIndexSelectable(selectedConveyorIndex)) {
                clearSelection();
            }
        } else if (selectedPlantName != null && !isStillSelectable(selectedPlantName)) {
            clearSelection();
        }
    }

    public String selectedPlantName() {
        return selectedPlantName;
    }

    public int selectedConveyorIndex() {
        return selectedConveyorIndex;
    }

    public void clearSelection() {
        selectedPlantName = null;
        selectedConveyorIndex = -1;
        selectedZombie = null;
    }

    public ZombieType selectedZombie() {
        return selectedZombie;
    }

    public int zombieCursorRow() {
        return zombieCursorRow;
    }

    public int zombieCursorCol() {
        return zombieCursorCol;
    }

    public boolean hasZombieCursor() {
        return selectedZombie != null && (isCouch()
                || (isOnline() && localRole() == MatchRole.ZOMBIES)
                || (hud != null && hud.mode == HudViewState.Mode.BRAINS && !isCouch() && !isOnline()));
    }

    public boolean isPlantFoodMode() {
        return plantFoodMode;
    }

    public void clearPlantFoodMode() {
        plantFoodMode = false;
    }

    public boolean hasHoverCell() {
        return hoverCellValid;
    }

    public int hoverRow() {
        return hoverRow;
    }

    public int hoverCol() {
        return hoverCol;
    }

    public boolean hasPointerWorld() {
        return pointerWorldValid;
    }

    public float pointerWorldX() {
        return pointerWorldX;
    }

    public float pointerWorldY() {
        return pointerWorldY;
    }

    public void clearHover() {
        hoverCellValid = false;
        pointerWorldValid = false;
    }

    public void refreshHoverFromPointer() {
        updateHoverFromScreen(Gdx.input.getX(), Gdx.input.getY());
    }

    private void updateHoverFromScreen(int screenX, int screenY) {
        touch.set(screenX, screenY, 0f);
        worldViewport.unproject(touch);
        pointerWorldValid = true;
        pointerWorldX = touch.x;
        pointerWorldY = touch.y;
        if (lawnLayout.worldToCell(touch.x, touch.y, cell)) {
            hoverCellValid = true;
            hoverRow = cell[0];
            hoverCol = cell[1];
        } else {
            hoverCellValid = false;
        }
    }

    private int plantFoodAmount() {
        if (controller == null || controller.getModel() == null || controller.getModel().getState() == null) {
            return 0;
        }
        return controller.getModel().getState().getPlantFoodAmount();
    }

    private ReadOnlyGameState gameState() {
        if (controller == null || controller.getModel() == null) {
            return null;
        }
        return controller.getModel().getState();
    }

    /** UI-only feedability preview; gameplay still goes through feedPlant. */
    public boolean hoverPlantFoodTargetValid() {
        if (!plantFoodMode || !hoverCellValid || plantFoodAmount() <= 0) {
            return false;
        }
        ReadOnlyGameState state = gameState();
        if (state == null) {
            return false;
        }
        Plant plant = state.getPlantAt(hoverRow, hoverCol);
        return plant != null
                && plant.plantFoodEffect != null
                && !plant.isPlantFoodActive
                && plant.canUseAbilities();
    }

    private void enterPlantFoodMode() {
        clearSelection();
        plantFoodMode = true;
    }

    private void togglePlantFoodMode() {
        if (plantFoodMode) {
            clearPlantFoodMode();
            return;
        }
        if (plantFoodAmount() <= 0) {
            return;
        }
        enterPlantFoodMode();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        updateHoverFromScreen(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        updateHoverFromScreen(screenX, screenY);
        return false;
    }

    private boolean isStillSelectable(String plantName) {
        if (hud == null || plantName == null) {
            return false;
        }
        for (HudViewState.TraySlot slot : hud.traySlots) {
            if (plantName.equals(slot.name)) {
                return SeedTrayRenderer.isSelectable(hud, slot, sunAmount);
            }
        }
        if (hud.rightTraySlots != null) {
            for (HudViewState.TraySlot slot : hud.rightTraySlots) {
                if (plantName.equals(slot.name)) {
                    return SeedTrayRenderer.isSelectable(hud, slot, rightSunAmount);
                }
            }
        }
        return false;
    }

    private boolean isConveyorIndexSelectable(int index) {
        if (hud == null || index < 0 || index >= hud.traySlots.size()) {
            return false;
        }
        return SeedTrayRenderer.isSelectable(hud, hud.traySlots.get(index), sunAmount);
    }

    private SessionConfig sessionConfig() {
        if (controller == null || controller.getModel() == null || controller.getModel().getPlayContext() == null) {
            return null;
        }
        return controller.getModel().getPlayContext().getConfig();
    }

    private boolean isCouch() {
        SessionConfig cfg = sessionConfig();
        return cfg != null && cfg.iZombiePlayMode == IZombiePlayMode.COUCH;
    }

    private boolean isOnline() {
        SessionConfig cfg = sessionConfig();
        return cfg != null && (cfg.iZombiePlayMode == IZombiePlayMode.ONLINE_RANDOM
                || cfg.iZombiePlayMode == IZombiePlayMode.ONLINE_INVITE);
    }

    private MatchRole localRole() {
        SessionConfig cfg = sessionConfig();
        return cfg == null ? MatchRole.ZOMBIES : cfg.localMatchRole;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE
                && (selectedPlantName != null || selectedConveyorIndex >= 0
                        || selectedZombie != null || plantFoodMode)) {
            clearSelection();
            clearPlantFoodMode();
            return true;
        }
        if (keycode == Input.Keys.M && isOnline()) {
            controller.openMenu(MenuType.QUICK_MESSAGES);
            return true;
        }

        if (isCouch() || (isOnline() && localRole() == MatchRole.ZOMBIES)
                || (hud != null && hud.mode == HudViewState.Mode.BRAINS && !isCouch() && !isOnline())) {
            ZombieType[] shop = IZombieShop.getAvailableTypes().toArray(new ZombieType[0]);
            if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_5) {
                int idx = keycode - Input.Keys.NUM_1;
                if (idx < shop.length) {
                    selectedZombie = shop[idx];
                    clearPlantFoodMode();
                    return true;
                }
            }
            if (keycode == Input.Keys.UP) {
                zombieCursorRow = Math.max(0, zombieCursorRow - 1);
                return true;
            }
            if (keycode == Input.Keys.DOWN) {
                zombieCursorRow = Math.min(4, zombieCursorRow + 1);
                return true;
            }
            if (keycode == Input.Keys.LEFT) {
                zombieCursorCol = Math.max(6, zombieCursorCol - 1);
                return true;
            }
            if (keycode == Input.Keys.RIGHT) {
                zombieCursorCol = Math.min(8, zombieCursorCol + 1);
                return true;
            }
            if ((keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) && selectedZombie != null) {
                placeZombie(zombieCursorRow, zombieCursorCol, selectedZombie, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (controller == null || hud == null || worldHeightProvider == null) {
            return false;
        }
        updateHoverFromScreen(screenX, screenY);
        if (button == Input.Buttons.RIGHT) {
            if (selectedPlantName != null || selectedConveyorIndex >= 0 || plantFoodMode) {
                clearSelection();
                clearPlantFoodMode();
                return true;
            }
            return false;
        }
        if (button != Input.Buttons.LEFT) {
            return false;
        }

        float worldX = pointerWorldX;
        float worldY = pointerWorldY;
        float worldH = worldHeightProvider.worldHeight();

        if (hud.showPlantFood && hudOverlay != null
                && hudOverlay.hitTestPlantFoodButton(assets, worldX, worldY, worldWidth, worldH)) {
            togglePlantFoodMode();
            return true;
        }

        if (hud.trayIsConveyorRow) {
            ConveyorTrayHit hit = seedTray.hitTestConveyor(hud, assets, worldX, worldY, worldH, sunAmount,
                    true, conveyorAnimator, hudTopReserve);
            if (hit.isHit()) {
                if (hit.isSlot()) {
                    clearPlantFoodMode();
                    int index = hit.slotIndex();
                    if (index == selectedConveyorIndex) {
                        clearSelection();
                    } else {
                        selectedConveyorIndex = index;
                        selectedPlantName = hud.traySlots.get(index).name;
                    }
                }
                return true;
            }
        } else {
            String packet = seedTray.hitTest(hud, assets, worldX, worldY, worldH,
                    sunAmount, rightSunAmount, worldWidth, true, conveyorAnimator, hudTopReserve);
            if (packet != null) {
                if (packet.isEmpty()) {
                    return true;
                }
                if (isCouch() && isRightTrayPacket(packet)) {
                    return true;
                }
                clearPlantFoodMode();
                if (packet.equals(selectedPlantName)) {
                    clearSelection();
                } else {
                    selectedPlantName = packet;
                    ZombieType zt = ZombieType.fromName(packet);
                    if (zt != null && IZombieShop.isPurchasable(zt) && !isCouch()) {
                        selectedZombie = zt;
                    }
                }
                return true;
            }

            if (seedTray.hitTest(hud, assets, worldX, worldY, worldH,
                    sunAmount, rightSunAmount, worldWidth, false, conveyorAnimator, hudTopReserve) != null) {
                return true;
            }
        }

        GameMechanismController game = controller.getGameMechanismController();
        float modelX = lawnLayout.modelX(worldX);
        float modelY = lawnLayout.modelY(worldY);
        float hitRadius = ReadOnlyGameState.CELL_WIDTH * 0.45f;
        CommandResult sunResult = game.collectSunAtPosition(modelX, modelY, hitRadius);
        if (sunResult.isSuccess()) {
            controller.handleCommandResult(sunResult);
            return true;
        }

        if (lawnLayout.worldToCell(worldX, worldY, cell)) {
            int row = cell[0];
            int col = cell[1];

            if (plantFoodMode) {
                CommandResult feedResult = game.feedPlant(row, col);
                controller.handleCommandResult(feedResult);
                if (feedResult != null && feedResult.isSuccess()) {
                    clearPlantFoodMode();
                }
                return true;
            }

            if (hud.mode == HudViewState.Mode.VASE_BREAKER) {
                ReadOnlyGameState gs = gameState();
                if (gs != null && gs.getVaseAt(row, col) != null) {
                    CommandResult vaseResult = game.breakVase(row, col);
                    controller.handleCommandResult(vaseResult);
                    return true;
                }
                if (gs != null && gs.getSeedDropAt(row, col) != null) {
                    CommandResult seedResult = game.collectSeed(row, col);
                    controller.handleCommandResult(seedResult);
                    return true;
                }
            }

            CommandResult result;

            if (hud.trayIsConveyorRow) {
                if (selectedConveyorIndex < 0) {
                    return false;
                }
                result = game.placeConveyorPlant(row, col, selectedConveyorIndex);
                controller.handleCommandResult(result);
                if (result.isSuccess()) {
                    clearSelection();
                }
                return true;
            }

            if (selectedPlantName == null) {
                return false;
            }

            ZombieType zombieType = ZombieType.fromName(selectedPlantName);
            PlantType plantType = PlantType.fromName(selectedPlantName);

            if (zombieType != null && IZombieShop.isPurchasable(zombieType)) {
                if (isCouch()) {
                    return false;
                }
                if (isOnline() && localRole() != MatchRole.ZOMBIES) {
                    return false;
                }
                placeZombie(row, col, zombieType);
                return true;
            }

            if (plantType != null) {
                if (isOnline() && localRole() != MatchRole.PLANTS) {
                    return false;
                }
                result = game.plantPlant(row, col, plantType);
                if (isOnline() && result != null && result.isSuccess()
                        && controller.getNetworkSession() != null) {
                    controller.getNetworkSession().socket().placePlant(plantType.name(), row, col);
                }
                controller.handleCommandResult(result);
                if (result != null && result.isSuccess()) {
                    clearSelection();
                }
                return true;
            }
        }
        return false;
    }

    private boolean isRightTrayPacket(String packetName) {
        if (hud == null || hud.rightTraySlots == null || packetName == null) {
            return false;
        }
        for (HudViewState.TraySlot slot : hud.rightTraySlots) {
            if (packetName.equals(slot.name)) {
                return true;
            }
        }
        return false;
    }

    private void placeZombie(int row, int col, ZombieType type) {
        placeZombie(row, col, type, false);
    }

    private void placeZombie(int row, int col, ZombieType type, boolean keepZombieSelection) {
        GameMechanismController game = controller.getGameMechanismController();
        CommandResult result = game.placeZombie(row, col, type);
        if (isOnline() && result != null && result.isSuccess()
                && controller.getNetworkSession() != null) {
            controller.getNetworkSession().socket().placeZombie(type.name(), row, col);
        }
        controller.handleCommandResult(result);
        if (result != null && result.isSuccess()) {
            if (keepZombieSelection) {
                selectedPlantName = null;
                selectedConveyorIndex = -1;
            } else {
                clearSelection();
            }
        }
    }
}
