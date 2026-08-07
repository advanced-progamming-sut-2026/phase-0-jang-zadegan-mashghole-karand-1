package view.gdx.lawn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import pvz.libpvz.pam.ClipRef;
import view.gdx.AssetContext;
import view.gdx.catalog.PlantVisualDef;
import view.gdx.catalog.VisualCatalog;
import view.gdx.catalog.ZombieVisualDef;

public final class DemoLawnPreview {
    private final VisualCatalog catalog;
    private final LawnLayout layout;
    private float stateTime;

    public DemoLawnPreview(VisualCatalog catalog, LawnLayout layout) {
        this.catalog = catalog;
        this.layout = layout;
    }

    public void update(float deltaSeconds) {
        stateTime += deltaSeconds;
    }

    public void render(SpriteBatch batch, AssetContext assets) {
        if (assets.pamPlayer() == null) {
            return;
        }
        PlantVisualDef plant = catalog.plant(PlantType.PeaShooter);
        ZombieVisualDef zombie = catalog.zombie(ZombieType.BASIC);
        if (plant != null) {
            ClipRef idle = assets.clip(plant.pamPath, plant.idleClip);
            if (idle != null) {
                assets.pamPlayer().draw(batch, idle, stateTime, layout.cellCenterX(2), layout.cellCenterY(2), true);
            }
        }
        if (zombie != null) {
            ClipRef walk = assets.clip(zombie.pamPath, zombie.walkClip);
            if (walk != null) {
                assets.pamPlayer().draw(batch, walk, stateTime, layout.cellCenterX(6), layout.cellCenterY(2), true);
            }
        }
    }
}
