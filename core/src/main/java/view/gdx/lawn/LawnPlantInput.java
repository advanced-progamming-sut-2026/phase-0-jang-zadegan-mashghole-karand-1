package view.gdx.lawn;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import controller.GameMechanismController;
import model.data.content.minigame.IZombieShop;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.rule.SessionConfig;
import model.service.HudViewState;
import shared.izombie.IZombiePlayMode;
import shared.izombie.MatchRole;
import view.MenuType;
import view.gdx.AssetContext;

public final class LawnPlantInput extends InputAdapter {
    private final Viewport worldViewport;
    private final LawnLayout lawnLayout;
    private final SeedTrayRenderer seedTray;
    private final Vector3 touch = new Vector3();
    private final int[] cell = new int[2];

    private ControllerManager controller;
    private AssetContext assets;
    private HudViewState hud;
    private ChapterWorldHeight worldHeightProvider;
    private int sunAmount;
    private String selectedPlantName;

    private ZombieType selectedZombie;
    private int zombieCursorRow = 2;
    private int zombieCursorCol = 6;

    @FunctionalInterface
    public interface ChapterWorldHeight {
        float worldHeight();
    }

    public LawnPlantInput(Viewport worldViewport, LawnLayout lawnLayout, SeedTrayRenderer seedTray) {
        this.worldViewport = worldViewport;
        this.lawnLayout = lawnLayout;
        this.seedTray = seedTray;
    }

    public void bind(ControllerManager controller, AssetContext assets,
            HudViewState hud, int sunAmount, ChapterWorldHeight worldHeightProvider) {
        this.controller = controller;
        this.assets = assets;
        this.hud = hud;
        this.sunAmount = sunAmount;
        this.worldHeightProvider = worldHeightProvider;
        if (selectedPlantName != null && !isStillSelectable(selectedPlantName)) {
            clearSelection();
        }
    }

    public String selectedPlantName() {
        return selectedPlantName;
    }

    public void clearSelection() {
        selectedPlantName = null;
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
        return false;
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
        if (keycode == Input.Keys.ESCAPE && selectedPlantName != null) {
            clearSelection();
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
                    return true;
                }
            }
            if (keycode == Input.Keys.UP) {
                zombieCursorRow = Math.min(4, zombieCursorRow + 1);
                return true;
            }
            if (keycode == Input.Keys.DOWN) {
                zombieCursorRow = Math.max(0, zombieCursorRow - 1);
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
                placeZombie(zombieCursorRow, zombieCursorCol, selectedZombie);
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
        if (button == Input.Buttons.RIGHT) {
            if (selectedPlantName != null) {
                clearSelection();
                return true;
            }
            return false;
        }
        if (button != Input.Buttons.LEFT) {
            return false;
        }

        touch.set(screenX, screenY, 0f);
        worldViewport.unproject(touch);
        float worldX = touch.x;
        float worldY = touch.y;
        float worldH = worldHeightProvider.worldHeight();

        String packet = seedTray.hitTest(hud, assets, worldX, worldY, worldH, sunAmount);
        if (packet != null) {
            if (packet.equals(selectedPlantName)) {
                clearSelection();
            } else {
                selectedPlantName = packet;
            }
            return true;
        }

        if (seedTray.hitTest(hud, assets, worldX, worldY, worldH, sunAmount, false) != null) {
            return true;
        }

        if (lawnLayout.worldToCell(worldX, worldY, cell)) {
            int row = cell[0];
            int col = cell[1];
            GameMechanismController game = controller.getGameMechanismController();
            CommandResult result;

            if (hud.trayIsConveyorRow) {
                result = game.placeConveyorPlant(row, col);
                controller.handleCommandResult(result);
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
                if (isOnline() && result != null && result.isSuccess()) {
                    controller.getNetworkSession().socket().placePlant(plantType.name(), row, col);
                }
                controller.handleCommandResult(result);
                return true;
            }
        }
        return false;
    }

    private void placeZombie(int row, int col, ZombieType type) {
        GameMechanismController game = controller.getGameMechanismController();
        CommandResult result = game.placeZombie(row, col, type);
        if (isOnline() && result != null && result.isSuccess()) {
            controller.getNetworkSession().socket().placeZombie(type.name(), row, col);
        }
        controller.handleCommandResult(result);
    }
}
