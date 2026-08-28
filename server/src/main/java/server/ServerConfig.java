package server;

public final class ServerConfig {
    public final int httpPort;
    public final String databasePath;

    public ServerConfig(int httpPort, String databasePath) {
        this.httpPort = httpPort;
        this.databasePath = databasePath;
    }

    public static ServerConfig fromEnv() {
        int port = shared.protocol.Protocol.DEFAULT_HTTP_PORT;
        String portEnv = System.getenv("PVZ_SERVER_PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            port = Integer.parseInt(portEnv.trim());
        }
        String db = System.getenv("PVZ_SERVER_DB");
        if (db == null || db.isBlank()) {
            db = "data/server.db";
        }
        return new ServerConfig(port, db);
    }
}
