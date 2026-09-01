package view.gdx.catalog;

import java.util.List;

public final class ArmVisualRecipe {
    public final List<String> hideParts;
    public final String bonePart;
    public final String flyingPart;

    public ArmVisualRecipe(String bonePart, String flyingPart, String... hideParts) {
        this.bonePart = bonePart;
        this.flyingPart = flyingPart;
        this.hideParts = List.of(hideParts);
    }

    public static ArmVisualRecipe darkBasic() {
        return new ArmVisualRecipe(
                "zombie_arm_outer_upper_bone",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_lower",
                "zombie_arms_outer_upper",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02",
                "zombie_hand_outer_03");
    }

    public static ArmVisualRecipe egyptBasic() {
        return new ArmVisualRecipe(
                "zombie_egypt_arm_outer_upper_bone",
                "zombie_egypt_arm_outer_upper",
                "zombie_egypt_arm_outer_upper",
                "zombie_egypt_arm_outer_lower",
                "zombie_egypt_arms_outer_upper",
                "zombie_egypt_hand_outer_01",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02",
                "zombie_hand_outer_03");
    }

    public static ArmVisualRecipe gargantuar() {
        return new ArmVisualRecipe(
                null,
                "Zombie_gargantuar_outerarm_upper",
                "Zombie_gargantuar_outerarm_upper",
                "Zombie_gargantuar_outerarm_lower",
                "Zombie_gargantuar_outerarm_hand",
                "Zombie_gargantuar_outerarm_finger",
                "Zombie_gargantuar_outerarm_thumb");
    }

    public static ArmVisualRecipe newspaper() {
        return darkBasic();
    }

    public static ArmVisualRecipe allStar() {
        return new ArmVisualRecipe(
                "zombie_arm_outer_upper_bone",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_lower",
                "zombie_arms_outer_upper",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02");
    }

    public static ArmVisualRecipe ra() {
        return new ArmVisualRecipe(
                null,
                "zombie_egypt_ra_arm_outer_upper_01",
                "zombie_egypt_ra_arm_outer_upper_01",
                "zombie_egypt_ra_arm_outer_upper_02",
                "zombie_egypt_ra_arm_outer_lower",
                "zombie_egypt_ra_arms_outer_upper",
                "zombie_egypt_ra_hand_outer");
    }

    public static ArmVisualRecipe explorer() {
        return new ArmVisualRecipe(
                null,
                "zombie_expl_arm_outer_upper_01",
                "zombie_expl_arm_outer_upper_01",
                "zombie_expl_arm_outer_upper_02",
                "zombie_expl_arm_outer_lower",
                "zombie_expl_arms_outer_upper",
                "zombie_expl_hand_outer",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02");
    }

    public static ArmVisualRecipe tombRaiser() {
        return new ArmVisualRecipe(
                "zombie_egypt_tr_bone",
                "zombie_egypt_tr_arm_outer_upper_01",
                "zombie_egypt_tr_arm_outer_upper_01",
                "zombie_egypt_tr_arm_outer_upper_02",
                "zombie_egypt_tr_arm_outer_lower",
                "zombie_egypt_tr_arms_outer_upper",
                "zombie_egypt_tr_hand_outer_01",
                "zombie_egypt_tr_hand_outer_02",
                "zombie_egypt_tr_hand_outer_03");
    }

    public static ArmVisualRecipe barrel() {
        return new ArmVisualRecipe(
                null,
                "zombie_barrel_arm_outer_upper_01",
                "zombie_barrel_arm_outer_upper_01",
                "zombie_barrel_arm_outer_upper_02",
                "zombie_barrel_arm_outer_lower",
                "zombie_barrel_arms_outer_upper",
                "zombie_barrel_hand_outer_01",
                "zombie_barrel_hand_outer_02",
                "zombie_barrel_hand_outer_03");
    }

    public static ArmVisualRecipe snorkel() {
        return new ArmVisualRecipe(
                null,
                "zombie_snorkeler_arm_outer_upper_03",
                "_zombie_snorkeler_arm_outer_upper",
                "_zombie_snorkeler_arm_outer_upper02",
                "_zombie_snorkeler_arm_outer_upper2",
                "_zombie_snorkeler_arm_outer_upper3",
                "_zombie_snorkeler_arms_outer_upper",
                "_zombie_snorkeler_arms_outer_upper1",
                "zombie_snorkeler_arm_outer_lower",
                "zombie_snorkeler_arm_outer_lower1",
                "zombie_snorkeler_arm_outer_upper_03",
                "zombie_snorkeler_basic_hand_outer_01",
                "zombie_snorkeler_hand_outer_01",
                "zombie_snorkeler_hand_outer_02",
                "zombie_snorkeler_hand_outer_1");
    }

    public static ArmVisualRecipe jester() {
        return new ArmVisualRecipe(
                "zombie_arm_outer_upper_bone",
                "zombie_pros_arm_outer_upper_01",
                "zombie_pros_arm_outer_upper_01",
                "zombie_pros_arm_outer_upper_02",
                "zombie_pros_arm_outer_lower",
                "_zombie_pros_arms_outer_upper",
                "zombie_pros_hand_outer_01",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02",
                "zombie_hand_outer_03");
    }

    public static ArmVisualRecipe wizard() {
        return new ArmVisualRecipe(
                null,
                "zombie_egyptflag_arm_outer_upper_02",
                "zombie_egyptflag_arm_outer_upper_02",
                "zombie_egyptflag_arm_outer_lower",
                "zombie_egyptflag_arms_outer_upper",
                "zombie_egyptflag_hand_outer",
                "zombie_hand_outer_02");
    }

    public static ArmVisualRecipe prospector() {
        return new ArmVisualRecipe(
                null,
                "zombie_pros_arm_outer_upper_01",
                "_zombie_pros_arm_outer_upper",
                "_zombie_pros_arm_outer_upper2",
                "_zombie_pros_arms_outer_upper",
                "zombie_pros_arm_outer_upper_01",
                "zombie_pros_arm_outer_upper_02",
                "zombie_pros_arm_outer_lower",
                "zombie_pros_basic_hand_outer_01",
                "zombie_pros_hand_outer_01",
                "zombie_pros_hand_outer_02");
    }

    public static ArmVisualRecipe arcade() {
        return new ArmVisualRecipe(
                "zombie_troglobite_arm_outer_upper_bone",
                "zombie_troglobite_arm_outer_upper",
                "zombie_troglobite_arm_outer_upper",
                "zombie_troglobite_arm_outer_lower",
                "zombie_troglobite_hand_outer",
                "zombie_troglobite_hand_oute_push",
                "zombie_hand_outer_02");
    }

    public static ArmVisualRecipe parasol() {
        return new ArmVisualRecipe(
                "zombie_arm_outer_upper_bone",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_lower",
                "zombie_arms_outer_upper",
                "zombie_hand_outer_01",
                "zombie_hand_outer_01_upperlayer",
                "zombie_hand_outer_01_upperlayer2",
                "zombie_hand_outer_04",
                "zombie_hand_outer_05");
    }

    public static ArmVisualRecipe raincoat() {
        return new ArmVisualRecipe(
                null,
                "zombie_raincoat_arm_outer_upper",
                "zombie_raincoat_arm_outer_upper",
                "zombie_raincoat_arm_outer_lower",
                "zombie_raincoat_arms_outer_upper",
                "zombie_raincoat_hand_outer",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02");
    }

    public static ArmVisualRecipe hunter() {
        return new ArmVisualRecipe(
                "zombie_arm_outer_upper_bone",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_upper",
                "zombie_arm_outer_lower",
                "zombie_arms_outer_upper",
                "zombie_hand_outer_01");
    }

    public static ArmVisualRecipe troglobite() {
        return arcade();
    }

    public static ArmVisualRecipe octopus() {
        return new ArmVisualRecipe(
                "zombie_octo_arm_outer_upper_bone",
                "zombie_octo_arm_outer_upper",
                "zombie_octo_arm_outer_upper",
                "zombie_octo_arm_outer_lower",
                "zombie_octo_hand_outer",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02");
    }

    public static ArmVisualRecipe king() {
        return new ArmVisualRecipe(
                "zombie_arm_outer_upper_bone",
                "zombie_egypt_ra_arm_outer_upper_01",
                "zombie_egypt_ra_arm_outer_upper_01",
                "zombie_egypt_ra_arm_outer_upper_02",
                "zombie_arm_outer_lower",
                "zombie_arms_outer_upper",
                "zombie_hand_outer_01",
                "zombie_hand_outer_02",
                "zombie_hand_outer_03");
    }
}
