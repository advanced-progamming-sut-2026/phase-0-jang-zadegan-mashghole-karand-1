package model.event.events;

import model.data.content.minigame.MiniGameType;

public class MinigameStartedEvent {
    public final MiniGameType miniGameType;

    public MinigameStartedEvent(MiniGameType miniGameType) {
        this.miniGameType = miniGameType;
    }
}
