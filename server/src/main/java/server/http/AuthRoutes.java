package server.http;

import io.javalin.Javalin;
import io.javalin.http.Context;
import server.auth.AuthService;
import shared.dto.ApiError;
import shared.dto.ForgotPasswordRequest;
import shared.dto.LoginRequest;
import shared.dto.LoginResponse;
import shared.dto.RegisterRequest;
import shared.dto.ResetPasswordRequest;

public final class AuthRoutes {
    private final AuthService auth;

    public AuthRoutes(AuthService auth) {
        this.auth = auth;
    }

    public void register(Javalin app) {
        app.get("/health", ctx -> ctx.json(java.util.Map.of("ok", true, "service", "pvz-server")));
        registerAuthRoutes(app);
        registerProfileRoutes(app);
        registerCatalogRoutes(app);
    }

    private void registerAuthRoutes(Javalin app) {
        app.post("/api/auth/register", ctx -> {
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
            Object result = auth.register(req);
            if (result instanceof ApiError) {
                ctx.status(400).json(result);
                return;
            }
            ctx.status(201).json(java.util.Map.of("ok", true));
        });

        app.post("/api/auth/login", ctx -> {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
            LoginResponse result = auth.login(req);
            ctx.status(result.ok ? 200 : 401).json(result);
        });

        app.post("/api/auth/forgot", ctx -> {
            ForgotPasswordRequest req = ctx.bodyAsClass(ForgotPasswordRequest.class);
            Object result = auth.forgot(req);
            writeResult(ctx, result);
        });

        app.post("/api/auth/reset", ctx -> {
            ResetPasswordRequest req = ctx.bodyAsClass(ResetPasswordRequest.class);
            Object result = auth.reset(req);
            writeResult(ctx, result);
        });

        app.get("/api/auth/me", ctx -> {
            String token = bearer(ctx);
            var me = auth.me(token);
            if (me.isEmpty()) {
                ctx.status(401).json(ApiError.of("UNAUTHORIZED"));
                return;
            }
            ctx.json(java.util.Map.of("ok", true, "user", me.get()));
        });

        app.post("/api/auth/logout", ctx -> {
            String token = bearer(ctx);
            auth.logout(token);
            ctx.json(java.util.Map.of("ok", true));
        });
    }

    private void registerProfileRoutes(Javalin app) {
        app.put("/api/profile", ctx -> {
            String token = bearer(ctx);
            var username = auth.usernameForToken(token);
            if (username.isEmpty()) {
                ctx.status(401).json(ApiError.of("UNAUTHORIZED"));
                return;
            }
            String body = ctx.body();
            auth.saveProfile(username.get(), body);
            ctx.json(java.util.Map.of("ok", true));
        });
    }

    private void registerCatalogRoutes(Javalin app) {
        app.get("/api/messages/catalog", ctx -> {
            ctx.json(java.util.Map.of("ok", true, "messages", shared.message.QuickMessageId.catalog()));
        });
    }

    private static void writeResult(Context ctx, Object result) {
        if (result instanceof ApiError) {
            ctx.status(400).json(result);
        } else {
            ctx.json(result);
        }
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
