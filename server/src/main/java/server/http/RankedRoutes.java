package server.http;

import java.time.LocalDate;
import java.time.ZoneOffset;

import io.javalin.Javalin;
import io.javalin.http.Context;
import server.auth.AuthService;
import server.db.UserRepository;
import shared.dto.ApiError;
import shared.dto.RankedChallengeDto;
import shared.dto.RankedCompleteRequest;
import shared.dto.RankedCompleteResponse;
import shared.dto.RankedLeaderboardResponse;
import shared.dto.RankedTodayResponse;
import shared.protocol.Protocol;
import shared.ranked.RankedChallengeGenerator;

public final class RankedRoutes {
    private final AuthService auth;
    private final UserRepository users;

    public RankedRoutes(AuthService auth, UserRepository users) {
        this.auth = auth;
        this.users = users;
    }

    public void register(Javalin app) {
        app.get(Protocol.RANKED_TODAY_PATH, this::today);
        app.post(Protocol.RANKED_COMPLETE_PATH, this::complete);
        app.get(Protocol.RANKED_LEADERBOARD_PATH, this::leaderboard);
    }

    private void today(Context ctx) {
        String username = requireUser(ctx);
        if (username == null) {
            return;
        }
        LocalDate utcToday = LocalDate.now(ZoneOffset.UTC);
        RankedChallengeDto challenge = RankedChallengeGenerator.forUtcDate(utcToday);
        boolean alreadyPlayed = users.getRankedLastPlayedDate(username)
                .map(d -> d.equals(utcToday.toString()))
                .orElse(false);
        int highest = users.getHighestScore(username);
        ctx.json(RankedTodayResponse.success(challenge, alreadyPlayed, highest));
    }

    private void complete(Context ctx) {
        String username = requireUser(ctx);
        if (username == null) {
            return;
        }
        RankedCompleteRequest req = ctx.bodyAsClass(RankedCompleteRequest.class);
        if (req == null || req.date == null || req.date.isBlank()) {
            ctx.status(400).json(RankedCompleteResponse.fail("BAD_REQUEST"));
            return;
        }
        LocalDate utcToday = LocalDate.now(ZoneOffset.UTC);
        if (!req.date.equals(utcToday.toString())) {
            ctx.status(400).json(RankedCompleteResponse.fail("DATE_MISMATCH"));
            return;
        }
        if (users.getRankedLastPlayedDate(username).map(d -> d.equals(utcToday.toString())).orElse(false)) {
            ctx.status(409).json(RankedCompleteResponse.fail("ALREADY_PLAYED"));
            return;
        }
        if (req.won && req.score <= 0) {
            ctx.status(400).json(RankedCompleteResponse.fail("INVALID_SCORE"));
            return;
        }
        boolean newRecord = users.markRankedPlayed(username, utcToday.toString(), req.won, req.score);
        int highest = users.getHighestScore(username);
        ctx.json(RankedCompleteResponse.success(highest, newRecord));
    }

    private void leaderboard(Context ctx) {
        String username = requireUser(ctx);
        if (username == null) {
            return;
        }
        ctx.json(RankedLeaderboardResponse.success(users.listByHighestScore(50)));
    }

    private String requireUser(Context ctx) {
        String token = bearer(ctx);
        var user = auth.usernameForToken(token);
        if (user.isEmpty()) {
            ctx.status(401).json(ApiError.of("UNAUTHORIZED"));
            return null;
        }
        return user.get();
    }

    private static String bearer(Context ctx) {
        String header = ctx.header("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length()).trim();
        }
        String q = ctx.queryParam("token");
        return q != null ? q : "";
    }
}
