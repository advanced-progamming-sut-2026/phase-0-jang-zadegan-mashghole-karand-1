package model.event.events;

public class WaveStartedEvent {
    public final int waveNumber;
    public final int totalZombies;
    public final boolean isFinalWave;

    public WaveStartedEvent(int waveNumber, int totalZombies, boolean isFinalWave) {
        this.waveNumber = waveNumber;
        this.totalZombies = totalZombies;
        this.isFinalWave = isFinalWave;
    }
}
