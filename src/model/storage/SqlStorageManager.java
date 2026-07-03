package model.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import model.data.plant.PlantType;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.User;
import model.world.ChapterType;

public class SqlStorageManager implements StorageManager {
    private static final String DEFAULT_DB_ADDRESS = "data/game.db";

    private static final int SESSION_ROW_ID = 1;

    private final String databasePath;
    private final Object lock = new Object();

    private User currentUser = null;
    private String sessionToken = null;

    public SqlStorageManager() {
        this(DEFAULT_DB_ADDRESS);
    }

    public SqlStorageManager(String databasePath) {
        this.databasePath = databasePath;
        initializeDatabase();
        seedDemoUserIfMissing();
    }

    @Override
    public boolean register(String username, String password, String email, String nickname, Gender gender,
            SafetyQuestion safetyQuestion) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || safetyQuestion == null) {
            return false;
        }

        synchronized (lock) {
            if (usernameExists(username)) {
                return false;
            }

            try (Connection connection = openConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO users (
                            username, password, email, nickname, gender,
                            safety_question, safety_answer, coins, gems, highest_score, games_played
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0)
                        """)) {
                    statement.setString(1, username);
                    statement.setString(2, password);
                    statement.setString(3, email);
                    statement.setString(4, nickname);
                    statement.setString(5, gender.name());
                    statement.setString(6, safetyQuestion.question);
                    statement.setString(7, safetyQuestion.answer);
                    statement.executeUpdate();
                }
                return true;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to register user", e);
            }
        }
    }

    @Override
    public boolean login(String username, String password, boolean stayLoggedIn) {
        if (username == null || password == null) {
            return false;
        }

        synchronized (lock) {
            User user = loadUser(username);
            if (user == null || !user.password.equals(password)) {
                return false;
            }

            currentUser = user;
            sessionToken = UUID.randomUUID().toString();
            persistSession(stayLoggedIn ? username : null, stayLoggedIn);
            return true;
        }
    }

    @Override
    public void logout() {
        synchronized (lock) {
            currentUser = null;
            sessionToken = null;
            clearPersistedSession();
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null && sessionToken != null;
    }

    @Override
    public boolean usernameExists(String username) {
        if (username == null) {
            return false;
        }

        synchronized (lock) {
            try (Connection connection = openConnection();
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
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        synchronized (lock) {
            return loadUser(username);
        }
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        if (username == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }

        synchronized (lock) {
            try (Connection connection = openConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE users SET password = ? WHERE username = ?")) {
                statement.setString(1, newPassword);
                statement.setString(2, username);
                boolean updated = statement.executeUpdate() > 0;
                if (updated && currentUser != null && currentUser.username.equals(username)) {
                    currentUser.password = newPassword;
                }
                return updated;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update password", e);
            }
        }
    }

    @Override
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.username : null;
    }

    @Override
    public void saveProgress() {
        synchronized (lock) {
            if (currentUser == null) {
                return;
            }
            saveUserProgress(currentUser);
        }
    }

    @Override
    public void loadProgress() {
        String username = null;
        boolean shouldRestoreSession = false;

        synchronized (lock) {
            try (Connection connection = openConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT username, stay_logged_in FROM app_session WHERE id = ?")) {
                statement.setInt(1, SESSION_ROW_ID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next() && resultSet.getBoolean("stay_logged_in")) {
                        username = resultSet.getString("username");
                        shouldRestoreSession = username != null && !username.isBlank();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load session", e);
            }

            if (!shouldRestoreSession) {
                return;
            }

            User user = loadUser(username);
            if (user == null) {
                clearPersistedSession();
                return;
            }

            currentUser = user;
            sessionToken = UUID.randomUUID().toString();
        }
    }

    @Override
    public void recordGamePlayed() {
        if (!isLoggedIn()) {
            return;
        }
        synchronized (lock) {
            currentUser.gamesPlayed++;
            saveUserProfile(currentUser);
        }
    }

    @Override
    public void markLevelCompleted(String levelId) {
        if (!isLoggedIn() || levelId == null || levelId.isBlank()) {
            return;
        }
        synchronized (lock) {
            currentUser.gameProgress.completeLevel(levelId);
            try (Connection connection = openConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT OR IGNORE INTO completed_levels (username, level_id) VALUES (?, ?)")) {
                statement.setString(1, currentUser.username);
                statement.setString(2, levelId);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to mark level completed", e);
            }
        }
    }

    @Override
    public boolean changeUsername(String newUsername) {
        if (!isLoggedIn() || newUsername == null || newUsername.isBlank()) {
            return false;
        }
        synchronized (lock) {
            String oldUsername = currentUser.username;
            if (oldUsername.equals(newUsername)) {
                return false;
            }
            if (usernameExists(newUsername)) {
                return false;
            }

            try (Connection connection = openConnection()) {
                connection.setAutoCommit(false);
                try {
                    updateUsernameReferences(connection, oldUsername, newUsername);
                    connection.commit();
                    currentUser.username = newUsername;
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
    }

    @Override
    public void changeNickname(String nickname) {
        if (!isLoggedIn() || nickname == null) {
            return;
        }
        synchronized (lock) {
            currentUser.nickname = nickname;
            saveUserProfile(currentUser);
        }
    }

    @Override
    public void changeEmail(String email) {
        if (!isLoggedIn() || email == null) {
            return;
        }
        synchronized (lock) {
            currentUser.email = email;
            saveUserProfile(currentUser);
        }
    }

    @Override
    public boolean changeProfilePassword(String oldPassword, String newPassword) {
        if (!isLoggedIn() || oldPassword == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        synchronized (lock) {
            if (!currentUser.password.equals(oldPassword)) {
                return false;
            }
            currentUser.password = newPassword;
            saveUserProfile(currentUser);
            return true;
        }
    }

    @Override
    public void updateUserProfile(User profile) {
        if (profile == null || currentUser == null) {
            return;
        }

        synchronized (lock) {
            if (!currentUser.username.equals(profile.username)) {
                return;
            }
            currentUser = profile;
            saveUserProfile(profile);
        }
    }

    @Override
    public void unlockChapter(ChapterType chapter) {
        if (!isLoggedIn() || chapter == null) {
            return;
        }

        synchronized (lock) {
            if (currentUser.gameProgress.isChapterUnlocked(chapter)) {
                return;
            }
            currentUser.gameProgress.unlockChapter(chapter);
            try (Connection connection = openConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT OR IGNORE INTO unlocked_chapters (username, chapter) VALUES (?, ?)")) {
                statement.setString(1, currentUser.username);
                statement.setString(2, chapter.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to unlock chapter", e);
            }
        }
    }

    @Override
    public boolean isChapterUnlocked(ChapterType chapter) {
        if (!isLoggedIn() || chapter == null) {
            return false;
        }
        return currentUser.gameProgress.isChapterUnlocked(chapter);
    }

    @Override
    public List<ChapterType> getUnlockedChapters() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentUser.gameProgress.getUnlockedChapters());
    }

    @Override
    public void unlockPlant(PlantType plant) {
        if (!isLoggedIn() || plant == null) {
            return;
        }

        synchronized (lock) {
            if (currentUser.collection.isPlantUnlocked(plant)) {
                return;
            }
            currentUser.collection.unlockPlant(plant);
            try (Connection connection = openConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT OR IGNORE INTO unlocked_plants (username, plant) VALUES (?, ?)")) {
                statement.setString(1, currentUser.username);
                statement.setString(2, plant.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to unlock plant", e);
            }
        }
    }

    @Override
    public boolean isPlantUnlocked(PlantType plant) {
        if (!isLoggedIn() || plant == null) {
            return false;
        }
        return currentUser.collection.isPlantUnlocked(plant);
    }

    @Override
    public List<PlantType> getUnlockedPlants() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentUser.collection.getUnlockedPlants());
    }

    private void initializeDatabase() {
        File databaseFile = new File(databasePath);
        File parent = databaseFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        synchronized (lock) {
            try (Connection connection = openConnection(); Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            username TEXT PRIMARY KEY,
                            password TEXT NOT NULL,
                            email TEXT NOT NULL,
                            nickname TEXT NOT NULL,
                            gender TEXT NOT NULL,
                            safety_question TEXT NOT NULL,
                            safety_answer TEXT NOT NULL,
                            coins INTEGER NOT NULL DEFAULT 0,
                            gems INTEGER NOT NULL DEFAULT 0,
                            highest_score INTEGER NOT NULL DEFAULT 0,
                            games_played INTEGER NOT NULL DEFAULT 0
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS completed_levels (
                            username TEXT NOT NULL,
                            level_id TEXT NOT NULL,
                            PRIMARY KEY (username, level_id),
                            FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS app_session (
                            id INTEGER PRIMARY KEY CHECK (id = 1),
                            username TEXT,
                            stay_logged_in INTEGER NOT NULL DEFAULT 0,
                            FOREIGN KEY (username) REFERENCES users(username) ON DELETE SET NULL
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS unlocked_chapters (
                            username TEXT NOT NULL,
                            chapter TEXT NOT NULL,
                            PRIMARY KEY (username, chapter),
                            FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS unlocked_plants (
                            username TEXT NOT NULL,
                            plant TEXT NOT NULL,
                            PRIMARY KEY (username, plant),
                            FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                        )
                        """);
                statement.execute("INSERT OR IGNORE INTO app_session (id, stay_logged_in) VALUES (1, 0)");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize database", e);
            }
        }
    }

    private void seedDemoUserIfMissing() {
        if (usernameExists("player")) {
            return;
        }

        SafetyQuestion safetyQuestion = new SafetyQuestion("DEMO_QUESTION", "DEMO_ANSWER");
        register("player", "password", "test@test.com", "player", Gender.MALE, safetyQuestion);

        User demoUser = loadUser("player");
        if (demoUser == null) {
            return;
        }

        demoUser.gameProgress.unlockChapter(ChapterType.ANCIENT_EGYPT);
        demoUser.collection.unlockPlants(Arrays.asList(
                PlantType.Sunflower, PlantType.PeaShooter, PlantType.Repeater));
        saveUserProgress(demoUser);
    }

    private Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private User loadUser(String username) {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("nickname"),
                        Gender.valueOf(resultSet.getString("gender")),
                        new SafetyQuestion(
                                resultSet.getString("safety_question"),
                                resultSet.getString("safety_answer")));
                user.coins = resultSet.getInt("coins");
                user.gems = resultSet.getInt("gems");
                user.highestScore = resultSet.getInt("highest_score");
                user.gamesPlayed = resultSet.getInt("games_played");

                loadUnlockedChapters(connection, user);
                loadUnlockedPlants(connection, user);
                loadCompletedLevels(connection, user);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user: " + username, e);
        }
    }

    private void loadUnlockedChapters(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT chapter FROM unlocked_chapters WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    user.gameProgress.unlockChapter(ChapterType.valueOf(resultSet.getString("chapter")));
                }
            }
        }
    }

    private void loadUnlockedPlants(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT plant FROM unlocked_plants WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlantType plant = PlantType.valueOf(resultSet.getString("plant"));
                    user.collection.unlockPlant(plant);
                }
            }
        }
    }

    private void loadCompletedLevels(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT level_id FROM completed_levels WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    user.gameProgress.completeLevel(resultSet.getString("level_id"));
                }
            }
        }
    }

    private void saveUserProgress(User user) {
        saveUserProfile(user);
        saveUnlockedChapters(user);
        saveUnlockedPlants(user);
        saveCompletedLevels(user);
    }

    private void saveUserProfile(User user) {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement("""
                        UPDATE users
                        SET password = ?, email = ?, nickname = ?, gender = ?,
                            safety_question = ?, safety_answer = ?,
                            coins = ?, gems = ?, highest_score = ?, games_played = ?
                        WHERE username = ?
                        """)) {
            statement.setString(1, user.password);
            statement.setString(2, user.email);
            statement.setString(3, user.nickname);
            statement.setString(4, user.gender.name());
            statement.setString(5, user.safetyQuestion.question);
            statement.setString(6, user.safetyQuestion.answer);
            statement.setInt(7, user.coins);
            statement.setInt(8, user.gems);
            statement.setInt(9, user.highestScore);
            statement.setInt(10, user.gamesPlayed);
            statement.setString(11, user.username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user profile", e);
        }
    }

    private void saveUnlockedChapters(User user) {
        try (Connection connection = openConnection()) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM unlocked_chapters WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO unlocked_chapters (username, chapter) VALUES (?, ?)")) {
                for (ChapterType chapter : user.gameProgress.getUnlockedChapters()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, chapter.name());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save unlocked chapters", e);
        }
    }

    private void saveUnlockedPlants(User user) {
        try (Connection connection = openConnection()) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM unlocked_plants WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO unlocked_plants (username, plant) VALUES (?, ?)")) {
                for (PlantType plant : user.collection.getUnlockedPlants()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, plant.name());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save unlocked plants", e);
        }
    }

    private void saveCompletedLevels(User user) {
        try (Connection connection = openConnection()) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM completed_levels WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO completed_levels (username, level_id) VALUES (?, ?)")) {
                for (String levelId : user.gameProgress.getCompletedLevelIds()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, levelId);
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save completed levels", e);
        }
    }

    private void updateUsernameReferences(Connection connection, String oldUsername, String newUsername)
            throws SQLException {
        updateChildUsername(connection, "unlocked_chapters", oldUsername, newUsername);
        updateChildUsername(connection, "unlocked_plants", oldUsername, newUsername);
        updateChildUsername(connection, "completed_levels", oldUsername, newUsername);

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

    private void persistSession(String username, boolean stayLoggedIn) {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO app_session (id, username, stay_logged_in)
                        VALUES (?, ?, ?)
                        ON CONFLICT(id) DO UPDATE SET
                            username = excluded.username,
                            stay_logged_in = excluded.stay_logged_in
                        """)) {
            statement.setInt(1, SESSION_ROW_ID);
            if (stayLoggedIn && username != null) {
                statement.setString(2, username);
                statement.setBoolean(3, true);
            } else {
                statement.setString(2, null);
                statement.setBoolean(3, false);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to persist session", e);
        }
    }

    private void clearPersistedSession() {
        persistSession(null, false);
    }
}
