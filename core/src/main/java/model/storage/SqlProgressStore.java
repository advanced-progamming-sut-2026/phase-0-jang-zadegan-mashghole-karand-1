package model.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import model.core.Position;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.greenhouse.Pot;
import model.news.NewsItem;
import model.quest.Quest;
import model.storage.user.User;

final class SqlProgressStore {

    private final String databasePath;

    SqlProgressStore(String databasePath) {
        this.databasePath = databasePath;
    }

    void saveAll(User user) {
        saveUnlockedChapters(user);
        saveUnlockedPlants(user);
        saveUnlockedZombies(user);
        saveUnlockedMinigames(user);
        saveCompletedLevels(user);
        saveLevelHighScores(user);
        saveSeedPackets(user);
        saveGreenhousePots(user);
        saveStoredBoosts(user);
        savePlantLevels(user);
        saveQuests(user);
    }

    void saveUnlockedChapters(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
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

    void saveUnlockedMinigames(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM unlocked_minigames WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO unlocked_minigames (username, minigame) VALUES (?, ?)")) {
                for (MiniGameType minigame : user.gameProgress.getUnlockedMinigames()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, minigame.name());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save unlocked minigames", e);
        }
    }

    void saveUnlockedPlants(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
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

    void saveUnlockedZombies(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM unlocked_zombies WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO unlocked_zombies (username, zombie) VALUES (?, ?)")) {
                for (ZombieType zombie : user.collection.getUnlockedZombies()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, zombie.name());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save unlocked zombies", e);
        }
    }

    void saveCompletedLevels(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
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

    void saveLevelHighScores(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM level_high_scores WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO level_high_scores (username, level_id, score) VALUES (?, ?, ?)")) {
                for (Map.Entry<String, Integer> entry : user.gameProgress.getLevelHighScores().entrySet()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, entry.getKey());
                    insertStatement.setInt(3, entry.getValue());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save level high scores", e);
        }
    }

    void saveSeedPackets(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM user_seed_packets WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO user_seed_packets (username, plant, amount) VALUES (?, ?, ?)")) {
                for (Map.Entry<PlantType, Integer> entry : user.seedPackets.entrySet()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, entry.getKey().name());
                    insertStatement.setInt(3, entry.getValue());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save seed packets", e);
        }
    }

    void saveGreenhousePots(User user) {
        if (user == null || user.greenhouse == null) {
            return;
        }

        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM user_greenhouse_pots WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            insertGreenhousePots(connection, user);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save greenhouse pots", e);
        }
    }

    private void insertGreenhousePots(Connection connection, User user) throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement("""
                INSERT INTO user_greenhouse_pots
                (username, col, row, locked, empty, plant_class, plant_type, planted_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
            for (Pot pot : user.greenhouse.getProduction()) {
                bindGreenhousePot(insertStatement, user.username, pot);
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    private void bindGreenhousePot(PreparedStatement insertStatement, String username, Pot pot)
            throws SQLException {
        Position pos = pot.getPosition();

        insertStatement.setString(1, username);
        insertStatement.setInt(2, (int) pos.x); // col
        insertStatement.setInt(3, (int) pos.y); // row
        insertStatement.setInt(4, pot.isLocked() ? 1 : 0);
        insertStatement.setInt(5, pot.isEmpty() ? 1 : 0);
        insertStatement.setString(6, pot.getPlantClass() == null ? null : pot.getPlantClass().name());
        insertStatement.setString(7, pot.getPlantType() == null ? null : pot.getPlantType().name());
        insertStatement.setString(8, pot.getPlantedAt() == null ? null : pot.getPlantedAt().toString());
    }

    void saveStoredBoosts(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM user_stored_boosts WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO user_stored_boosts (username, plant) VALUES (?, ?)")) {
                for (PlantType plant : user.storedBoosts) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, plant.name());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save stored boosts", e);
        }
    }

    void savePlantLevels(User user) {
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM user_plant_levels WHERE username = ?")) {
                deleteStatement.setString(1, user.username);
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO user_plant_levels (username, plant, level) VALUES (?, ?, ?)")) {
                for (Map.Entry<PlantType, Integer> entry : user.plantLevels.entrySet()) {
                    insertStatement.setString(1, user.username);
                    insertStatement.setString(2, entry.getKey().name());
                    insertStatement.setInt(3, entry.getValue());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save plant levels", e);
        }
    }

    void saveQuests(User user) {
        if (user == null || user.quests == null) {
            return;
        }
        try (Connection connection = SqlConnections.open(databasePath)) {
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM user_quests WHERE username = ?")) {
                delete.setString(1, user.username);
                delete.executeUpdate();
            }
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO user_quests (username, quest_id, progress, completed) VALUES (?, ?, ?, ?)")) {
                for (Quest quest : user.quests) {
                    insert.setString(1, user.username);
                    insert.setString(2, quest.getId());
                    insert.setInt(3, quest.getProgress());
                    insert.setInt(4, quest.isCompleted() ? 1 : 0);
                    insert.addBatch();
                }
                insert.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save quests", e);
        }
    }

    void insertNewsItem(String username, NewsItem item) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO user_news (username, news_id, message, is_read, created_at) "
                                + "VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, item.getId());
            statement.setString(3, item.getMessage());
            statement.setInt(4, item.isRead() ? 1 : 0);
            statement.setString(5, item.getTimestamp().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save news item", e);
        }
    }

    void markAllNewsRead(String username) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE user_news SET is_read = 1 WHERE username = ? AND is_read = 0")) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark news as read", e);
        }
    }

    void persistCompletedLevelId(String username, String levelId) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR IGNORE INTO completed_levels (username, level_id) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, levelId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark level completed", e);
        }
    }

    void persistLevelHighScore(String username, String levelId, int score) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO level_high_scores (username, level_id, score)
                        VALUES (?, ?, ?)
                        ON CONFLICT(username, level_id) DO UPDATE SET score = excluded.score
                        """)) {
            statement.setString(1, username);
            statement.setString(2, levelId);
            statement.setInt(3, score);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save level high score", e);
        }
    }

    void insertUnlockedMinigame(String username, MiniGameType minigame) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR IGNORE INTO unlocked_minigames (username, minigame) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, minigame.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unlock minigame", e);
        }
    }

    void insertUnlockedChapter(String username, ChapterType chapter) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR IGNORE INTO unlocked_chapters (username, chapter) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, chapter.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unlock chapter", e);
        }
    }

    void insertUnlockedPlant(String username, PlantType plant) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR IGNORE INTO unlocked_plants (username, plant) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, plant.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unlock plant", e);
        }
    }

    void insertUnlockedZombie(String username, ZombieType zombie) {
        try (Connection connection = SqlConnections.open(databasePath);
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR IGNORE INTO unlocked_zombies (username, zombie) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, zombie.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unlock zombie", e);
        }
    }
}
