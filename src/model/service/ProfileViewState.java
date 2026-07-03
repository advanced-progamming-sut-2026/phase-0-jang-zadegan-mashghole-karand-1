package model.service;

import model.storage.user.User;

public class ProfileViewState {
    public final String username;
    public final String nickname;
    public final int gamesPlayed;
    public final int coins;
    public final int gems;
    public final int completedLevels;
    public final int highestScore;

    public ProfileViewState(String username, String nickname, int gamesPlayed, int coins, int gems,
            int completedLevels, int highestScore) {
        this.username = username;
        this.nickname = nickname;
        this.gamesPlayed = gamesPlayed;
        this.coins = coins;
        this.gems = gems;
        this.completedLevels = completedLevels;
        this.highestScore = highestScore;
    }

    public static ProfileViewState fromUser(User user) {
        if (user == null) {
            return empty();
        }
        return new ProfileViewState(
                user.username,
                user.nickname,
                user.gamesPlayed,
                user.coins,
                user.gems,
                user.gameProgress.getCompletedLevelCount(),
                user.highestScore);
    }

    public static ProfileViewState empty() {
        return new ProfileViewState("-", "-", 0, 0, 0, 0, 0);
    }
}
