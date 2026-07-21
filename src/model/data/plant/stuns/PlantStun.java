package model.data.plant.stuns;

import model.data.plant.Plant;
import model.data.projectile.Projectile;

public abstract class PlantStun {

    public void onApply(Plant plant) {}
    public void onRemove(Plant plant) {}
    public boolean canAttack() {
        return true;
    }
    public boolean canUseAbilities() {
        return true;
    }
    public boolean canBeDamaged() {
        return true;
    }
    public boolean canBeEaten() {
        return true;
    }

    public boolean blocksProjectile(Projectile projectile) {
        return false;
    }

    public void onHitByAlly(Plant plant, int damage) {
    }

    public abstract StunKind getKind();
}
