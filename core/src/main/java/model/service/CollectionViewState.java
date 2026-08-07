package model.service;

import java.util.Collections;
import java.util.List;

public class CollectionViewState {

    public enum Tab {
        PLANTS,
        ZOMBIES
    }

    public enum Mode {
        UNLOCKED,
        ALL
    }

    public static final class Entry {
        public final String name;
        public final boolean unlocked;

        public Entry(String name, boolean unlocked) {
            this.name = name;
            this.unlocked = unlocked;
        }
    }

    public final Tab tab;
    public final Mode mode;
    public final List<Entry> entries;
    public final String detailTitle;
    public final List<String> detailLines;

    public CollectionViewState(Tab tab, Mode mode, List<Entry> entries,
            String detailTitle, List<String> detailLines) {
        this.tab = tab != null ? tab : Tab.PLANTS;
        this.mode = mode != null ? mode : Mode.UNLOCKED;
        this.entries = entries != null ? List.copyOf(entries) : List.of();
        this.detailTitle = detailTitle;
        this.detailLines = detailLines != null ? List.copyOf(detailLines) : List.of();
    }

    public boolean hasDetail() {
        return detailTitle != null && !detailTitle.isBlank();
    }

    public static CollectionViewState empty() {
        return new CollectionViewState(Tab.PLANTS, Mode.UNLOCKED, Collections.emptyList(), null, List.of());
    }
}
