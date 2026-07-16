package model.data.projectile;

import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.ProjectileType;
import model.data.plant.abilities.effects.HitEffect;
import model.data.zombie.Zombie;

public class HomingProjectile extends Projectile{
    Zombie targetZombie;
    public HomingProjectile(int damage, Position position, int row, int col, float speed, ProjectileType type,
                            ProjectileTarget target, Plant owner, Zombie targetZombie ) {
        super(damage, position, row, col, speed, type, target, owner);
        this.targetZombie = targetZombie;
    }
    public void updateMovement(){
        if (targetZombie==null || !targetZombie.isAlive){
            this.position.x += speed;
            return;
        }
        float diffX = targetZombie.position.x - this.position.x;
        float diffY = targetZombie.position.y - this.position.y;
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > 0) {
            this.position.x += (diffX / distance) * speed;
            this.position.y += (diffY / distance) * speed;
        }
    }
}
