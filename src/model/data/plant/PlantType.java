package model.data.plant;

import java.util.List;
import java.util.Set;

import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.upgrades.PlantLevelUpgrades;

public enum PlantType {
        Sunflower(PlantTypeSunDefs.sunflower()),
        TwinSunflower(PlantTypeSunDefs.twinSunflower()),
        SunShroom(PlantTypeSunDefs.sunShroom()),
        PrimalSunflower(PlantTypeSunDefs.primalSunflower()),
        GoldBloom(PlantTypeSunDefs.goldBloom()),
        PeaShooter(PlantTypeShooterDefs.peaShooter()),
        Repeater(PlantTypeShooterDefs.repeater()),
        Threepeater(PlantTypeShooterDefs.threepeater()),
        SnowPea(PlantTypeShooterDefs.snowPea()),
        Rotobaga(PlantTypeShooterDefs.rotobaga()),

        SplitPea(PlantTypeShooterDefs.splitPea()),

        Citron(PlantTypeShooterDefs.citron()),
        Caulipower(PlantTypeHomingDefs.caulipower()),
        Electric_Blueberry(PlantTypeHomingDefs.electricBlueberry()),
        BowlingBulb(PlantTypeShooterDefs.bowlingBulb()),
        Cactus(PlantTypeShooterDefs.cactus()),
        FirePeashooter(PlantTypeShooterDefs.firePeashooter()),
        Starfruit(PlantTypeShooterDefs.starfruit()),
        GooPeashooter(PlantTypeShooterDefs.gooPeashooter()),
        MegaGatlingPea(PlantTypeShooterDefs.megaGatlingPea()),
        SeaShroom(PlantTypeShooterDefs.seaShroom()),
        PuffShroom(PlantTypeShooterDefs.puffShroom()),
        Fume_shroom(PlantTypeShooterDefs.fumeShroom()),
        Cabbage_pult(PlantTypeLobberDefs.cabbagePult()),
        Kernel_pult(PlantTypeLobberDefs.kernelPult()),
        Melon_pult(PlantTypeLobberDefs.melonPult()),
        Winter_Melon(PlantTypeLobberDefs.winterMelon()),
        Pepper_pult(PlantTypeLobberDefs.pepperPult()),
        Potato_Mine(PlantTypeExplosiveDefs.potatoMine()),
        Primal_Potato_Mine(PlantTypeExplosiveDefs.primalPotatoMine()),
        Cherry_Bomb(PlantTypeExplosiveDefs.cherryBomb()),
        Squash(PlantTypeExplosiveDefs.squash()),
        Jalapeno(PlantTypeExplosiveDefs.jalapeno()),
        Doom_shroom(PlantTypeExplosiveDefs.doomShroom()),
        Tangle_Kelp(PlantTypeExplosiveDefs.tangleKelp()),
        Iceberg_Lettuce(PlantTypeExplosiveDefs.icebergLettuce()),
        Bonk_Choy(PlantTypeMeleeDefs.bonkChoy()),
        Phat_Beet(PlantTypeMeleeDefs.phatBeet()),
        Chomper(PlantTypeMeleeDefs.chomper()),
        Wasabi_Whip(PlantTypeMeleeDefs.wasabiWhip()),
        Wall_nut(PlantTypeDefenderDefs.wallNut()),
        Tall_nut(PlantTypeDefenderDefs.tallNut()),
        Endurian(PlantTypeDefenderDefs.endurian()),
        Garlic(PlantTypeDefenderDefs.garlic()),
        Sweet_Potato(PlantTypeDefenderDefs.sweetPotato()),
        Explode_o_nut(PlantTypeDefenderDefs.explodeONut()),
        Pumpkin(PlantTypeDefenderDefs.pumpkin()),
        Sun_Bean(PlantTypeDefenderDefs.sunBean()),
        Torchwood(PlantTypeModifierDefs.torchwood()),
        Magnet_shroom(PlantTypeHomingDefs.magnetShroom()),
        Hypno_shroom(PlantTypeModifierDefs.hypnoShroom()),
        Cat_tail(PlantTypeHomingDefs.catTail()),
        Imitater(PlantTypeModifierDefs.imitater()),
        Ice_shroom(PlantTypeExplosiveDefs.iceShroom()),
        Lily_Pad(PlantTypeModifierDefs.lilyPad()),
        Hot_Potato(PlantTypeExplosiveDefs.hotPotato()),
        Grave_Buster(PlantTypeExplosiveDefs.graveBuster()),
        Enlighten_mint(PlantTypeMintDefs.enlightenMint()),
        Appease_mint(PlantTypeMintDefs.appeaseMint()),
        Arma_mint(PlantTypeMintDefs.armaMint()),
        Bombard_mint(PlantTypeMintDefs.bombardMint()),
        Enforce_mint(PlantTypeMintDefs.enforceMint()),
        Reinforce_mint(PlantTypeMintDefs.reinforceMint()),
        Enchant_mint(PlantTypeMintDefs.enchantMint()),
        Pierce_mint(PlantTypeMintDefs.pierceMint()),
        catTail_mint(PlantTypeMintDefs.catTailMint()),
        Bowling_Wall_nut(PlantTypeDefenderDefs.bowlingWallNut()),
        Bowling_Explode_o_nut(PlantTypeDefenderDefs.bowlingExplodeONut()),
        Giant_Bowling_Wall_nut(PlantTypeDefenderDefs.giantBowlingWallNut());

        public final int id;
        public final String name;
        public final PlantCategory category;
        public final Set<PlantTag> tags;
        public final PlantBaseStats baseStats;
        public final List<PlantAbilityConfig> abilities;
        public final PlantEffectConfig plantFoodEffect;
        public final PlantLevelUpgrades levelUpgrades;

        PlantType(PlantTypeSpec spec) {
                this.id = spec.id;
                this.name = spec.name;
                this.category = spec.category;
                this.tags = spec.tags;
                this.baseStats = spec.baseStats;
                this.abilities = spec.abilities;
                this.plantFoodEffect = spec.plantFoodEffect;
                this.levelUpgrades = spec.levelUpgrades;
        }

        public boolean hasTag(PlantTag tag) {
                return tags != null && tags.contains(tag);
        }

        public boolean isBowlingExclusive() {
                return hasTag(PlantTag.BOWLING);
        }

        public static List<PlantType> bowlingPlants() {
                return List.of(Bowling_Wall_nut, Bowling_Explode_o_nut, Giant_Bowling_Wall_nut);
        }

        public static PlantType fromName(String name) {
                if (name == null || name.isEmpty()) {
                        return null;
                }

                for (PlantType type : PlantType.values()) {
                        if (type.name.equalsIgnoreCase(name)) {
                                return type;
                        }
                }
                return null;
        }
}