package view.gdx.lawn;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import controller.GameMechanismController;
import model.data.plant.PlantType;
import model.service.HudViewState;
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

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE && selectedPlantName != null) {
            clearSelection();
            return true;
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
            } else if (selectedPlantName != null) {
                PlantType type = PlantType.fromName(selectedPlantName);
                result = game.plantPlant(row, col, type);
            } else {
                return false;
            }
            controller.handleCommandResult(result);
            return true;
        }
        return false;
    }
}
