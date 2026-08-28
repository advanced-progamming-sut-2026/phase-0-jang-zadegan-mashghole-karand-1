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
                       izombie_wins, profile_json
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
                        "UPDATE users SET izombie_wins = izombie_wins + 1, games_played = games_played + 1 WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    }
}
