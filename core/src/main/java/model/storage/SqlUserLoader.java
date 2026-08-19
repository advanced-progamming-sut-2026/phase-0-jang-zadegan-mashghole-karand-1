package model.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import model.data.plant.PlantType;
import model.gameSetting.GameSetting;
import model.quest.QuestAssigner;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.SafetyQuestionType;
import model.storage.user.User;

final class SqlUserLoader {

    private final String databasePath;
    private final SqlProgressLoader progressLoader;

    SqlUserLoader(String databasePath, SqlProgressLoader progressLoader) {
        this.databasePath = databasePath;
        this.progressLoader = progressLoader;
    }

    User loadUser(String username) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                User user = buildUserFromRow(resultSet);
                progressLoader.loadAll(connection, user);
                QuestAssigner.ensureAssigned(user);
                progressLoader.loadQuests(connection, user);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user: " + username, e);
        }
    }

    void loadQuestProgress(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            QuestAssigner.ensureAssigned(user);
            progressLoader.loadQuests(connection, user);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load quest progress", e);
        }
    }

    private User buildUserFromRow(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("email"),
                resultSet.getString("nickname"),
                Gender.valueOf(resultSet.getString("gender")),
                new SafetyQuestion(
                        SafetyQuestionType.fromStored(resultSet.getString("safety_question")),
                        resultSet.getString("safety_answer")));
        user.coins = resultSet.getInt("coins");
        user.gems = resultSet.getInt("gems");
        user.highestScore = resultSet.getInt("highest_score");
        user.gamesPlayed = resultSet.getInt("games_played");
        user.preferredSetting.setDifficultyLevel(readDifficultyLevel(resultSet));
        user.preferredSetting.setGameSpeed(readGameSpeed(resultSet));
        user.preferredSetting.setShowGroundWebbing(readFlag(resultSet, "show_ground_webbing"));
        user.preferredSetting.setDebugMode(readFlag(resultSet, "debug_mode"));
        loadShopStateFromUsersRow(resultSet, user);
        return user;
    }

    private int readDifficultyLevel(ResultSet resultSet) throws SQLException {
        try {
            return resultSet.getInt("difficulty_level");
        } catch (SQLException e) {
            return GameSetting.DEFAULT_DIFFICULTY;
        }
    }

    private void loadShopStateFromUsersRow(ResultSet resultSet, User user) {
        try {
            String refreshDate = resultSet.getString("shop_last_refresh_date");
            String dailyPlant = resultSet.getString("shop_daily_deal_plant");
            int purchased = resultSet.getInt("shop_daily_deal_purchased");

            user.dailyDeal.lastRefreshDate = (refreshDate == null || refreshDate.isBlank())
                    ? null
                    : LocalDate.parse(refreshDate);

            user.dailyDeal.dailyDealPlant = (dailyPlant == null || dailyPlant.isBlank())
                    ? null
                    : PlantType.valueOf(dailyPlant);

            user.dailyDeal.dailyDealPurchased = purchased == 1;
        } catch (SQLException ignored) {
            // Columns may not exist yet on older DBs.
        }
    }
    private int readGameSpeed(ResultSet resultSet) {
        try {
            return resultSet.getInt("game_speed");
        } catch (SQLException e) {
            return GameSetting.DEFAULT_GAME_SPEED;
        }
    }

    private boolean readFlag(ResultSet resultSet, String column) {
        try {
            return resultSet.getInt(column) == 1;
        } catch (SQLException e) {
            return false;
        }
    }
}
