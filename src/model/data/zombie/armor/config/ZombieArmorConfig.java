package model.data.zombie.armor.config;

public class ZombieArmorConfig {
    public final ZombieArmorType type;
    public final int hp;
    public final boolean enragesOnBreak;

    private ZombieArmorConfig(ZombieArmorType type, int health, boolean enragesOnBreak) {
        this.type = type;
        this.hp = health;
        this.enragesOnBreak = enragesOnBreak;
    }

    public static ZombieArmorConfig cone() {
        return new ZombieArmorConfig(ZombieArmorType.CONE, ZombieArmorType.CONE.hp,
                ZombieArmorType.CONE.enragesOnBreak);
    }

    public static ZombieArmorConfig bucket() {
        return new ZombieArmorConfig(ZombieArmorType.BUCKET, ZombieArmorType.BUCKET.hp,
                ZombieArmorType.BUCKET.enragesOnBreak);
    }

    public static ZombieArmorConfig brick() {
        return new ZombieArmorConfig(ZombieArmorType.BRICK, ZombieArmorType.BRICK.hp,
                ZombieArmorType.BRICK.enragesOnBreak);
    }

    public static ZombieArmorConfig newspaper() {
        return new ZombieArmorConfig(ZombieArmorType.NEWSPAPER, ZombieArmorType.NEWSPAPER.hp,
                ZombieArmorType.NEWSPAPER.enragesOnBreak);
    }

    public static ZombieArmorConfig sarcophagus() {
        return new ZombieArmorConfig(ZombieArmorType.SARCOPHAGUS, ZombieArmorType.SARCOPHAGUS.hp,
                ZombieArmorType.SARCOPHAGUS.enragesOnBreak);
    }
}