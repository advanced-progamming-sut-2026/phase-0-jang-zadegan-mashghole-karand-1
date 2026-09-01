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
    private float mapRightX = 1280f;
    private ConveyorTrayAnimator conveyorAnimator;
    private float hudTopReserve;
    private int sunAmount;
    private int rightSunAmount;
    private float worldWidth;
    private String selectedPlantName;
    private int selectedConveyorIndex = -1;
    private boolean plantFoodMode;
    private boolean shovelMode;
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
                conveyorAnimator, hudTopReserve, 1280f);
    }

    public void bind(ControllerManager controller, AssetContext assets,
            HudViewState hud, int leftSun, int rightSun, float worldWidth,
            ChapterWorldHeight worldHeightProvider, ConveyorTrayAnimator conveyorAnimator,
            float hudTopReserve) {
        bind(controller, assets, hud, leftSun, rightSun, worldWidth, worldHeightProvider,
                conveyorAnimator, hudTopReserve, worldWidth);
    }

    public void bind(ControllerManager controller, AssetContext assets,
            HudViewState hud, int leftSun, int rightSun, float worldWidth,
            ChapterWorldHeight worldHeightProvider, ConveyorTrayAnimator conveyorAnimator,
            float hudTopReserve, float mapRightX) {
        this.controller = controller;
        this.assets = assets;
        this.hud = hud;
        this.sunAmount = leftSun;
        this.rightSunAmount = rightSun;
        this.worldWidth = worldWidth;
        this.worldHeightProvider = worldHeightProvider;
        this.conveyorAnimator = conveyorAnimator;
        this.hudTopReserve = hudTopReserve;
        this.mapRightX = mapRightX;
        if (hud != null && !hud.showPlantFood && plantFoodMode) {
            clearPlantFoodMode();
        }
        if (plantFoodMode && plantFoodAmount() <= 0) {
            clearPlantFoodMode();
            clearShovelMode();
        }
        if (hud != null && !HudOverlayRenderer.shouldShowShovel(hud) && shovelMode) {
            clearShovelMode();
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

    public boolean isShovelMode() {
        return shovelMode;
    }

    public void clearPlantFoodMode() {
        plantFoodMode = false;
    }

    public void clearShovelMode() {
        shovelMode = false;
    }

    public boolean hoverShovelTargetValid() {
        if (!shovelMode || !hoverCellValid) {
            return false;
        }
        ReadOnlyGameState state = gameState();
        return state != null && state.getPlantAt(hoverRow, hoverCol) != null;
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
        clearShovelMode();
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

    private void enterShovelMode() {
        clearSelection();
        clearPlantFoodMode();
        shovelMode = true;
    }

    private void toggleShovelMode() {
        if (shovelMode) {
            clearShovelMode();
            return;
        }
        enterShovelMode();
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
                        || selectedZombie != null || plantFoodMode || shovelMode)) {
            clearSelection();
            clearPlantFoodMode();
            clearShovelMode();
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
                    clearShovelMode();
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
        if (handleRightClick(button)) {
            return true;
        }
        if (button != Input.Buttons.LEFT) {
            return false;
        }

        float worldX = pointerWorldX;
        float worldY = pointerWorldY;
        float worldH = worldHeightProvider.worldHeight();

        if (handlePlantFoodButton(worldX, worldY, worldH)) {
            return true;
        }
        if (handleShovelButton(worldX, worldY, worldH)) {
            return true;
        }
        if (handleTrayTouch(worldX, worldY, worldH)) {
            return true;
        }
        if (handleSunCollection(worldX, worldY)) {
            return true;
        }
        return handleCellTouch(worldX, worldY);
    }

    private boolean handleRightClick(int button) {
        if (button != Input.Buttons.RIGHT) {
            return false;
        }
        if (selectedPlantName != null || selectedConveyorIndex >= 0 || plantFoodMode || shovelMode) {
            clearSelection();
            clearPlantFoodMode();
            clearShovelMode();
            clearShovelMode();
            return true;
        }
        return false;
    }

    private boolean handlePlantFoodButton(float worldX, float worldY, float worldH) {
        if (!hud.showPlantFood || hudOverlay == null) {
            return false;
        }
        if (!hudOverlay.hitTestPlantFoodButton(assets, worldX, worldY, worldWidth, worldH)) {
            return false;
        }
        togglePlantFoodMode();
        return true;
    }

    private boolean handleShovelButton(float worldX, float worldY, float worldH) {
        if (hud == null || hudOverlay == null || !HudOverlayRenderer.shouldShowShovel(hud)) {
            return false;
        }
        if (!hudOverlay.hitTestShovelButton(assets, worldX, worldY, mapRightX, worldH)) {
            return false;
        }
        toggleShovelMode();
        return true;
    }

    private boolean handleTrayTouch(float worldX, float worldY, float worldH) {
        if (hud.trayIsConveyorRow) {
            return handleConveyorTrayTouch(worldX, worldY, worldH);
        }
        return handleSeedTrayTouch(worldX, worldY, worldH);
    }

    private boolean handleConveyorTrayTouch(float worldX, float worldY, float worldH) {
        ConveyorTrayHit hit = seedTray.hitTestConveyor(hud, assets, worldX, worldY, worldH, sunAmount,
                true, conveyorAnimator, hudTopReserve);
        if (!hit.isHit()) {
            return false;
        }
        if (hit.isSlot()) {
            clearPlantFoodMode();
            clearShovelMode();
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

    private boolean handleSeedTrayTouch(float worldX, float worldY, float worldH) {
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
            clearShovelMode();
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
        return seedTray.hitTest(hud, assets, worldX, worldY, worldH,
                sunAmount, rightSunAmount, worldWidth, false, conveyorAnimator, hudTopReserve) != null;
    }

    private boolean handleSunCollection(float worldX, float worldY) {
        GameMechanismController game = controller.getGameMechanismController();
        float modelX = lawnLayout.modelX(worldX);
        float modelY = lawnLayout.modelY(worldY);
        float hitRadius = ReadOnlyGameState.CELL_WIDTH * 0.45f;
        CommandResult sunResult = game.collectSunAtPosition(modelX, modelY, hitRadius);
        if (!sunResult.isSuccess()) {
            return false;
        }
        controller.handleCommandResult(sunResult);
        return true;
    }

    private boolean handleCellTouch(float worldX, float worldY) {
        if (!lawnLayout.worldToCell(worldX, worldY, cell)) {
            return false;
        }
        int row = cell[0];
        int col = cell[1];
        GameMechanismController game = controller.getGameMechanismController();

        if (plantFoodMode) {
            return handlePlantFoodFeed(game, row, col);
        }
        if (shovelMode) {
            return handleShovelPluck(game, row, col);
        }
        if (hud.mode == HudViewState.Mode.VASE_BREAKER) {
            if (handleVaseBreakerCell(game, row, col)) {
                return true;
            }
        }
        if (hud.trayIsConveyorRow) {
            return handleConveyorPlacement(game, row, col);
        }
        return handlePlantOrZombiePlacement(game, row, col);
    }

    private boolean handlePlantFoodFeed(GameMechanismController game, int row, int col) {
        CommandResult feedResult = game.feedPlant(row, col);
        controller.handleCommandResult(feedResult);
        if (feedResult != null && feedResult.isSuccess()) {
            clearPlantFoodMode();
            clearShovelMode();
        }
        return true;
    }

    private boolean handleShovelPluck(GameMechanismController game, int row, int col) {
        CommandResult pluckResult = game.pluckPlant(row, col);
        controller.handleCommandResult(pluckResult);
        if (pluckResult != null && pluckResult.isSuccess()) {
            clearShovelMode();
        }
        return true;
    }

    private boolean handleVaseBreakerCell(GameMechanismController game, int row, int col) {
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
        return false;
    }

    private boolean handleConveyorPlacement(GameMechanismController game, int row, int col) {
        if (selectedConveyorIndex < 0) {
            return false;
        }
        CommandResult result = game.placeConveyorPlant(row, col, selectedConveyorIndex);
        controller.handleCommandResult(result);
        if (result.isSuccess()) {
            clearSelection();
        }
        return true;
    }

    private boolean handlePlantOrZombiePlacement(GameMechanismController game, int row, int col) {
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
            CommandResult result = game.plantPlant(row, col, plantType);
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
