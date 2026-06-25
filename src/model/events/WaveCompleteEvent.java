package model.events;

public class WaveCompleteEvent {
    public final int waveNumber;
    public final boolean isFinal;

    public WaveCompleteEvent(int waveNumber, boolean isFinal) {
        this.waveNumber = waveNumber;
        this.isFinal = isFinal;
    }
}
