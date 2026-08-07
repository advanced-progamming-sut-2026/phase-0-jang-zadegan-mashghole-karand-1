package model.data.plant.stuns;

import model.data.zombie.Zombie;

public class CatStun extends PlantStun{
    private final int wizardInstanceId;
    public CatStun(Zombie wizard) {
        this.wizardInstanceId = wizard.instanceId;
    }
    public int getWizardInstanceId() {
        return wizardInstanceId;
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
    public boolean canBeDamaged() {
        return false;
    }
    @Override
    public boolean canBeEaten() {
        return false;
    }

    public boolean isWizardDead(java.util.List<Zombie> zombies) {
        return zombies.stream().noneMatch(z ->
                z.instanceId == wizardInstanceId && z.isAlive);
    }
    @Override
    public StunKind getKind() {
        return StunKind.CAT;
    }
}
