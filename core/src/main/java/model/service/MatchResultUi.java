package model.service;

/** UI payload for the post-match / post-level result overlay. */
public final class MatchResultUi {
    public final String title;
    public final String detail;
    public final boolean won;
    /** True when Restart should call local restartLevel (couch/offline). */
    public final boolean localRestart;
    /** True when Restart should request an online rematch/restart while match is still active. */
    public final boolean onlineRestart;
    /** True when Restart should open the I, Zombie mode picker (match already over online). */
    public final boolean returnToIZombieModes;

    public MatchResultUi(String title, String detail, boolean won,
            boolean localRestart, boolean onlineRestart, boolean returnToIZombieModes) {
        this.title = title == null ? "" : title;
        this.detail = detail == null ? "" : detail;
        this.won = won;
        this.localRestart = localRestart;
        this.onlineRestart = onlineRestart;
        this.returnToIZombieModes = returnToIZombieModes;
    }

    public static MatchResultUi of(String title, String detail, boolean won) {
        return new MatchResultUi(title, detail, won, true, false, false);
    }
}
