package network;

import shared.protocol.Protocol;

public final class NetworkConfig {
    public static final String DEFAULT_BASE_URL = "http://127.0.0.1:" + Protocol.DEFAULT_HTTP_PORT;

    private final String baseUrl;

    public NetworkConfig(String baseUrl) {
        this.baseUrl = baseUrl == null || baseUrl.isBlank() ? DEFAULT_BASE_URL : baseUrl.replaceAll("/$", "");
    }

    public static NetworkConfig fromEnv() {
        String url = System.getenv("PVZ_SERVER_URL");
        if (url == null || url.isBlank()) {
            url = System.getProperty("pvz.serverUrl", DEFAULT_BASE_URL);
        }
        return new NetworkConfig(url);
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String wsUrl() {
        String http = baseUrl;
        if (http.startsWith("https://")) {
            return "wss://" + http.substring("https://".length()) + Protocol.WS_PATH;
        }
        if (http.startsWith("http://")) {
            return "ws://" + http.substring("http://".length()) + Protocol.WS_PATH;
        }
        return "ws://" + http + Protocol.WS_PATH;
    }
}
