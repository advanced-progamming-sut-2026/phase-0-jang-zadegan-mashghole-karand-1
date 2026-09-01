package view.gdx.catalog;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ZombieVisualDef {
    public final String pamPath;
    public final String idleClip;
    public final String walkClip;
    public final String eatClip;
    public final String dieClip;
    public final ArmorVisualRecipe armor;
    public final ArmVisualRecipe arm;
    public final List<CompanionVisual>  companions;

    public ZombieVisualDef(String pamPath, String idleClip, String walkClip, String eatClip, String dieClip,
            ArmorVisualRecipe armor,  List<CompanionVisual> companions) {
        this(pamPath, idleClip, walkClip, eatClip, dieClip, armor, null, companions);
    }

    public ZombieVisualDef(String pamPath, String idleClip, String walkClip, String eatClip, String dieClip,
            ArmorVisualRecipe armor, ArmVisualRecipe arm, List<CompanionVisual> companions) {
        this.pamPath = pamPath;
        this.idleClip = idleClip;
        this.walkClip = walkClip;
        this.eatClip = eatClip;
        this.dieClip = dieClip;
        this.armor = armor;
        this.arm = arm;
        this.companions = companions;
    }

    public static ZombieVisualDef plain(String pamPath, String idle, String walk, String eat, String die) {
        return new ZombieVisualDef(pamPath, idle, walk, eat, die, null, null, Collections.emptyList());
    }

    public ZombieVisualDef withArm(ArmVisualRecipe armRecipe) {
        return new ZombieVisualDef(pamPath, idleClip, walkClip, eatClip, dieClip, armor, armRecipe, companions);
    }
}
