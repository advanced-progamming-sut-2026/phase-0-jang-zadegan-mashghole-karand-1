package model.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.core.Position;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.news.NewsItem;
import model.quest.Quest;
import model.storage.user.User;

final class SqlProgressLoader {

    void loadAll(Connection connection, User user) throws SQLException {
        loadUnlockedChapters(connection, user);
        loadUnlockedPlants(connection, user);
        loadUnlockedZombies(connection, user);
        loadUnlockedMinigames(connection, user);
        loadCompletedLevels(connection, user);
        loadLevelHighScores(connection, user);
        loadNews(connection, user);
        loadSeedPackets(connection, user);
        loadGreenhousePots(connection, user);
        loadStoredBoosts(connection, user);
        loadPlantLevels(connection, user);
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

    private void loadUnlockedZombies(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT zombie FROM unlocked_zombies WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ZombieType zombie = ZombieType.valueOf(resultSet.getString("zombie"));
                    user.collection.unlockZombie(zombie);
                }
            }
        }
    }

    private void loadUnlockedMinigames(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT minigame FROM unlocked_minigames WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    user.gameProgress.unlockMinigame(MiniGameType.valueOf(resultSet.getString("minigame")));
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

    private void loadLevelHighScores(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT level_id, score FROM level_high_scores WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    user.gameProgress.setLevelHighScore(
                            resultSet.getString("level_id"),
                            resultSet.getInt("score"));
                }
            }
        }
    }

    private void loadNews(Connection connection, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT news_id, message, is_read, created_at FROM user_news "
                        + "WHERE username = ? ORDER BY created_at DESC")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<NewsItem> items = new ArrayList<>();
                while (resultSet.next()) {
                    items.add(new NewsItem(
                            resultSet.getString("news_id"),
                            resultSet.getString("message"),
                            resultSet.getInt("is_read") == 1,
                            LocalDateTime.parse(resultSet.getString("created_at"))));
                }
                user.newsFeed.replaceItems(items);
            }
        }
    }

    private void loadSeedPackets(Connection connection, User user) throws SQLException {
        user.seedPackets.clear();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT plant, amount FROM user_seed_packets WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String plant = resultSet.getString("plant");
                    int amount = resultSet.getInt("amount");
                    if (plant == null || plant.isBlank()) {
                        continue;
                    }
                    user.seedPackets.put(PlantType.valueOf(plant), amount);
                }
            }
        }
    }

    private void loadGreenhousePots(Connection connection, User user) throws SQLException {
        user.greenhouse = new Greenhouse();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT col, row, locked, empty, plant_class, plant_type, planted_at " +
                        "FROM user_greenhouse_pots WHERE username = ?")) {
            ps.setString(1, user.username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applyGreenhouseRow(rs, user);
                }
            }
        }
    }

    private void applyGreenhouseRow(ResultSet rs, User user) throws SQLException {
        Position pos = new Position(rs.getInt("col"), rs.getInt("row"));
        Pot pot = user.greenhouse.getPot(pos);
        if (pot == null) {
            return;
        }

        if (rs.getInt("locked") == 0) {
            pot.setUnlocked();
        }

        if (rs.getInt("empty") == 1) {
            return;
        }

        String plantClassStr = rs.getString("plant_class");
        String plantTypeStr = rs.getString("plant_type");
        String plantedAtStr = rs.getString("planted_at");

        Pot.PlantClass plantClass = Pot.PlantClass.valueOf(plantClassStr);
        PlantType plantType = plantTypeStr == null ? null : PlantType.valueOf(plantTypeStr);

        pot.plant(plantClass, plantType);
        if (plantedAtStr != null && !plantedAtStr.isBlank()) {
            pot.setPlantedAt(LocalDateTime.parse(plantedAtStr));
        }
    }

    private void loadStoredBoosts(Connection connection, User user) throws SQLException {
        user.storedBoosts.clear();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT plant FROM user_stored_boosts WHERE username = ?")) {
            statement.setString(1, user.username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String plant = resultSet.getString("plant");
                    if (plant == null || plant.isBlank()) {
                        continue;
                    }
                    user.storedBoosts.add(PlantType.valueOf(plant));
                }
            }
        }
    }

    private void loadPlantLevels(Connection connection, User user) throws SQLException {
        user.plantLevels.clear();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT plant, level FROM user_plant_levels WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String plant = resultSet.getString("plant");
                    int level = resultSet.getInt("level");
                    user.plantLevels.put(PlantType.valueOf(plant), level);
                }
            }
        }
    }

    void loadQuests(Connection connection, User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT quest_id, progress, completed FROM user_quests WHERE username = ?")) {
            statement.setString(1, user.username);
            try (ResultSet rs = statement.executeQuery()) {
                Map<String, int[]> saved = new HashMap<>();
                while (rs.next()) {
                    saved.put(rs.getString("quest_id"),
                            new int[] { rs.getInt("progress"), rs.getInt("completed") });
                }
                for (Quest quest : user.quests) {
                    int[] row = saved.get(quest.getId());
                    if (row != null) {
                        quest.setProgress(row[0]);
                        quest.setCompleted(row[1] == 1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load quests", e);
        }
    }
}
