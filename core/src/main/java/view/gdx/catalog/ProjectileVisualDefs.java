package view.gdx.catalog;

import model.data.projectile.ProjectileType;

import java.util.HashMap;
import java.util.Map;

public final class ProjectileVisualDefs {
    public static Map<ProjectileType, ProjectileVisualDef> create() {
        Map<ProjectileType, ProjectileVisualDef> projectiles = new HashMap<>();
        putPeaProjectiles(projectiles);
        putPultProjectiles(projectiles);
        putSpecialProjectiles(projectiles);
        putBowlingProjectiles(projectiles);
        return projectiles;
    }

    private static void putPeaProjectiles(Map<ProjectileType, ProjectileVisualDef> projectiles) {
        projectiles.put(ProjectileType.PEA,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_PEA_PROJECTILE/T_PEA_PROJECTILE.PAM", "animation"));
        projectiles.put(ProjectileType.FIRE,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_FIRE_PEA/T_FIRE_PEA.PAM", "animation"));
        projectiles.put(ProjectileType.BLUE_FIRE,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_FIRE_PEA_BLUE/T_FIRE_PEA_BLUE.PAM", "animation"));
        projectiles.put(ProjectileType.ICE,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_SNOW_PEA/T_SNOW_PEA.PAM", "animation"));
        projectiles.put(ProjectileType.BUTTER,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/BUTTERCUP_BUTTER/BUTTERCUP_BUTTER.PAM", "animation"));
        projectiles.put(ProjectileType.FUME,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/FUMESHROOM_BUBBLES/FUMESHROOM_BUBBLES.PAM", "special"));
        projectiles.put(ProjectileType.POISON,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/GOOPEASHOOTER_PROJECTILES/GOOPEASHOOTER_PROJECTILES.PAM",
                        "projectile_t1"));
        projectiles.put(ProjectileType.STAR,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_STARFRUIT_PROJECTILE/T_STARFRUIT_PROJECTILE.PAM",
                        "animation"));
        projectiles.put(ProjectileType.SPIKE,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_CACTUS_PROJECTILE/T_CACTUS_PROJECTILE.PAM", "idle2"));
        projectiles.put(ProjectileType.FREEZE_LINE,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/SNOWPEA_PLANTFOOD/SNOWPEA_PLANTFOOD.PAM", "plantfood_on"));
    }

    private static void putPultProjectiles(Map<ProjectileType, ProjectileVisualDef> projectiles) {
        projectiles.put(ProjectileType.CABBAGE,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_CABBAGEPULT_PROJECTILE/T_CABBAGEPULT_PROJECTILE.PAM",
                        "animation"));
        projectiles.put(ProjectileType.ICE_MELON,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/T_WINTERMELON_PROJECTILE/T_WINTERMELON_PROJECTILE.PAM",
                        "animation"));
        projectiles.put(ProjectileType.KERNEL,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_KERNALPULT_PROJECTILE/T_KERNALPULT_PROJECTILE.PAM",
                        "animation"));
        projectiles.put(ProjectileType.MELON,
                new ProjectileVisualDef(
                        "768/INITIAL/EFFECTS/T_MELON_PROJECTILE/T_MELON_PROJECTILE.PAM", "animation"));
        projectiles.put(ProjectileType.PEPPER,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/T_PEPPERPULT_PROJECTILE/T_PEPPERPULT_PROJECTILE.PAM",
                        "animation"));
    }

    private static void putSpecialProjectiles(Map<ProjectileType, ProjectileVisualDef> projectiles) {
        projectiles.put(ProjectileType.OCTOPUS,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/ZOMBIE_OCTOPUS_PROJECTILE/ZOMBIE_OCTOPUS_PROJECTILE.PAM",
                        "animation"));
        projectiles.put(ProjectileType.ROTO_SEED,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/T_ROTORUTABAGA_PROJECTILE1/T_ROTORUTABAGA_PROJECTILE1.PAM",
                        "animation2"));
        projectiles.put(ProjectileType.PLASMA,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/T_CITRON_CITRUS_ORB/T_CITRON_CITRUS_ORB.PAM",
                        "Citron_Citrus_Orb"));
        projectiles.put(ProjectileType.LASER,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/LASERBEAN_LASER/LASERBEAN_LASER.PAM", "animation"));
    }

    private static void putBowlingProjectiles(Map<ProjectileType, ProjectileVisualDef> projectiles) {
        projectiles.put(ProjectileType.BOUNCING_AQUA,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE1/BOWLINGBULB_PROJECTILE1.PAM",
                        "animation"));
        projectiles.put(ProjectileType.BOUNCING_BLUE,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE2/BOWLINGBULB_PROJECTILE2.PAM",
                        "animation"));
        projectiles.put(ProjectileType.BOUNCING_ORANGE,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE3/BOWLINGBULB_PROJECTILE3.PAM",
                        "animation"));
        projectiles.put(ProjectileType.SUPER_BULB,
                new ProjectileVisualDef(
                        "768/FULL/EFFECTS/BOWLINGBULB_PLANTFOOD_PROJECTILE/"
                                + "BOWLINGBULB_PLANTFOOD_PROJECTILE.PAM",
                        "animation"));
    }
}
