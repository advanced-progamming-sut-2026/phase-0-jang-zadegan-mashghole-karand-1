package model.systems;

import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.Barrel.Barrel;
import model.data.Grave.Grave;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.FreezeEffect;
import model.data.plant.abilities.effects.HypnotizeEffect;
import model.data.plant.abilities.runtime.PlantDefenderAbility;
import model.data.plant.stuns.BlockingStun;
import model.data.plant.stuns.CatStun;
import model.data.plant.stuns.StunKind;
import model.data.projectile.*;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CombatSystem {
    public EventBus eventBus;

    public CombatSystem(EventBus bus) {
        this.eventBus = bus;
    }

    public void update(GameState state, EventBus eventBus, boolean freezeProjectilesEnabled) {
        Iterator<Projectile> projIter = state.projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();

            if (p.target == ProjectileTarget.ZOMBIE) {
                if (handleZombieTargetedProjectile(state, eventBus, freezeProjectilesEnabled, projIter, p)) {
                    continue;
                }
            } else if (p.target == ProjectileTarget.PLANT) {
                if (p instanceof LobbedProjectile lob && !lob.hasLanded()) {
                    continue;
                }
                boolean removed = handlePlantTargetedProjectile(state, eventBus, projIter, p);
                if (!removed && p instanceof LobbedProjectile lob && lob.hasLanded()) {
                    projIter.remove();
                }
            }
        }

        updateZombieAttacks(state, eventBus);

        state.removeDeadZombies();
        state.removeDeadPlants();
    }

    private boolean handleZombieTargetedProjectile(GameState state, EventBus eventBus,
            boolean freezeProjectilesEnabled, Iterator<Projectile> projIter, Projectile p) {
        if (p instanceof LobbedProjectile lob && !lob.hasLanded()) {
            return false;
        }
        if (handleZombossProjectileHit(state, eventBus, freezeProjectilesEnabled, projIter, p)) {
            return true;
        }
        if (handleZombieProjectileObstacles(state, eventBus, projIter, p)) {
            return true;
        }
        boolean hitZombie = handleZombieProjectileZombieCollision(state, eventBus, freezeProjectilesEnabled,
                projIter, p);
        if (!hitZombie && p instanceof LobbedProjectile lob && lob.hasLanded()) {
            projIter.remove();
            return true;
        }
        return hitZombie;
    }

    private boolean handleZombossProjectileHit(GameState state, EventBus eventBus,
            boolean freezeProjectilesEnabled, Iterator<Projectile> projIter, Projectile p) {
        for (Zombie z : state.zombies) {
            if (z.type == null || !z.type.isZomboss() || !z.isHitByProjectile(p)) {
                continue;
            }
            if (p instanceof PiercingProjectile piercing && piercing.hitZombies.contains(z)) {
                continue;
            }
            if (p instanceof BouncingProjectile bouncing && bouncing.hitZombies.contains(z)) {
                continue;
            }
            if (p instanceof PiercingProjectile piercing) {
                piercing.hitZombies.add(z);
            }
            if (p instanceof BouncingProjectile bouncing) {
                bouncing.hitZombies.add(z);
            }
            applyZombieProjectileHit(state, eventBus, freezeProjectilesEnabled, projIter, p, null, z);
            return true;
        }
        return false;
    }

    private boolean handleZombieProjectileObstacles(GameState state, EventBus eventBus,
            Iterator<Projectile> projIter, Projectile p) {
        if (p.type != ProjectileType.SPIKE) {
            Grave graveAhead = state.graves.stream()
                    .filter(g -> g.row == p.row && g.col >= p.col)
                    .min(Comparator.comparingInt(g -> g.col))
                    .orElse(null);
            if (graveAhead != null) {
                if (Math.abs(graveAhead.pos.x - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
                    graveAhead.takeDamage(p.damage, state, eventBus);
                    projIter.remove();
                    return true;
                }
            }
        }
        Plant blocker = state.plants.stream()
                .filter(plant -> plant.blocksProjectile(p) && plant.row == p.row && plant.col > p.col)
                .min(Comparator.comparingInt(plant -> plant.col))
                .orElse(null);
        if (blocker != null
                && Math.abs(blocker.getX() - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
            blocker.receiveAllyHit(p.damage);
            projIter.remove();
            return true;
        }

        Barrel barrelAhead = state.barrels.stream().filter(barrel -> barrel.row == p.row && barrel.col >= p.col)
                .min(Comparator.comparingInt(barrel -> barrel.col)).orElse(null);

        if (barrelAhead != null) {
            if (Math.abs(barrelAhead.pos.x - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
                barrelAhead.takeDamage(p.damage, state, eventBus);
                projIter.remove();
                return true;
            }
        }
        Plant frostbiteFrozenPlantAhead = state.plants.stream()
                .filter(plant -> plant.isFrostbiteFreezeActive() && plant.row == p.row && plant.col > p.col)
                .min(Comparator.comparingInt(plant -> plant.col)).orElse(null);
        if (frostbiteFrozenPlantAhead != null) {
            if (p.type == ProjectileType.FIRE || p.type == ProjectileType.BLUE_FIRE) {
                frostbiteFrozenPlantAhead.removeFrostbiteFreeze();
            } else {
                frostbiteFrozenPlantAhead.damageFrostbiteFreeze(p.damage);
            }
            projIter.remove();
            return true;
        }
        return false;
    }

    private boolean handleZombieProjectileZombieCollision(GameState state, EventBus eventBus,
            boolean freezeProjectilesEnabled, Iterator<Projectile> projIter, Projectile p) {
        Iterator<Zombie> zombieIter = state.zombies.iterator();
        while (zombieIter.hasNext()) {
            Zombie z = zombieIter.next();

            if (z.isHitByProjectile(p)) {
                boolean blocked = z.abilities.stream().anyMatch(a -> a.blocksProjectiles(z, p));
                if (blocked) {
                    projIter.remove();
                    return true;
                }

                boolean pass = z.abilities.stream().anyMatch(a -> a.passProjectiles(z, p));
                if (pass) {
                    continue;
                }
                if (p instanceof PiercingProjectile piercingProjectile) {
                    if (piercingProjectile.hitZombies.contains(z)) {
                        continue;
                    }
                    piercingProjectile.hitZombies.add(z);
                }
                if (p instanceof BouncingProjectile bouncingProjectile) {
                    if (bouncingProjectile.hitZombies.contains(z)) {
                        continue;
                    }
                    bouncingProjectile.hitZombies.add(z);
                }

                applyZombieProjectileHit(state, eventBus, freezeProjectilesEnabled, projIter, p, zombieIter, z);
                return true;
            }
        }
        return false;
    }

    private void applyZombieProjectileHit(GameState state, EventBus eventBus, boolean freezeProjectilesEnabled,
            Iterator<Projectile> projIter, Projectile p, Iterator<Zombie> zombieIter, Zombie z) {
        z.abilities.forEach(a -> a.onProjectileHit(z, p));

        if (p.sourcePlant == PlantType.Caulipower) {
            new HypnotizeEffect().apply(z, state, eventBus, p.sourcePlant);
            projIter.remove();
            return;
        }

        applyProjectileEffects(state, p, z, freezeProjectilesEnabled);
        if (p instanceof LobbedProjectile lob) {
            applyLobButter(lob, z);
        }
        if (z.isIced()) {
            z.damageIce(p.damage);
        } else if (p.type != ProjectileType.POISON) {
            new DamageEffect(p.damage).apply(z, state, eventBus, p.sourcePlant);
        }
        if (p instanceof LobbedProjectile lob) {
            applyLobSplash(state, eventBus, freezeProjectilesEnabled, lob, z);
        }
        removeOrContinueProjectile(projIter, p);
        handleZombieDeathAfterHit(state, zombieIter, p, z);
    }

    private void removeOrContinueProjectile(Iterator<Projectile> projIter, Projectile p) {
        if (p instanceof PiercingProjectile piercingProjectile) {
            if (piercingProjectile.pierceCount > 0) {
                piercingProjectile.pierceCount--;
            }
            if (piercingProjectile.pierceCount == 0) {
                projIter.remove();
            }
            return;
        }
        if (p instanceof BouncingProjectile bouncing) {
            if (bouncing.canBounce()) {
                bouncing.incrementBounceCount();
                boolean up = Math.random() < 0.5;
                int newRow = bouncing.row + (up ? -1 : 1);
                if (newRow < 0 || newRow >= GameState.GRID_ROWS) {
                    newRow = bouncing.row + (up ? 1 : -1);
                }
                bouncing.row = newRow;
                bouncing.position.y = newRow * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f;
                bouncing.setDirection(up ? Direction.UP_RIGHT : Direction.DOWN_RIGHT);
            } else {
                projIter.remove();
            }
            return;
        }
        projIter.remove();
    }

    private void handleZombieDeathAfterHit(GameState state, Iterator<Zombie> zombieIter, Projectile p, Zombie z) {
        if (z != null && !z.isAlive) {
            z.lastHitBy = p.sourcePlant;
            z.kill(state);
            if (zombieIter != null) {
                zombieIter.remove();
            }
        }
    }

    private void applyLobButter(LobbedProjectile lob, Zombie z) {
        if (lob.butterChance <= 0f) {
            return;
        }
        if (lob.type == ProjectileType.BUTTER) {
            return;
        }
        if (Math.random() < lob.butterChance) {
            z.stunned = true;
            z.stunTicks = 30;
        }
    }

    private void applyLobSplash(GameState state, EventBus eventBus, boolean freezeProjectilesEnabled,
            LobbedProjectile lob, Zombie primary) {
        if (lob.aoeRadius <= 0) {
            return;
        }
        int splashDamage = lob.aoeDamage > 0 ? lob.aoeDamage : Math.max(1, lob.damage / 2);
        int primaryCol = (int) (primary.position.x / GameState.CELL_WIDTH);
        for (Zombie other : List.copyOf(state.zombies)) {
            if (!other.isAlive || other == primary) {
                continue;
            }
            int otherCol = (int) (other.position.x / GameState.CELL_WIDTH);
            if (Math.abs(other.row - primary.row) > lob.aoeRadius) {
                continue;
            }
            if (Math.abs(otherCol - primaryCol) > lob.aoeRadius) {
                continue;
            }
            if (other.isIced()) {
                other.damageIce(splashDamage);
            } else {
                new DamageEffect(splashDamage).apply(other, state, eventBus, lob.sourcePlant);
            }
            if (lob.type == ProjectileType.ICE_MELON && freezeProjectilesEnabled) {
                new FreezeEffect(30).apply(other, state, eventBus, lob.sourcePlant);
            }
            if (!other.isAlive) {
                other.lastHitBy = lob.sourcePlant;
                other.kill(state);
            }
        }
    }

    private boolean handlePlantTargetedProjectile(GameState state, EventBus eventBus,
            Iterator<Projectile> projIter, Projectile p) {
        Plant target = findPlantAt(state, p.row, p.position.x);
        if (target != null && Math.abs(target.getX() - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
            target.hp -= p.damage;
            projIter.remove();
            if (p.type == ProjectileType.OCTOPUS) {
                target.applyStun(new BlockingStun(StunKind.OCTOPUS));
            } else if (p.type == ProjectileType.ICE) {
                target.applyStun(new BlockingStun(StunKind.FROZEN));
            }
            if (target.hp <= 0) {
                target.kill(state, eventBus);
            }
            return true;
        }
        return false;
    }

    private void updateZombieAttacks(GameState state, EventBus eventBus) {
        Iterator<Zombie> zombieIter = state.zombies.iterator();
        while (zombieIter.hasNext()) {
            Zombie z = zombieIter.next();
            if (!z.isAlive || z.type.isZomboss() || z.stunned) {
                continue;
            }
            if (z.isHypnotized) {
                attackHypnotizedTarget(state, z);
                continue;
            }
            if (attackPlantTarget(state, eventBus, z)) {
                break;
            }
        }
    }

    private void attackHypnotizedTarget(GameState state, Zombie z) {
        Zombie targetZombie = state.zombies.stream()
                .filter(zombie -> zombie.isAlive && zombie.row == z.row && zombie.position.x >= z.position.x
                        && !zombie.isHypnotized
                        && Math.abs(zombie.position.x - z.position.x) < ReadOnlyGameState.ZOMBIE_ATTACK_RANGE)
                .min(Comparator.comparingDouble(zombie -> zombie.position.x - z.position.x)).orElse(null);
        if (targetZombie == null) {
            return;
        }
        targetZombie.takeDamage((int) (z.getDPS() / 10));
        z.isEating = true;
        if (!targetZombie.isAlive) {
            targetZombie.kill(state);
        }
    }

    private boolean attackPlantTarget(GameState state, EventBus eventBus, Zombie z) {
        Plant targetPlant = findPlantAt(state, z.row, z.position.x);
        if (targetPlant == null) {
            return false;
        }
        if (z.type == ZombieType.WIZARD_ZOMBIE) {
            targetPlant.applyStun(new CatStun(z));
            return false;
        }
        if (!targetPlant.canBeEaten() || !targetPlant.canBeDamaged()) {
            return false;
        }
        targetPlant.hp -= (int) z.getDPS() / 10;
        for (PlantAbilityConfig a : targetPlant.abilities) {
            if (a instanceof PlantDefenderAbility def) {
                def.onDamaged(targetPlant, z, state, eventBus);
            }
        }
        z.isEating = true;
        if (targetPlant.hp <= 0) {
            targetPlant.kill(state, eventBus, z);
            z.isEating = false;
        }
        return true;
    }

    private Plant findPlantAt(GameState state, int row, float x) {
        int col = (int) (x / GameState.CELL_WIDTH);
        return state.getPlantAt(row, col);
    }

    private void applyProjectileEffects(GameState state, Projectile projectile, Zombie zombie,
            boolean freezeProjectilesEnabled) {
        switch (projectile.type) {
            case FIRE, BLUE_FIRE:
                if (zombie.isIced())
                    zombie.removeIce();
                break;
            case ICE, ICE_MELON:
                if (freezeProjectilesEnabled) {
                    new FreezeEffect(30).apply(zombie, state, eventBus, projectile.sourcePlant);
                }
                break;
            case POISON:
                if (zombie.isIced())
                    return;
                if (projectile.sourcePlant != null) {
                    zombie.lastHitBy = projectile.sourcePlant;
                }
                zombie.takeDamage(projectile.damage, true);
                break;
            case BUTTER:
                zombie.stunned = true;
                zombie.stunTicks = 30;
                break;
            case FREEZE_LINE:
                if (!freezeProjectilesEnabled)
                    break;
                for (Zombie z : state.zombies) {
                    if (z.occupiesRow(projectile.row)) {
                        new FreezeEffect(30).apply(z, state, eventBus, projectile.sourcePlant);
                    }
                }
                break;
        }
    }
}