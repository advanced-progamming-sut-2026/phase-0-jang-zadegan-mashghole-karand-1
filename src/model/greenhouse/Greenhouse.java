package model.greenhouse;

import java.time.LocalDateTime;
import java.util.List;

public class Greenhouse {

    private LocalDateTime lastHarvest;
    private List<Pot> production;

    public void addPot(Pot p){
        production.add(p);
    }
}
