package model.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.gameSetting.GameSetting;
import model.service.Hash;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.User;

final class SqlAccountManager {

    private static final int SESSION_ROW_ID = 1;

    private final String databasePath;
    private final SqlProgressStore progressStore;

    SqlAccountManager(String databasePath, SqlProgressStore progressStore) {
        this.databasePath = databasePath;
        this.progressStore = progressStore;
    }

    boolean register(String username, String password, String email, String nickname, Gender gender,
            SafetyQuestion safetyQuestion) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO users (
                        username, password, email, nickname, gender,
                        safety_question, safety_answer, coins, gems, highest_score, games_played,
                        difficulty_level, game_speed, show_ground_webbing, debug_mode
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0, ?, ?, ?, ?)
                    """)) {
                statement.setString(1, username);
                statement.setString(2, Hash.hashPassword(password));
                statement.setString(3, email);
                statement.setString(4, nickname);
                statement.setString(5, gender.name());
                statement.setString(6, safetyQuestion.type.name());
                statement.setString(7, safetyQuestion.answer);
                statement.setInt(8, GameSetting.DEFAULT_DIFFICULTY);
                statement.setInt(9, GameSetting.DEFAULT_GAME_SPEED);
                statement.setInt(10, 0);
                statement.setInt(11, 0);
                statement.executeUpdate();
            }

            User registered = new User(username, Hash.hashPassword(password), email, nickname, gender,
                    safetyQuestion);
            registered.collection.unlockStarterPlants();
            registered.gameProgress.unlockChapter(ChapterType.ANCIENT_EGYPT);
            progressStore.saveUnlockedPlants(registered);
            progressStore.saveUnlockedChapters(registered);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    List<String> listUsernames() {
        List<String> usernames = new ArrayList<>();
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement("SELECT username FROM users");
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
            return usernames;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load users", e);
        }
    }

    boolean usernameExists(String username) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT 1 FROM users WHERE username = ? LIMIT 1")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check username", e);
        }
    }

    boolean updatePassword(String username, String hashedPassword) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE users SET password = ? WHERE username = ?")) {
            statement.setString(1, hashedPassword);
            statement.setString(2, username);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }

    boolean renameUsername(String oldUsername, String newUsername) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = OFF");
            }
            connection.setAutoCommit(false);
            try {
                updateUsernameReferences(connection, oldUsername, newUsername);
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to change username", e);
        }
    }

    private void updateUsernameReferences(Connection connection, String oldUsername, String newUsername)
            throws SQLException {
        updateChildUsername(connection, "unlocked_chapters", oldUsername, newUsername);
        updateChildUsername(connection, "unlocked_plants", oldUsername, newUsername);
        updateChildUsername(connection, "unlocked_zombies", oldUsername, newUsername);
        updateChildUsername(connection, "completed_levels", oldUsername, newUsername);
        updateChildUsername(connection, "level_high_scores", oldUsername, newUsername);
        updateChildUsername(connection, "unlocked_minigames", oldUsername, newUsername);
        updateChildUsername(connection, "user_news", oldUsername, newUsername);
        updateChildUsername(connection, "user_seed_packets", oldUsername, newUsername);
        updateChildUsername(connection, "user_plant_levels", oldUsername, newUsername);
        updateChildUsername(connection, "user_greenhouse_pots", oldUsername, newUsername);
        updateChildUsername(connection, "user_stored_boosts", oldUsername, newUsername);
        updateChildUsername(connection, "user_quests", oldUsername, newUsername);

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE app_session SET username = ? WHERE username = ?")) {
            statement.setString(1, newUsername);
            statement.setString(2, oldUsername);
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET username = ? WHERE username = ?")) {
            statement.setString(1, newUsername);
            statement.setString(2, oldUsername);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("User not found: " + oldUsername);
            }
        }
    }

    private void updateChildUsername(Connection connection, String table, String oldUsername, String newUsername)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE " + table + " SET username = ? WHERE username = ?")) {
            statement.setString(1, newUsername);
            statement.setString(2, oldUsername);
            statement.executeUpdate();
        }
    }

    String loadPersistedUsername() {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT username, stay_logged_in FROM app_session WHERE id = ?")) {
            statement.setInt(1, SESSION_ROW_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getBoolean("stay_logged_in")) {
                    String username = resultSet.getString("username");
                    return (username != null && !username.isBlank()) ? username : null;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load session", e);
        }
    }

    String loadPersistedAuthToken() {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT auth_token, stay_logged_in FROM app_session WHERE id = ?")) {
            statement.setInt(1, SESSION_ROW_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getBoolean("stay_logged_in")) {
                    String token = resultSet.getString("auth_token");
                    return (token != null && !token.isBlank()) ? token : null;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load auth token", e);
        }
    }

    void persistSession(String username, boolean stayLoggedIn) {
        persistSession(username, stayLoggedIn, null);
    }

    void persistSession(String username, boolean stayLoggedIn, String authToken) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO app_session (id, username, stay_logged_in, auth_token)
                        VALUES (?, ?, ?, ?)
                        ON CONFLICT(id) DO UPDATE SET
                            username = excluded.username,
                            stay_logged_in = excluded.stay_logged_in,
                            auth_token = excluded.auth_token
                        """)) {
            statement.setInt(1, SESSION_ROW_ID);
            if (stayLoggedIn && username != null) {
                statement.setString(2, username);
                statement.setBoolean(3, true);
                statement.setString(4, authToken);
            } else {
                statement.setString(2, null);
                statement.setBoolean(3, false);
                statement.setString(4, null);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to persist session", e);
        }
    }

    void clearPersistedSession() {
        persistSession(null, false, null);
    }
}
