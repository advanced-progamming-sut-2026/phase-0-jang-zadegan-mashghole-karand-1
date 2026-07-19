package model.rule;

import model.systems.WaveManager;

public class SessionContext {
    private final SessionConfig config;
    private final RuleEngine ruleEngine;
    private final WaveManager waveManager;

    public SessionContext(SessionConfig config, RuleEngine ruleEngine, WaveManager waveManager) {
        this.config = config;
        this.ruleEngine = ruleEngine;
        this.waveManager = waveManager;
    }

    public SessionConfig getConfig() {
        return config;
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }
}