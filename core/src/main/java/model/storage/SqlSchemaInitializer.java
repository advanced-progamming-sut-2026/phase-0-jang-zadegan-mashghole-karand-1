package model.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class SqlSchemaInitializer {

    private SqlSchemaInitializer() {
    }

    static void initialize(String databasePath) {
        File databaseFile = new File(databasePath);
        File parent = databaseFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (Connection connection = SqlConnections.open(databasePath);
                Statement statement = connection.createStatement()) {
            createAccountTables(statement);
            createUnlockTables(statement);
            statement.execute("INSERT OR IGNORE INTO app_session (id, stay_logged_in) VALUES (1, 0)");
            applyLegacyColumnMigrations(statement);
            createProgressTables(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void createAccountTables(Statement statement) throws SQLException {
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
                    games_played INTEGER NOT NULL DEFAULT 0,
                    difficulty_level INTEGER NOT NULL DEFAULT 3,
                    game_speed INTEGER NOT NULL DEFAULT 2,
                    show_ground_webbing INTEGER NOT NULL DEFAULT 0,
                    debug_mode INTEGER NOT NULL DEFAULT 0
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
                CREATE TABLE IF NOT EXISTS level_high_scores (
                    username TEXT NOT NULL,
                    level_id TEXT NOT NULL,
                    score INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (username, level_id),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS app_session (
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    username TEXT,
                    stay_logged_in INTEGER NOT NULL DEFAULT 0,
                    auth_token TEXT,
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE SET NULL
                )
                """);
    }

    private static void createUnlockTables(Statement statement) throws SQLException {
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
        statement.execute("""
                CREATE TABLE IF NOT EXISTS unlocked_zombies (
                    username TEXT NOT NULL,
                    zombie TEXT NOT NULL,
                    PRIMARY KEY (username, zombie),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS unlocked_minigames (
                    username TEXT NOT NULL,
                    minigame TEXT NOT NULL,
                    PRIMARY KEY (username, minigame),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS user_news (
                    username TEXT NOT NULL,
                    news_id TEXT NOT NULL,
                    message TEXT NOT NULL,
                    is_read INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL,
                    PRIMARY KEY (username, news_id),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
    }

    private static void applyLegacyColumnMigrations(Statement statement) {
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN difficulty_level INTEGER NOT NULL DEFAULT 3");
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN shop_last_refresh_date TEXT");
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN shop_daily_deal_plant TEXT");
        tryAlterTable(statement,
                "ALTER TABLE users ADD COLUMN shop_daily_deal_purchased INTEGER NOT NULL DEFAULT 0");
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN plant_food INTEGER NOT NULL DEFAULT 0");
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN game_speed INTEGER NOT NULL DEFAULT 2");
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN show_ground_webbing INTEGER NOT NULL DEFAULT 0");
        tryAlterTable(statement, "ALTER TABLE users ADD COLUMN debug_mode INTEGER NOT NULL DEFAULT 0");
        tryAlterTable(statement, "ALTER TABLE app_session ADD COLUMN auth_token TEXT");
    }

    private static void tryAlterTable(Statement statement, String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException ignored) {
            // Column already exists.
        }
    }

    private static void createProgressTables(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS user_seed_packets (
                    username TEXT NOT NULL,
                    plant TEXT NOT NULL,
                    amount INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (username, plant),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS user_greenhouse_pots (
                    username TEXT NOT NULL,
                    col INTEGER NOT NULL,
                    row INTEGER NOT NULL,
                    locked INTEGER NOT NULL,
                    empty INTEGER NOT NULL,
                    plant_class TEXT,
                    plant_type TEXT,
                    planted_at TEXT,
                    PRIMARY KEY (username, col, row),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
        createQuestAndBoostTables(statement);
    }

    private static void createQuestAndBoostTables(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS user_stored_boosts (
                    username TEXT NOT NULL,
                    plant TEXT NOT NULL,
                    PRIMARY KEY (username, plant)
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS user_plant_levels (
                    username TEXT NOT NULL,
                    plant TEXT NOT NULL,
                    level INTEGER NOT NULL DEFAULT 1,
                    PRIMARY KEY (username, plant),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS user_quests (
                    username TEXT NOT NULL,
                    quest_id TEXT NOT NULL,
                    progress INTEGER NOT NULL DEFAULT 0,
                    completed INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (username, quest_id),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
                """);
    }
}
