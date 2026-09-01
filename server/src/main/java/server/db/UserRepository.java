package server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import shared.dto.UserProfileDto;

public final class UserRepository {
    private final Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

    public boolean usernameExists(String username) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void upsertAdmin(String username, String passwordHash, String email, String nickname,
            String gender, String safetyQuestion, String safetyAnswerHash, int coins, int gems) {
        String sql = """
                INSERT INTO users (username, password_hash, email, nickname, gender,
                    safety_question, safety_answer_hash, coins, gems, highest_score,
                    games_played, izombie_wins, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, ?)
                ON CONFLICT(username) DO UPDATE SET
                    password_hash = excluded.password_hash,
                    email = excluded.email,
                    nickname = excluded.nickname,
                    gender = excluded.gender,
                    safety_question = excluded.safety_question,
                    safety_answer_hash = excluded.safety_answer_hash,
                    coins = excluded.coins,
                    gems = excluded.gems
                """;
        try (Connection c = database.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, email);
            ps.setString(4, nickname);
            ps.setString(5, gender);
            ps.setString(6, safetyQuestion);
            ps.setString(7, safetyAnswerHash);
            ps.setInt(8, coins);
            ps.setInt(9, gems);
            ps.setLong(10, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertUser(String username, String passwordHash, String email, String nickname,
            String gender, String safetyQuestion, String safetyAnswerHash) {
        String sql = """
                INSERT INTO users (username, password_hash, email, nickname, gender,
                    safety_question, safety_answer_hash, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = database.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, email);
            ps.setString(4, nickname);
            ps.setString(5, gender);
            ps.setString(6, safetyQuestion);
            ps.setString(7, safetyAnswerHash);
            ps.setLong(8, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<StoredUser> findByUsername(String username) {
        String sql = """
                SELECT username, password_hash, email, nickname, gender, safety_question,
                       safety_answer_hash, coins, gems, highest_score, games_played,
                       izombie_wins, profile_json, ranked_last_played_date
                FROM users WHERE username = ?
                """;
        try (Connection c = database.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(String username, String passwordHash) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE users SET password_hash = ? WHERE username = ?")) {
            ps.setString(1, passwordHash);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProfileJson(String username, String profileJson) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE users SET profile_json = ? WHERE username = ?")) {
            ps.setString(1, profileJson);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void recordGamePlayed(String username) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE users SET games_played = games_played + 1 WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void recordIZombieWin(String username) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE users SET izombie_wins = izombie_wins + 1, "
                                + "games_played = games_played + 1 WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getRankedLastPlayedDate(String username) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT ranked_last_played_date FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                String date = rs.getString(1);
                if (date == null || date.isBlank()) {
                    return Optional.empty();
                }
                return Optional.of(date);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Marks ranked day as played. On win, bumps highest_score when score is higher.
     * @return newRecord whether highest_score increased
     */
    public boolean markRankedPlayed(String username, String utcDate, boolean won, int score) {
        Optional<StoredUser> found = findByUsername(username);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("USER_NOT_FOUND");
        }
        StoredUser user = found.get();
        boolean newRecord = false;
        int highest = user.highestScore;
        if (won && score > highest) {
            highest = score;
            newRecord = true;
        }
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement("""
                        UPDATE users SET ranked_last_played_date = ?, highest_score = ?
                        WHERE username = ?
                        """)) {
            ps.setString(1, utcDate);
            ps.setInt(2, highest);
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newRecord;
    }

    public int getHighestScore(String username) {
        return findByUsername(username).map(u -> u.highestScore).orElse(0);
    }

    public java.util.List<shared.dto.RankedLeaderboardEntry> listByHighestScore(int limit) {
        int capped = Math.max(1, Math.min(limit, 100));
        String sql = """
                SELECT username, highest_score FROM users
                ORDER BY highest_score DESC, username COLLATE NOCASE ASC
                LIMIT ?
                """;
        java.util.List<shared.dto.RankedLeaderboardEntry> entries = new java.util.ArrayList<>();
        try (Connection c = database.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, capped);
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    entries.add(new shared.dto.RankedLeaderboardEntry(
                            rank++,
                            rs.getString("username"),
                            rs.getInt("highest_score")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }

    public String createSession(String username) {
        String token = UUID.randomUUID().toString();
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO sessions (token, username, created_at) VALUES (?, ?, ?)")) {
            ps.setString(1, token);
            ps.setString(2, username);
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
            return token;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> usernameForToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT username FROM sessions WHERE token = ?")) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSession(String token) {
        try (Connection c = database.open();
                PreparedStatement ps = c.prepareStatement("DELETE FROM sessions WHERE token = ?")) {
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserProfileDto toDto(StoredUser user) {
        UserProfileDto dto = new UserProfileDto();
        dto.username = user.username;
        dto.email = user.email;
        dto.nickname = user.nickname;
        dto.gender = user.gender;
        dto.safetyQuestion = user.safetyQuestion;
        dto.coins = user.coins;
        dto.gems = user.gems;
        dto.highestScore = user.highestScore;
        dto.gamesPlayed = user.gamesPlayed;
        dto.izombieWins = user.izombieWins;
        dto.profileJson = user.profileJson;
        return dto;
    }

    private static StoredUser map(ResultSet rs) throws SQLException {
        StoredUser u = new StoredUser();
        u.username = rs.getString("username");
        u.passwordHash = rs.getString("password_hash");
        u.email = rs.getString("email");
        u.nickname = rs.getString("nickname");
        u.gender = rs.getString("gender");
        u.safetyQuestion = rs.getString("safety_question");
        u.safetyAnswerHash = rs.getString("safety_answer_hash");
        u.coins = rs.getInt("coins");
        u.gems = rs.getInt("gems");
        u.highestScore = rs.getInt("highest_score");
        u.gamesPlayed = rs.getInt("games_played");
        u.izombieWins = rs.getInt("izombie_wins");
        u.profileJson = rs.getString("profile_json");
        try {
            u.rankedLastPlayedDate = rs.getString("ranked_last_played_date");
        } catch (SQLException ignored) {
            u.rankedLastPlayedDate = null;
        }
        return u;
    }

    public static final class StoredUser {
        public String username;
        public String passwordHash;
        public String email;
        public String nickname;
        public String gender;
        public String safetyQuestion;
        public String safetyAnswerHash;
        public int coins;
        public int gems;
        public int highestScore;
        public int gamesPlayed;
        public int izombieWins;
        public String profileJson;
        public String rankedLastPlayedDate;
    }
}
