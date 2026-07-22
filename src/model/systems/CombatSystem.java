package model.systems;

import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.Barrel.Barrel;
import model.data.Grave.Grave;
import model.data.plant.Plant;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.FreezeEffect;
import model.data.plant.stuns.BlockingStun;
import model.data.plant.stuns.CatStun;
import model.data.plant.stuns.StunKind;
import model.data.projectile.PiercingProjectile;
import model.data.projectile.Projectile;
import model.data.projectile.ProjectileTarget;
import model.data.projectile.ProjectileType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.events.BarrelCreatedEvent;
import model.events.PlantDiedEvent;

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
                Grave graveAhead = state.graves.stream().filter(g -> g.row == p.row && g.col > p.col)
                        .min(Comparator.comparingInt(g -> g.col)).orElse(null);
                if (graveAhead != null) {
                    if (Math.abs(graveAhead.pos.x - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
                        graveAhead.takeDamage(p.damage, state, eventBus);
                        projIter.remove();
                        continue;
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
                    continue;
                }

                Barrel barrelAhead = state.barrels.stream().filter(barrel -> barrel.row == p.row && barrel.col>= p.col).
                        min(Comparator.comparingInt(barrel -> barrel.col)).orElse(null);

                if(barrelAhead != null) {
                    if(Math.abs(barrelAhead.pos.x -  p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
                        barrelAhead.takeDamage(p.damage, state, eventBus);
                        projIter.remove();
                        continue;
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
                    continue;
                }
                Iterator<Zombie> zombieIter = state.zombies.iterator();
                while (zombieIter.hasNext()) {
                    Zombie z = zombieIter.next();

                    if (z.row == p.row && Math.abs(z.position.x - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
                        boolean blocked = z.abilities.stream().anyMatch(a -> a.blocksProjectiles(z, p));
                        if (blocked) {
                            projIter.remove();
                            break;
                        }

                        boolean pass = z.abilities.stream().anyMatch(a -> a.passProjectiles(z, p));
                        if (pass) {
                            continue;
                        }
                        if(p instanceof PiercingProjectile piercingProjectile) {
                            if(piercingProjectile.hitZombies.contains(z)){
                                continue;
                            }
                            piercingProjectile.hitZombies.add(z);
                        }

                        z.abilities.forEach(a -> a.onProjectileHit(z, p));



                        applyProjectileEffects(state, p, z,freezeProjectilesEnabled);

                        if (z.isIced()) z.damageIce(p.damage);

                        else if(p.type != ProjectileType.POISON) {
                            new DamageEffect(p.damage).apply(z, state, eventBus);
                        }
                        if(p instanceof PiercingProjectile piercingProjectile) {
                            piercingProjectile.pierceCount--;

                            if(piercingProjectile.pierceCount<=0){
                                projIter.remove();
                            }
                        }else {
                            projIter.remove();
                        }


                        if (!z.isAlive) {
                            z.onDeath(state);
                            zombieIter.remove();
                        }
                        break;
                    }
                }
            } else if (p.target == ProjectileTarget.PLANT) {
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
                        eventBus.publish(new PlantDiedEvent(target));
                    }
                }
            }
        }

        Iterator<Zombie> zombieIter = state.zombies.iterator();
        while (zombieIter.hasNext()) {
            Zombie z = zombieIter.next();
            if (z.stunned) continue;
            if(z.isHypnotized) {
                Zombie targetZombie = state.zombies.stream()
                        .filter(zombie -> zombie.row == z.row && zombie.position.x>= z.position.x
                                && !zombie.isHypnotized && Math.abs(zombie.position.x - z.position.x) < ReadOnlyGameState.ZOMBIE_ATTACK_RANGE)
                        .min(Comparator.comparingDouble(zombie -> zombie.position.x - z.position.x)).orElse(null);
                if (targetZombie == null) continue;
                targetZombie.takeDamage((int)(z.getDPS() / 10));
                z.isEating = true;
                if(!targetZombie.isAlive) {
                    targetZombie.onDeath(state);
                }
                continue;
            }
            Plant targetPlant = findPlantAt(state, z.row, z.position.x);
            if (targetPlant != null) {
                if(z.type== ZombieType.WIZARD_ZOMBIE){
                    targetPlant.applyStun(new CatStun(z)); //wizard don't eat plants
                    continue;
                }
                if (!targetPlant.canBeEaten() || !targetPlant.canBeDamaged()) {
                    continue;
                }
                targetPlant.hp -=(int) z.getDPS() / 10;
                z.isEating = true;
                if (targetPlant.hp <= 0) {
                    state.plants.remove(targetPlant);
                    z.isEating = false;
                    eventBus.publish(new PlantDiedEvent(targetPlant));
                }
                break;
            }
        }

        // events should be triggered here!
        state.zombies.removeIf(z -> z.hp <= 0);

        state.plants.removeIf(p -> p.hp <= 0);
    }

    private Plant findPlantAt(GameState state, int row, float x) {
        int col = (int) (x / GameState.CELL_WIDTH);
        return state.getPlantAt(row, col);
    }

    private void applyProjectileEffects(GameState state, Projectile projectile, Zombie zombie, boolean freezeProjectilesEnabled) {
        switch (projectile.type) {
            case FIRE, BLUE_FIRE:
                if(zombie.isIced()) zombie.removeIce();
                break;
            case ICE, ICE_MELON:
                if(freezeProjectilesEnabled) {
                    new FreezeEffect(30).apply(zombie, state, eventBus);
                }
                break;
            case POISON:
                if(zombie.isIced()) return;
                zombie.takeDamage(projectile.damage, true);
                break;
            case BUTTER:
                zombie.stunned = true;
                zombie.stunTicks = 30;
                break;
            case FREEZE_LINE:
                if(!freezeProjectilesEnabled) break;
                for(Zombie z : state.zombies) {
                    if(z.row == projectile.row){
                        new FreezeEffect(30).apply(z, state, eventBus);
                    }
                }
                break;
        }
    }
}