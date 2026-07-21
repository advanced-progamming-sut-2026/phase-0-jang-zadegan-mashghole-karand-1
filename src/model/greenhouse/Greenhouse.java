package model.greenhouse;

import model.core.Position;
import model.data.plant.PlantType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Greenhouse {

    private final static int GREENHOUSE_ROW = 4;
    private final static int GREENHOUSE_COL = 5;
    private LocalDateTime lastHarvest;
    private List<Pot> production;

    public Greenhouse() {
        this.production = new ArrayList<>();
        for (int i = 1 ; i <= GREENHOUSE_ROW ; i++ ){
            for (int j = 1; j <= GREENHOUSE_COL ; j++ ){
                if( i < 2 )
                    production.add(new Pot(new Position(j,i),false));
                else
                    production.add(new Pot(new Position(j,i),true));
            }
        }
    }

    public void unlockSlot() {
        for (Pot pot : production){
            if (pot.isLocked()){
                pot.setUnlocked();
                break;
            }
        }
    }

    public int getUnlockPotCount() {
        return production == null ? 0 : (int) production.stream().filter(p -> !p.isLocked()).count();
    }
    public boolean plantAt(Position pos, Pot.PlantClass plantClass , PlantType type) {
        Pot pot = getPot(pos);
        if (pot == null) return false;
        return pot.plant(plantClass , type);
    }
    public Pot getPot(Position position){
        for (Pot pot : production){
            if (pot.getPosition().x == position.x && pot.getPosition().y == position.y)
                return pot;
        }
        return null;
    }

    public List<Pot> getProduction() {
        return production;
    }
}
