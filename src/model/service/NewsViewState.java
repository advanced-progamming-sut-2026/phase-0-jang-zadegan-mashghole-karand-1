package model.service;

import java.util.Collections;
import java.util.List;

public class NewsViewState {
    public final List<String> messages;

    public NewsViewState(List<String> messages) {
        this.messages = messages != null ? List.copyOf(messages) : List.of();
    }

    public static NewsViewState empty() {
        return new NewsViewState(Collections.emptyList());
    }
}
