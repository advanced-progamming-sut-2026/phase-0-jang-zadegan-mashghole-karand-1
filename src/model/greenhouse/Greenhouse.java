package model.greenhouse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Greenhouse {

    private LocalDateTime lastHarvest;
    private List<Pot> production;

    public Greenhouse() {
        this.production = new ArrayList<>();
    }

    public void addPot(Pot p) {
        if (production == null) {
            production = new ArrayList<>();
        }
        production.add(p);
    }

    public int getPotCount() {
        return production == null ? 0 : production.size();
    }
}
