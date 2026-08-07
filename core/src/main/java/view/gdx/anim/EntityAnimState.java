package view.gdx.anim;

public final class EntityAnimState {
    public String clipName;
    public float stateTime;
    public boolean looping = true;

    public EntityAnimState(String clipName) {
        this.clipName = clipName;
    }
}
