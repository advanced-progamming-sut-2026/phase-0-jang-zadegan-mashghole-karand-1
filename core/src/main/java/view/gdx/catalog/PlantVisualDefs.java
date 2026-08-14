package view.gdx.catalog;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import model.data.plant.PlantType;

public final class PlantVisualDefs {
    private PlantVisualDefs() {
    }

    public static Map<PlantType, PlantVisualDef> create() {
        Map<PlantType, PlantVisualDef> plants = new EnumMap<>(PlantType.class);
        //sun
        plants.put(PlantType.Sunflower, new PlantVisualDef(
                "768/INITIAL/PLANT/SUNFLOWER/SUNFLOWER.PAM", "idle", "special","plantfood"));
        plants.put(PlantType.TwinSunflower, new PlantVisualDef(
                "768/INITIAL/PLANT/SUNFLOWER_TWIN/SUNFLOWER_TWIN.PAM","idle","special","plantfood"
        ));
        plants.put(PlantType.PrimalSunflower, new PlantVisualDef(
                "768/FULL/PLANT/PRIMAL_SUNFLOWER/PRIMAL_SUNFLOWER.PAM","idle","special","plantfood"
        ));
        plants.put(PlantType.GoldBloom, new PlantVisualDef(
                "768/INITIAL/PLANT/GOLDBLOOM/GOLDBLOOM.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.SunShroom, new PlantVisualDef(
                "768/FULL/PLANT/SUNSHROOM/SUNSHROOM.PAM","idle_stage","special_stage","plantfood_stage"
        ));
//*******************************
        //shooter
        plants.put(PlantType.PeaShooter, new PlantVisualDef(
                "768/INITIAL/PLANT/PEASHOOTER/PEASHOOTER.PAM", "idle", "attack","plantfood"));
        plants.put(PlantType.Threepeater, new PlantVisualDef(
                "768/INITIAL/PLANT/THREEPEATER/THREEPEATER.PAM", "idle", "attack","plantfood"
        ));
        plants.put(PlantType.Cactus, new PlantVisualDef(
                "768/INITIAL/PLANT/CACTUS/CACTUS.PAM", "idle", "attack","plantfood"
        ));
        plants.put(PlantType.GooPeashooter, new PlantVisualDef(
                "768/INITIAL/PLANT/GOOPEASHOOTER/GOOPEASHOOTER.PAM", "idle", "attack","plantfood"));
        plants.put(PlantType.FirePeashooter , new PlantVisualDef(
                "768/INITIAL/PLANT/FIREPEASHOOTER/FIREPEASHOOTER.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.SplitPea,new PlantVisualDef(
                "768/FULL/PLANT/SPLITPEA/SPLITPEA.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.SnowPea,new PlantVisualDef(
                "768/INITIAL/PLANT/SNOWPEA/SNOWPEA.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.MegaGatlingPea,new PlantVisualDef(
                "768/INITIAL/PLANT/MEGAGATLING/MEGAGATLING.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Repeater,new PlantVisualDef(
                "768/INITIAL/PLANT/REPEATER/REPEATER.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Rotobaga,new PlantVisualDef(
                "768/FULL/PLANT/ROTORUTABAGA/ROTORUTABAGA.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Starfruit,new PlantVisualDef(
                "768/INITIAL/PLANT/STARFRUIT/STARFRUIT.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.SeaShroom,new PlantVisualDef(
                "768/FULL/PLANT/SEASHROOM/SEASHROOM.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Fume_shroom,new PlantVisualDef(
                "768/INITIAL/PLANT/FUMESHROOM/FUMESHROOM.PAM","idle","special","plantfood"
        ));
        plants.put(PlantType.PuffShroom,new PlantVisualDef(
                "768/INITIAL/PLANT/PUFFSHROOM/PUFFSHROOM.PAM","idle_stage","special_stage","plantfood"
        ));
        plants.put(PlantType.Citron,new PlantVisualDef(
                "768/FULL/PLANT/CITRON/CITRON.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.BowlingBulb,new PlantVisualDef(
                "768/FULL/PLANT/BOWLINGBULB/BOWLINGBULB.PAM","idle","special3",
                "plantfood3"
        ));
//*************************
        //lobber
        plants.put(PlantType.Kernel_pult, new PlantVisualDef(
                "768/INITIAL/PLANT/KERNALPULT/KERNALPULT.PAM", "idle", "attack","plantfood"
        ));
        plants.put(PlantType.Melon_pult, new PlantVisualDef(
                "768/INITIAL/PLANT/MELONPULT/MELONPULT.PAM", "idle", "attack","plantfood"
        ));
        plants.put(PlantType.Pepper_pult,new PlantVisualDef(
                "768/FULL/PLANT/PEPPERPULT/PEPPERPULT.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Cabbage_pult, new PlantVisualDef(
                "768/INITIAL/PLANT/CABBAGEPULT/CABBAGEPULT.PAM", "idle", "attack","plantfood"
        ));
        plants.put(PlantType.Winter_Melon, new PlantVisualDef(
                "768/FULL/PLANT/WINTERMELON/WINTERMELON.PAM", "idle", "attack","plantfood"
        ));
//******************************
        //defender
        plants.put(PlantType.Wall_nut, new PlantVisualDef(
                "768/INITIAL/PLANT/WALLNUT/WALLNUT.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Tall_nut, new PlantVisualDef(
                "768/FULL/PLANT/TALLNUT/TALLNUT.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Garlic, new PlantVisualDef(
                "768/FULL/PLANT/GARLIC/GARLIC.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Sweet_Potato, new PlantVisualDef(
                "768/INITIAL/PLANT/SWEETPOTATO/SWEETPOTATO.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Explode_o_nut, new PlantVisualDef(
                "768/INITIAL/PLANT/EXPLODEONUT/EXPLODEONUT.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Pumpkin, new PlantVisualDef(
                "768/INITIAL/PLANT/PUMPKIN/PUMPKIN.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Sun_Bean, new PlantVisualDef(
                "768/FULL/PLANT/SUNBEAN/SUNBEAN.PAM","idle","idle","plantfood"
        ));
        //endurian add damage and attack damage latter if we need
        plants.put(PlantType.Endurian, new PlantVisualDef(
                "768/FULL/PLANT/ENDURIAN/ENDURIAN.PAM","idle","attack_loop","plantfood_on"
        ));
        //*******************
        //homing
        plants.put(PlantType.Caulipower, new PlantVisualDef(
                "768/INITIAL/PLANT/CAULIPOWER/CAULIPOWER.PAM","idle3_1","attack","plantfood"
        ));
        plants.put(PlantType.Electric_Blueberry, new PlantVisualDef(
                "768/INITIAL/PLANT/ELECTRICBLUEBERRY/ELECTRICBLUEBERRY.PAM","idle3_1","attack","plantfood"
        ));
        plants.put(PlantType.Magnet_shroom, new PlantVisualDef(
                "768/FULL/PLANT/MAGNETSHROOM/MAGNETSHROOM.PAM","idle","catch","plantfood"
        ));
        plants.put(PlantType.Cat_tail, new PlantVisualDef(
                "768/FULL/PLANT/LIGHTNINGREED/LIGHTNINGREED.PAM","idle","attack","plantfood"
        ));
        //******************
        //melee
        plants.put(PlantType.Bonk_Choy, new PlantVisualDef(
                "768/INITIAL/PLANT/BONKCHOY/BONKCHOY.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Phat_Beet, new PlantVisualDef(
                "768/FULL/PLANT/PHATBEETS/PHATBEETS.PAM","idle","attack","plantfood"
        ));
        //wasabi add attack stage
        plants.put(PlantType.Wasabi_Whip, new PlantVisualDef(
                "768/INITIAL/PLANT/WASABIWHIP/WASABIWHIP.PAM",
                "idle",
                "attack",
                "plantfood"
        ));
        //chomper add special
        plants.put(PlantType.Chomper, new PlantVisualDef(
                "768/INITIAL/PLANT/CHOMPER/CHOMPER.PAM",
                "idle",
                "bite",
                "plantfood"
        ));
        //*****************************
        //modifier
        plants.put(PlantType.Torchwood, new PlantVisualDef(
                "768/INITIAL/PLANT/TORCHWOOD/TORCHWOOD.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Hypno_shroom, new PlantVisualDef(
                "768/INITIAL/PLANT/HYPNOSHROOM/HYPNOSHROOM.PAM","idle","idle","plantfood"
        ));
        plants.put(PlantType.Imitater, new PlantVisualDef(
                "768/INITIAL/PLANT/IMITATER/IMITATER.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Lily_Pad, new PlantVisualDef(
                "768/FULL/PLANT/LILYPAD/LILYPAD.PAM","idle","idle","plantfood"
        ));
        //***************************
        //explosive
        plants.put(PlantType.Cherry_Bomb, new PlantVisualDef(
                "768/FULL/PLANT/CHERRYBOMB/CHERRYBOMB.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Jalapeno, new PlantVisualDef(
                "768/INITIAL/PLANT/JALAPENO/JALAPENO.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Tangle_Kelp, new PlantVisualDef(
                "768/FULL/PLANT/TANGLEKELP/TANGLEKELP.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Iceberg_Lettuce, new PlantVisualDef(
                "768/INITIAL/PLANT/ICEBURG/ICEBURG.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Ice_shroom, new PlantVisualDef(
                "768/FULL/PLANT/ICESHROOM/ICESHROOM.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Hot_Potato, new PlantVisualDef(
                "768/FULL/PLANT/HOTPOTATO/HOTPOTATO.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Grave_Buster, new PlantVisualDef(
                "768/INITIAL/PLANT/GRAVEBUSTER/GRAVEBUSTER.PAM","attack","attack","plantfood"
        ));
        plants.put(PlantType.Potato_Mine , new PlantVisualDef(
            "768/INITIAL/PLANT/POTATOMINE/POTATOMINE.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Primal_Potato_Mine , new PlantVisualDef(
                "768/FULL/PLANT/PRIMAL_POTATOMINE/PRIMAL_POTATOMINE.PAM","idle","attack","plantfood"
        ));
        plants.put(PlantType.Squash , new PlantVisualDef(
                "768/INITIAL/PLANT/SQUASH/SQUASH.PAM","idle",
                "jump_up_right","plantfood_jump_down_right"
        ));
        plants.put(PlantType.Doom_shroom , new PlantVisualDef(
                "768/FULL/PLANT/DOOMSHROOM/DOOMSHROOM.PAM","stage2_idle",
                "stage2_explode",null
        ));
//********************************************************
        //mint
        plants.put(PlantType.Appease_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/APPEASEMINT/APPEASEMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.catTail_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/AILMINT/AILMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Arma_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/ARMAMINT/ARMAMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Bombard_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/BOMBARDMINT/BOMBARDMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Enchant_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/ENCHANTMINT/ENCHANTMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Enforce_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/ENFORCEMINT/ENFORCEMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Enlighten_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/ENLIGHTENMINT/ENLIGHTENMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Pierce_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/PEPPERMINT/PEPPERMINT.PAM","loop",
                null,null
        ));
        plants.put(PlantType.Reinforce_mint , new PlantVisualDef(
                "768/INITIAL/EMPOWERMINTS/PLANT/REINFORCEMINT/REINFORCEMINT.PAM","loop",
                null,null
        ));
        return Collections.unmodifiableMap(plants);
    }
}
