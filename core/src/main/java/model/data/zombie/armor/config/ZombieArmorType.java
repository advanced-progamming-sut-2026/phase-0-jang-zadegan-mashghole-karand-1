package model.data.zombie.armor.config;

public enum ZombieArmorType {
    CONE(370, "plastic", false),
    BUCKET(1100, "metallic", false),
    BRICK(2200, "brick", false),
    NEWSPAPER(800, "paper", true),
    KNIGHT_ARMOR(3200, "metallic", false),
    SARCOPHAGUS(370, "stone", true);

    public final int hp;
    public final String material;
    public final boolean enragesOnBreak;

    ZombieArmorType(int hp, String material, boolean enragesOnBreak) {
        this.hp = hp;
        this.material = material;
        this.enragesOnBreak = enragesOnBreak;
    }
}