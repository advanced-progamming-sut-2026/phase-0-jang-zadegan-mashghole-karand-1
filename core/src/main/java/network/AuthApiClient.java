package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import shared.dto.ForgotPasswordRequest;
import shared.dto.LoginRequest;
import shared.dto.LoginResponse;
import shared.dto.RegisterRequest;
import shared.dto.ResetPasswordRequest;
import shared.dto.UserProfileDto;
import shared.protocol.Protocol;

public final class AuthApiClient {
    private final NetworkConfig config;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public AuthApiClient(NetworkConfig config) {
        this.config = config;
    }

    public boolean healthOk() {
        try {
            HttpResponse<String> res = send(HttpRequest.newBuilder()
                    .uri(URI.create(config.baseUrl() + "/health"))
                    .GET()
                    .timeout(Duration.ofSeconds(3))
                    .build());
            return res.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public LoginResponse login(String username, String password) throws IOException, InterruptedException {
        String body = Protocol.GSON.toJson(new LoginRequest(username, password));
        HttpResponse<String> res = postJson("/api/auth/login", body, null);
        return Protocol.GSON.fromJson(res.body(), LoginResponse.class);
    }

    public String register(RegisterRequest request) throws IOException, InterruptedException {
        String body = Protocol.GSON.toJson(request);
        HttpResponse<String> res = postJson("/api/auth/register", body, null);
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        if (res.statusCode() >= 200 && res.statusCode() < 300 && json.has("ok") && json.get("ok").getAsBoolean()) {
            return null;
        }
        return json.has("error") ? json.get("error").getAsString() : "REGISTER_FAILED";
    }

    public Optional<String> forgotQuestion(String username, String email) throws IOException, InterruptedException {
        String body = Protocol.GSON.toJson(new ForgotPasswordRequest(username, email));
        HttpResponse<String> res = postJson("/api/auth/forgot", body, null);
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!json.has("ok") || !json.get("ok").getAsBoolean()) {
            return Optional.empty();
        }
        if (json.has("user") && json.get("user").isJsonObject()) {
            UserProfileDto user = Protocol.GSON.fromJson(json.get("user"), UserProfileDto.class);
            return Optional.ofNullable(user.safetyQuestion);
        }
        return Optional.empty();
    }

    public String resetPassword(ResetPasswordRequest request) throws IOException, InterruptedException {
        String body = Protocol.GSON.toJson(request);
        HttpResponse<String> res = postJson("/api/auth/reset", body, null);
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        if (json.has("ok") && json.get("ok").getAsBoolean()) {
            return null;
        }
        return json.has("error") ? json.get("error").getAsString() : "RESET_FAILED";
    }

    public Optional<UserProfileDto> me(String token) throws IOException, InterruptedException {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        HttpResponse<String> res = send(HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl() + "/api/auth/me"))
                .timeout(Duration.ofSeconds(5))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build());
        if (res.statusCode() != 200) {
            return Optional.empty();
        }
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!json.has("ok") || !json.get("ok").getAsBoolean() || !json.has("user")) {
            return Optional.empty();
        }
        return Optional.of(Protocol.GSON.fromJson(json.get("user"), UserProfileDto.class));
    }

    public void logout(String token) {
        try {
            postJson("/api/auth/logout", "{}", token);
        } catch (Exception ignored) {
        }
    }

    public void saveProfile(String token, String profileJson) throws IOException, InterruptedException {
        postJson("/api/profile", profileJson == null ? "{}" : profileJson, token);
    }

    public shared.dto.RankedTodayResponse rankedToday(String token) throws IOException, InterruptedException {
        HttpResponse<String> res = getAuth(Protocol.RANKED_TODAY_PATH, token);
        return Protocol.GSON.fromJson(res.body(), shared.dto.RankedTodayResponse.class);
    }

    public shared.dto.RankedCompleteResponse rankedComplete(String token, shared.dto.RankedCompleteRequest request)
            throws IOException, InterruptedException {
        String body = Protocol.GSON.toJson(request);
        HttpResponse<String> res = postJson(Protocol.RANKED_COMPLETE_PATH, body, token);
        return Protocol.GSON.fromJson(res.body(), shared.dto.RankedCompleteResponse.class);
    }

    public shared.dto.RankedLeaderboardResponse rankedLeaderboard(String token)
            throws IOException, InterruptedException {
        HttpResponse<String> res = getAuth(Protocol.RANKED_LEADERBOARD_PATH, token);
        return Protocol.GSON.fromJson(res.body(), shared.dto.RankedLeaderboardResponse.class);
    }

    private HttpResponse<String> getAuth(String path, String token) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl() + path))
                .timeout(Duration.ofSeconds(8))
                .GET();
        if (token != null && !token.isBlank()) {
            b.header("Authorization", "Bearer " + token);
        }
        return send(b.build());
    }

    private HttpResponse<String> postJson(String path, String body, String token)
            throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl() + path))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null && !token.isBlank()) {
            b.header("Authorization", "Bearer " + token);
        }
        return send(b.build());
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
