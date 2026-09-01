package controller;

import model.ModelManager;
import model.core.GameLoop;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.brain.Brain;
import model.data.plant.Plant;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.seed.PlantSeedDrop;
import model.data.vase.Vase;
import model.data.vase.VaseContentType;
import model.data.zombie.Zombie;
import model.data.zombie.armor.config.ZombieArmorType;
import model.rule.rules.specialLevel.PlantWhatYouGetRules;
import model.storage.user.User;

final class GameMechanismControllerSupport {
    private GameMechanismControllerSupport() {
    }

    static String formatZombieInfo(Zombie z) {
        StringBuilder sb = new StringBuilder();
        sb.append(z.type.name).append(":\n");
        sb.append("position: ").append(z.row).append(", ").append(z.col).append('\n');
        sb.append("health: ").append(z.hp).append('\n');

        sb.append("armor:");
        if (z.armor != null && z.armor.isIntact()) {
            sb.append('\n');
            sb.append("     ").append(armorLabel(z.armor.type))
                    .append(": ").append(z.armor.currentHealth);
        } else {
            sb.append(" none");
        }
        sb.append('\n');

        sb.append("effects:");
        boolean any = false;
        if (z.frozenTicks > 0 || z.isFrozen) {
            int ticks = Math.max(z.frozenTicks, z.isFrozen ? 1 : 0);
            int seconds = (int) Math.ceil(ticks / (double) GameLoop.TICKS_PER_SECOND);
            sb.append("\n     frozen: ").append(seconds).append('s');
            any = true;
        }
        if (z.isIced()) {
            sb.append("\n     iced: ").append(z.getIceHP()).append(" hp");
            any = true;
        }
        if (z.stunned && z.stunTicks > 0) {
            int seconds = (int) Math.ceil(z.stunTicks / (double) GameLoop.TICKS_PER_SECOND);
            sb.append("\n     stunned: ").append(seconds).append('s');
            any = true;
        }
        if (z.isHypnotized) {
            sb.append("\n     hypnotized");
            any = true;
        }
        if (z.isEating) {
            sb.append("\n     eating");
            any = true;
        }
        if (!any) {
            sb.append(" none");
        }
        return sb.toString();
    }

    static String armorLabel(ZombieArmorType type) {
        return switch (type) {
            case CONE -> "cone";
            case BUCKET -> "bucket";
            case BRICK -> "brick";
            case NEWSPAPER -> "newspaper";
            case KNIGHT_ARMOR -> "knightArmor";
            case SARCOPHAGUS -> "sarcophagus";
        };
    }

    static String describeBrokenVase(Vase vase, int row, int col) {
        String location = "(" + row + ", " + col + ")";
        if (vase.contentType == VaseContentType.EMPTY) {
            return "Broke vase at " + location + ". It was empty.";
        }
        if (vase.contentType == VaseContentType.PLANT_SEED) {
            String plantName = vase.plantType != null ? vase.plantType.name : "unknown";
            return "Broke vase at " + location + ". A " + plantName + " seed dropped.";
        }
        if (vase.contentType == VaseContentType.ZOMBIE) {
            String zombieName = vase.zombieType != null ? vase.zombieType.name : "zombie";
            return "Broke vase at " + location + ". A " + zombieName + " appeared!";
        }
        return "Broke vase at " + location + ".";
    }

    static String describeTileStatus(GameState gameState, int row, int col) {
        Plant plant = gameState.getPlantAt(row, col);
        if (plant != null) {
            return "Plant: " + plant.type.name + " HP " + plant.hp + "/" + plant.totalHP + ".";
        }

        if (gameState.brainsMode && col == 0) {
            Brain brain = gameState.getBrainAtRow(row);
            if (brain != null) {
                return "Tile (" + row + ", " + col + "): brain "
                        + (brain.isCollected() ? "collected" : "available") + ".";
            }
        }

        Vase vase = gameState.getVaseAt(row, col);
        if (vase != null) {
            return "Tile (" + row + ", " + col + "): vase (" + vase.vaseType.name().toLowerCase() + ").";
        }

        PlantSeedDrop seed = gameState.getSeedDropAt(row, col);
        if (seed != null) {
            int remaining = Math.max(0, PlantSeedDrop.TTL_TICKS - seed.age);
            return "Tile (" + row + ", " + col + "): " + seed.plantType.name
                    + " seed (" + remaining + " ticks left).";
        }

        int cellStartX = col * ReadOnlyGameState.CELL_WIDTH;
        int cellEndX = (col + 1) * ReadOnlyGameState.CELL_WIDTH;
        long zombiesInCell = gameState.zombies.stream()
                .filter(z -> z.row == row && z.position.x >= cellStartX && z.position.x < cellEndX)
                .count();
        boolean hasSun = gameState.sunDrops.stream()
                .anyMatch(s -> s.row == row && !s.isFalling
                        && s.position.x >= cellStartX && s.position.x < cellEndX);

        if (zombiesInCell > 0 || hasSun) {
            return "Tile (" + row + ", " + col + "): zombies=" + zombiesInCell + ", sun=" + hasSun + ".";
        }
        return "Tile (" + row + ", " + col + ") is empty.";
    }

    static String validatePlantPlacement(ModelManager model, GameState gameState, User user,
            PlantType plantType, int row, int col) {
        if (model.getPlayContext() != null && model.getPlayContext().isConveyorMode()) {
            return "Conveyor Belt mode: use plant conveyor -l (row,col) instead.";
        }
        if (plantType == null) {
            return "Plant type not found.";
        }
        if (row < 0 || row >= ReadOnlyGameState.GRID_ROWS
                || col < 0 || col >= ReadOnlyGameState.GRID_COLS) {
            return "Invalid cell (" + row + ", " + col + ").";
        }
        int level = user != null ? user.getPlantLevel(plantType) : PlantStats.DEFAULT_LEVEL;
        PlantStats stats = PlantStats.forLevel(plantType, level);
        boolean usesSun = model.getRuleEngine().usesSunCurrency();
        boolean hasHeldSeed = model.getPlayContext() != null
                && model.getPlayContext().hasHeldSeed(plantType);
        if (usesSun && !hasHeldSeed && gameState.sunAmount < stats.cost) {
            return "Not enough sun. Need " + stats.cost + ", have " + gameState.sunAmount + ".";
        }
        if (!usesSun && !hasHeldSeed) {
            return "You need to collect a " + plantType.name + " seed before planting.";
        }
        if (model.getPlayContext() != null
                && model.getPlayContext().isPlantOnCooldown(plantType)
                && !hasHeldSeed) {
            int sec = (int) Math.ceil(
                    model.getPlayContext().getPlantingCooldownTicks(plantType)
                            / (double) GameLoop.TICKS_PER_SECOND);
            return plantType.name + " is recharging (" + sec + "s left).";
        }
        return null;
    }

    static boolean isPlantWhatYouGetPlantingLocked(ModelManager model) {
        return model.getRuleEngine().getActiveRules().stream()
                .filter(PlantWhatYouGetRules.class::isInstance)
                .map(PlantWhatYouGetRules.class::cast)
                .anyMatch(PlantWhatYouGetRules::hasWavesStarted);
    }
}
