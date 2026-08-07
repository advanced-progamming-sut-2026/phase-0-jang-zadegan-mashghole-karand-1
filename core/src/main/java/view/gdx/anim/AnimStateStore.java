package view.gdx.anim;

import java.util.HashMap;
import java.util.Map;

public final class AnimStateStore {
    private final Map<Long, EntityAnimState> byId = new HashMap<>();

    public EntityAnimState getOrCreate(long entityId, String defaultClip) {
        return byId.computeIfAbsent(entityId, id -> new EntityAnimState(defaultClip));
    }

    public void remove(long entityId) {
        byId.remove(entityId);
    }

    public void clear() {
        byId.clear();
    }

    public void advanceAll(float deltaSeconds) {
        for (EntityAnimState state : byId.values()) {
            state.stateTime += deltaSeconds;
        }
    }
}
