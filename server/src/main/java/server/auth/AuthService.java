package server.auth;

import java.util.Optional;

import server.db.UserRepository;
import server.db.UserRepository.StoredUser;
import shared.auth.AuthRules;
import shared.dto.ApiError;
import shared.dto.ForgotPasswordRequest;
import shared.dto.LoginRequest;
import shared.dto.LoginResponse;
import shared.dto.RegisterRequest;
import shared.dto.ResetPasswordRequest;
import shared.dto.UserProfileDto;

public final class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
        seedDemoUser();
    }

    private void seedDemoUser() {
        if (users.usernameExists("player")) {
            return;
        }
        users.insertUser(
                "player",
                PasswordHasher.hash("Password1!"),
                "player@example.com",
                "Player",
                "male",
                "FIRST_PET",
                PasswordHasher.hash("fluffy"));
    }

    public Object register(RegisterRequest req) {
        if (req == null) {
            return ApiError.of("INVALID_REQUEST");
        }
        String err = AuthRules.validateUsername(req.username);
        if (err != null) {
            return ApiError.of(err);
        }
        err = AuthRules.validatePassword(req.password);
        if (err != null) {
            return ApiError.of(err);
        }
        err = AuthRules.validateNickname(req.nickname);
        if (err != null) {
            return ApiError.of(err);
        }
        err = AuthRules.validateEmail(req.email);
        if (err != null) {
            return ApiError.of(err);
        }
        err = AuthRules.validateGender(req.gender);
        if (err != null) {
            return ApiError.of(err);
        }
        if (req.safetyQuestion == null || req.safetyQuestion.isBlank()
                || req.safetyAnswer == null || req.safetyAnswer.isBlank()) {
            return ApiError.of("INVALID_SECURITY_ANSWER");
        }
        if (users.usernameExists(req.username)) {
            return ApiError.of("USERNAME_TAKEN");
        }
        users.insertUser(
                req.username,
                PasswordHasher.hash(req.password),
                req.email,
                req.nickname,
                AuthRules.parseGender(req.gender),
                req.safetyQuestion.trim().toUpperCase(),
                PasswordHasher.hash(req.safetyAnswer));
        return LoginResponse.success(null, null);
    }

    public LoginResponse login(LoginRequest req) {
        if (req == null || req.username == null || req.password == null) {
            return LoginResponse.fail("LOGIN_FAILED");
        }
        Optional<StoredUser> found = users.findByUsername(req.username);
        if (found.isEmpty() || !PasswordHasher.verify(req.password, found.get().passwordHash)) {
            return LoginResponse.fail("LOGIN_FAILED");
        }
        String token = users.createSession(req.username);
        return LoginResponse.success(token, users.toDto(found.get()));
    }

    public Object forgot(ForgotPasswordRequest req) {
        if (req == null) {
            return ApiError.of("INVALID_SECURITY_ANSWER");
        }
        Optional<StoredUser> found = users.findByUsername(req.username);
        if (found.isEmpty() || found.get().email == null
                || !found.get().email.equalsIgnoreCase(req.email)) {
            return ApiError.of("INVALID_SECURITY_ANSWER");
        }
        UserProfileDto dto = new UserProfileDto();
        dto.username = found.get().username;
        dto.safetyQuestion = found.get().safetyQuestion;
        LoginResponse ok = new LoginResponse();
        ok.ok = true;
        ok.user = dto;
        return ok;
    }

    public Object reset(ResetPasswordRequest req) {
        if (req == null) {
            return ApiError.of("INVALID_SECURITY_ANSWER");
        }
        String err = AuthRules.validatePassword(req.newPassword);
        if (err != null) {
            return ApiError.of(err);
        }
        Optional<StoredUser> found = users.findByUsername(req.username);
        if (found.isEmpty() || found.get().email == null
                || !found.get().email.equalsIgnoreCase(req.email)
                || !PasswordHasher.verify(req.safetyAnswer, found.get().safetyAnswerHash)) {
            return ApiError.of("INVALID_SECURITY_ANSWER");
        }
        users.updatePassword(req.username, PasswordHasher.hash(req.newPassword));
        LoginResponse ok = new LoginResponse();
        ok.ok = true;
        return ok;
    }

    public Optional<UserProfileDto> me(String token) {
        return users.usernameForToken(token)
                .flatMap(users::findByUsername)
                .map(users::toDto);
    }

    public Optional<String> usernameForToken(String token) {
        return users.usernameForToken(token);
    }

    public void logout(String token) {
        users.deleteSession(token);
    }

    public void saveProfile(String username, String profileJson) {
        users.saveProfileJson(username, profileJson);
    }

    public UserRepository users() {
        return users;
    }
}
