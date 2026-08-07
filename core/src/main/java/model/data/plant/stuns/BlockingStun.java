package model.data.plant.stuns;

import model.data.plant.Plant;
import model.data.projectile.LobbedProjectile;
import model.data.projectile.Projectile;

public class BlockingStun extends PlantStun{
    private final StunKind kind;
    public BlockingStun(StunKind kind) {
        this.kind = kind;
    }

    @Override
    public boolean canAttack() {
        return false;
    }
    @Override
    public boolean canUseAbilities() {
        return false;
    }

    @Override
    public StunKind getKind() {
        return kind;
    }

    @Override
    public boolean blocksProjectile(Projectile projectile) {
        if (projectile instanceof LobbedProjectile) {
            return false;
        }

        return true;
    }
    @Override
    public void onHitByAlly(Plant plant, int damage) {
        plant.clearStun();
    }
}
