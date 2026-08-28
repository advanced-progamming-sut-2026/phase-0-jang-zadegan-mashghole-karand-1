package network;

import model.storage.SqlStorageManager;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.SafetyQuestionType;
import model.storage.user.User;
import shared.dto.LoginResponse;
import shared.dto.RegisterRequest;
import shared.dto.ResetPasswordRequest;
import shared.dto.UserProfileDto;

public final class NetworkAuthBridge {
    private final NetworkSession session;
    private final SqlStorageManager local;

    public NetworkAuthBridge(NetworkSession session, SqlStorageManager local) {
        this.session = session;
        this.local = local;
    }

    public NetworkSession session() {
        return session;
    }

    public SqlStorageManager local() {
        return local;
    }

    public String register(String username, String password, String email, String nickname,
            Gender gender, SafetyQuestion safety) {
        try {
            if (!session.authApi().healthOk()) {
                return "SERVER_UNAVAILABLE";
            }
            RegisterRequest req = new RegisterRequest(
                    username,
                    password,
                    email,
                    nickname,
                    gender == Gender.MALE ? "male" : "female",
                    safety.type.name(),
                    safety.answer);
            String error = session.authApi().register(req);
            if (error != null) {
                return error;
            }
            ensureLocalAccount(username, password, email, nickname, gender, safety);
            return null;
        } catch (Exception e) {
            return "SERVER_UNAVAILABLE";
        }
    }

    public String login(String username, String password, boolean stayLoggedIn) {
        try {
            if (!session.authApi().healthOk()) {
                return "SERVER_UNAVAILABLE";
            }
            LoginResponse response = session.authApi().login(username, password);
            if (response == null || !response.ok || response.token == null || response.user == null) {
                return response != null && response.error != null ? response.error : "LOGIN_FAILED";
            }
            ensureLocalFromProfile(response.user, password);
            if (!local.forceLogin(username, stayLoggedIn)) {
                return "LOGIN_FAILED";
            }
            applyProfileStats(response.user);
            session.onLoginSuccess(response.token, response.user);
            return null;
        } catch (Exception e) {
            return "SERVER_UNAVAILABLE";
        }
    }

    public void logout() {
        session.logout();
        local.logout();
    }

    public String forgot(String username, String email) {
        try {
            var question = session.authApi().forgotQuestion(username, email);
            return question.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public String resetPassword(String username, String email, String answer, String newPassword) {
        try {
            ResetPasswordRequest req = new ResetPasswordRequest();
            req.username = username;
            req.email = email;
            req.safetyAnswer = answer;
            req.newPassword = newPassword;
            String error = session.authApi().resetPassword(req);
            if (error == null) {
                local.updatePassword(username, newPassword);
            }
            return error;
        } catch (Exception e) {
            return "SERVER_UNAVAILABLE";
        }
    }

    public void syncProfileUpload() {
        if (!session.isLoggedIn()) {
            return;
        }
        User user = local.getCurrentUser();
        if (user == null) {
            return;
        }
        try {
            String json = shared.protocol.Protocol.GSON.toJson(userProfileSnapshot(user));
            session.authApi().saveProfile(session.token(), json);
        } catch (Exception ignored) {
        }
    }

    private UserProfileDto userProfileSnapshot(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.username = user.username;
        dto.email = user.email;
        dto.nickname = user.nickname;
        dto.gender = user.gender == Gender.MALE ? "male" : "female";
        dto.coins = user.coins;
        dto.gems = user.gems;
        dto.highestScore = user.highestScore;
        dto.gamesPlayed = user.gamesPlayed;
        return dto;
    }

    private void ensureLocalFromProfile(UserProfileDto profile, String password) {
        Gender gender = "female".equalsIgnoreCase(profile.gender) ? Gender.FEMALE : Gender.MALE;
        SafetyQuestionType type = SafetyQuestionType.fromStored(profile.safetyQuestion);
        SafetyQuestion safety = new SafetyQuestion(type, "synced");
        ensureLocalAccount(profile.username, password, profile.email, profile.nickname, gender, safety);
    }

    private void ensureLocalAccount(String username, String password, String email, String nickname,
            Gender gender, SafetyQuestion safety) {
        if (!local.usernameExists(username)) {
            local.register(username, password, email, nickname, gender, safety);
        }
    }

    private void applyProfileStats(UserProfileDto profile) {
        User user = local.getCurrentUser();
        if (user == null || profile == null) {
            return;
        }
        user.coins = profile.coins;
        user.gems = profile.gems;
        user.highestScore = profile.highestScore;
        user.gamesPlayed = profile.gamesPlayed;
        if (profile.email != null) {
            user.email = profile.email;
        }
        if (profile.nickname != null) {
            user.nickname = profile.nickname;
        }
    }
}
