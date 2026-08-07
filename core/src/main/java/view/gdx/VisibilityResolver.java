package view.gdx;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import model.data.zombie.Zombie;
import model.data.zombie.armor.runtime.ZombieArmor;
import view.gdx.catalog.ArmorVisualRecipe;
import view.gdx.catalog.ZombieVisualDef;

public final class VisibilityResolver {
    public Map<String, Boolean> forZombie(Zombie zombie, ZombieVisualDef visual) {
        if (visual == null || visual.armor == null) {
            return Collections.emptyMap();
        }
        ZombieArmor armor = zombie.armor;
        if (armor == null || !armor.isIntact()) {
            return Collections.emptyMap();
        }

        ArmorVisualRecipe recipe = visual.armor;
        Map<String, Boolean> map = new HashMap<>();
        if (recipe.groupPart != null) {
            map.put(recipe.groupPart, true);
        }

        String stagePart = pickStage(recipe, armor);
        if (stagePart != null) {
            map.put(stagePart, true);
        }
        return map;
    }

    private static String pickStage(ArmorVisualRecipe recipe, ZombieArmor armor) {
        float ratio = armor.type.hp <= 0 ? 0f : (float) armor.currentHealth / armor.type.hp;
        if (ratio > 0.66f) {
            return recipe.intactPart;
        }
        if (ratio > 0.33f) {
            return recipe.midDamagePart != null ? recipe.midDamagePart : recipe.intactPart;
        }
        return recipe.lowDamagePart != null ? recipe.lowDamagePart : recipe.intactPart;
    }
}
