package model.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.storage.user.User;

final class SqlUserSaver {

    private final String databasePath;
    private final SqlProgressStore progressStore;

    SqlUserSaver(String databasePath, SqlProgressStore progressStore) {
        this.databasePath = databasePath;
        this.progressStore = progressStore;
    }

    void saveUserProgress(User user) {
        saveUserProfile(user);
        progressStore.saveAll(user);
    }

    void saveUserProfile(User user) {
        try (Connection connection = SqlConnections.open(databasePath);

                PreparedStatement statement = connection.prepareStatement("""
                        UPDATE users
                        SET password = ?, email = ?, nickname = ?, gender = ?,
                            safety_question = ?, safety_answer = ?,
                            coins = ?, gems = ?, highest_score = ?, games_played = ?,
                            difficulty_level = ?,
                            shop_last_refresh_date = ?, shop_daily_deal_plant = ?, shop_daily_deal_purchased = ?

                        WHERE username = ?
                        """)) {
            statement.setString(1, user.password);
            statement.setString(2, user.email);
            statement.setString(3, user.nickname);
            statement.setString(4, user.gender.name());
            statement.setString(5, user.safetyQuestion.type.name());
            statement.setString(6, user.safetyQuestion.answer);
            statement.setInt(7, user.coins);
            statement.setInt(8, user.gems);
            statement.setInt(9, user.highestScore);
            statement.setInt(10, user.gamesPlayed);
            statement.setInt(11, user.preferredSetting.getDifficultyLevel());
            statement.setString(12, user.dailyDeal.lastRefreshDate == null
                    ? null
                    : user.dailyDeal.lastRefreshDate.toString());
            statement.setString(13, user.dailyDeal.dailyDealPlant == null
                    ? null
                    : user.dailyDeal.dailyDealPlant.name());
            statement.setInt(14, user.dailyDeal.dailyDealPurchased ? 1 : 0);
            statement.setString(15, user.username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user profile", e);
        }
    }
}
