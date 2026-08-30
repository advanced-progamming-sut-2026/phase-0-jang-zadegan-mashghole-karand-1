package server.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private final String jdbcUrl;

    public Database(String path) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        this.jdbcUrl = "jdbc:sqlite:" + path;
        initSchema();
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void initSchema() {
        try (Connection c = open(); Statement s = c.createStatement()) {
            s.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username TEXT PRIMARY KEY,
                        password_hash TEXT NOT NULL,
                        email TEXT NOT NULL,
                        nickname TEXT NOT NULL,
                        gender TEXT NOT NULL,
                        safety_question TEXT NOT NULL,
                        safety_answer_hash TEXT NOT NULL,
                        coins INTEGER NOT NULL DEFAULT 0,
                        gems INTEGER NOT NULL DEFAULT 0,
                        highest_score INTEGER NOT NULL DEFAULT 0,
                        games_played INTEGER NOT NULL DEFAULT 0,
                        izombie_wins INTEGER NOT NULL DEFAULT 0,
                        profile_json TEXT,
                        created_at INTEGER NOT NULL
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS sessions (
                        token TEXT PRIMARY KEY,
                        username TEXT NOT NULL,
                        created_at INTEGER NOT NULL,
                        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                    )
                    """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init server DB", e);
        }
    }
}
