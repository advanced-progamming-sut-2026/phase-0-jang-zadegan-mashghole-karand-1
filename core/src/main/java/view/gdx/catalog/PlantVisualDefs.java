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
                "768/INITIAL/PLANT/SUNFLOWER/SUNFLOWER.PAM", "idle", "special"));
        plants.put(PlantType.TwinSunflower, new PlantVisualDef(
                "768/INITIAL/PLANT/SUNFLOWER_TWIN/SUNFLOWER_TWIN.PAM","idle","special"
        ));
        plants.put(PlantType.PrimalSunflower, new PlantVisualDef(
                "768/FULL/PLANT/PRIMAL_SUNFLOWER/PRIMAL_SUNFLOWER.PAM","idle","special"
        ));
        plants.put(PlantType.GoldBloom, new PlantVisualDef(
                "768/INITIAL/PLANT/GOLDBLOOM/GOLDBLOOM.PAM","idle","attack"
        ));
        //sun shroom
//*******************************
        //shooter
        plants.put(PlantType.PeaShooter, new PlantVisualDef(
                "768/INITIAL/PLANT/PEASHOOTER/PEASHOOTER.PAM", "idle", "attack"));
        plants.put(PlantType.Threepeater, new PlantVisualDef(
                "768/INITIAL/PLANT/THREEPEATER/THREEPEATER.PAM", "idle", "attack"
        ));
        plants.put(PlantType.Cactus, new PlantVisualDef(
                "768/INITIAL/PLANT/CACTUS/CACTUS.PAM", "idle", "attack"
        ));
        plants.put(PlantType.GooPeashooter, new PlantVisualDef(
                "768/INITIAL/PLANT/GOOPEASHOOTER/GOOPEASHOOTER.PAM", "idle", "attack"));
        plants.put(PlantType.FirePeashooter , new PlantVisualDef(
                "768/INITIAL/PLANT/FIREPEASHOOTER/FIREPEASHOOTER.PAM","idle","attack"
        ));
        plants.put(PlantType.SplitPea,new PlantVisualDef(
                "768/FULL/PLANT/SPLITPEA/SPLITPEA.PAM","idle","attack"
        ));
        plants.put(PlantType.SnowPea,new PlantVisualDef(
                "768/INITIAL/PLANT/SNOWPEA/SNOWPEA.PAM","idle","attack"
        ));
        plants.put(PlantType.MegaGatlingPea,new PlantVisualDef(
                "768/INITIAL/PLANT/MEGAGATLING/MEGAGATLING.PAM","idle","attack"
        ));
        plants.put(PlantType.Repeater,new PlantVisualDef(
                "768/INITIAL/PLANT/REPEATER/REPEATER.PAM","idle","attack"
        ));
        plants.put(PlantType.Rotobaga,new PlantVisualDef(
                "768/FULL/PLANT/ROTORUTABAGA/ROTORUTABAGA.PAM","idle","attack"
        ));
        plants.put(PlantType.Starfruit,new PlantVisualDef(
                "768/INITIAL/PLANT/STARFRUIT/STARFRUIT.PAM","idle","attack"
        ));
        plants.put(PlantType.SeaShroom,new PlantVisualDef(
                "768/FULL/PLANT/SEASHROOM/SEASHROOM.PAM","idle","attack"
        ));
        plants.put(PlantType.Fume_shroom,new PlantVisualDef(
                "768/INITIAL/PLANT/FUMESHROOM/FUMESHROOM.PAM","idle","special"
        ));
        //puff shroom
        //bowling bulb
        //citron
//*************************
        //lobber
        plants.put(PlantType.Kernel_pult, new PlantVisualDef(
                "768/INITIAL/PLANT/KERNALPULT/KERNALPULT.PAM", "idle", "attack"
        ));
        plants.put(PlantType.Melon_pult, new PlantVisualDef(
                "768/INITIAL/PLANT/MELONPULT/MELONPULT.PAM", "idle", "attack"
        ));
        plants.put(PlantType.Pepper_pult,new PlantVisualDef(
                "768/FULL/PLANT/PEPPERPULT/PEPPERPULT.PAM","idle","attack"
        ));
        plants.put(PlantType.Cabbage_pult, new PlantVisualDef(
                "768/INITIAL/PLANT/CABBAGEPULT/CABBAGEPULT.PAM", "idle", "attack"
        ));
        plants.put(PlantType.Winter_Melon, new PlantVisualDef(
                "768/FULL/PLANT/WINTERMELON/WINTERMELON.PAM", "idle", "attack"
        ));
//******************************
        //defender
        plants.put(PlantType.Wall_nut, new PlantVisualDef(
                "768/INITIAL/PLANT/WALLNUT/WALLNUT.PAM","idle","idle"
        ));
        plants.put(PlantType.Tall_nut, new PlantVisualDef(
                "768/FULL/PLANT/TALLNUT/TALLNUT.PAM","idle","idle"
        ));
        plants.put(PlantType.Garlic, new PlantVisualDef(
                "768/FULL/PLANT/GARLIC/GARLIC.PAM","idle","idle"
        ));
        plants.put(PlantType.Sweet_Potato, new PlantVisualDef(
                "768/INITIAL/PLANT/SWEETPOTATO/SWEETPOTATO.PAM","idle","idle"
        ));
        plants.put(PlantType.Explode_o_nut, new PlantVisualDef(
                "768/INITIAL/PLANT/EXPLODEONUT/EXPLODEONUT.PAM","idle","idle"
        ));
        plants.put(PlantType.Pumpkin, new PlantVisualDef(
                "768/INITIAL/PLANT/PUMPKIN/PUMPKIN.PAM","idle","idle"
        ));
        plants.put(PlantType.Sun_Bean, new PlantVisualDef(
                "7768/FULL/PLANT/SUNBEAN/SUNBEAN.PAM","idle","idle"
        ));
        //endurian
        //*******************
        //homing
        plants.put(PlantType.Caulipower, new PlantVisualDef(
                "768/INITIAL/PLANT/CAULIPOWER/CAULIPOWER.PAM","idle3_1","attack"
        ));
        plants.put(PlantType.Electric_Blueberry, new PlantVisualDef(
                "768/INITIAL/PLANT/ELECTRICBLUEBERRY/ELECTRICBLUEBERRY.PAM","idle3_1","attack"
        ));
        plants.put(PlantType.Magnet_shroom, new PlantVisualDef(
                "768/FULL/PLANT/MAGNETSHROOM/MAGNETSHROOM.PAM","idle","catch"
        ));
        //cat tail
        //******************
        //melee
        plants.put(PlantType.Bonk_Choy, new PlantVisualDef(
                "768/INITIAL/PLANT/BONKCHOY/BONKCHOY.PAM","idle","attack"
        ));
        plants.put(PlantType.Phat_Beet, new PlantVisualDef(
                "768/FULL/PLANT/PHATBEETS/PHATBEETS.PAM","idle","attack"
        ));
        //wasabi
        //chomper
        //*****************************
        //modifier
        plants.put(PlantType.Torchwood, new PlantVisualDef(
                "768/INITIAL/PLANT/TORCHWOOD/TORCHWOOD.PAM","idle","idle"
        ));
        plants.put(PlantType.Hypno_shroom, new PlantVisualDef(
                "768/INITIAL/PLANT/HYPNOSHROOM/HYPNOSHROOM.PAM","idle","idle"
        ));
        plants.put(PlantType.Imitater, new PlantVisualDef(
                "768/INITIAL/PLANT/IMITATER/IMITATER.PAM","idle","attack"
        ));
        plants.put(PlantType.Lily_Pad, new PlantVisualDef(
                "768/FULL/PLANT/LILYPAD/LILYPAD.PAM","idle","idle"
        ));
        //***************************
        //explosive
        plants.put(PlantType.Cherry_Bomb, new PlantVisualDef(
                "768/FULL/PLANT/CHERRYBOMB/CHERRYBOMB.PAM","idle","attack"
        ));
        plants.put(PlantType.Jalapeno, new PlantVisualDef(
                "768/INITIAL/PLANT/JALAPENO/JALAPENO.PAM","idle","attack"
        ));
        plants.put(PlantType.Tangle_Kelp, new PlantVisualDef(
                "768/FULL/PLANT/TANGLEKELP/TANGLEKELP.PAM","idle","attack"
        ));
        plants.put(PlantType.Iceberg_Lettuce, new PlantVisualDef(
                "768/INITIAL/PLANT/ICEBURG/ICEBURG.PAM","idle","attack"
        ));
        plants.put(PlantType.Ice_shroom, new PlantVisualDef(
                "768/FULL/PLANT/ICESHROOM/ICESHROOM.PAM","idle","attack"
        ));
        plants.put(PlantType.Hot_Potato, new PlantVisualDef(
                "768/FULL/PLANT/HOTPOTATO/HOTPOTATO.PAM","idle","attack"
        ));
        plants.put(PlantType.Grave_Buster, new PlantVisualDef(
                "768/INITIAL/PLANT/GRAVEBUSTER/GRAVEBUSTER.PAM","attack","attack"
        ));

        //potato mine -- primary potato mine -- squash -- doom shroom
//********************************************************
        //mint



        return Collections.unmodifiableMap(plants);
    }
}
