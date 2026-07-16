package model.rule;

public class SessionContext {
    private final SessionConfig config;
    private final RuleEngine ruleEngine;

    public SessionContext(SessionConfig config, RuleEngine ruleEngine) {
        this.config = config;
        this.ruleEngine = ruleEngine;
    }

    public SessionConfig getConfig() {
        return config;
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }
}