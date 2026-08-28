package server;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.javalin.Javalin;
import server.auth.AuthService;
import server.db.Database;
import server.db.UserRepository;
import server.http.AuthRoutes;
import server.match.MatchmakingService;
import server.match.PresenceHub;
import server.ws.GameWebSocket;
import shared.protocol.Protocol;

public final class ServerMain {
    public static void main(String[] args) {
        ServerConfig config = ServerConfig.fromEnv();
        Database database = new Database(config.databasePath);
        UserRepository users = new UserRepository(database);
        AuthService auth = new AuthService(users);

        PresenceHub presence = new PresenceHub();
        presence.setUserExistsCheck(users::usernameExists);
        MatchmakingService matchmaking = new MatchmakingService(presence);
        matchmaking.setStats(users);

        Javalin app = Javalin.create(cfg -> {
            cfg.bundledPlugins.enableCors(cors -> cors.addRule(rule -> rule.anyHost()));
            cfg.showJavalinBanner = false;
        });

        new AuthRoutes(auth).register(app);
        new GameWebSocket(auth, presence, matchmaking).register(app);

        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "invite-expiry");
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(matchmaking::expireStaleInvites, 5, 5, TimeUnit.SECONDS);

        app.start(config.httpPort);
        System.out.println("PVZ server listening on http://localhost:" + config.httpPort
                + " ws://localhost:" + config.httpPort + Protocol.WS_PATH);
        System.out.println("DB: " + config.databasePath);
    }
}
