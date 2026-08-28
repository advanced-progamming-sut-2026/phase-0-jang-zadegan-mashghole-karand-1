package shared.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class Protocol {
    public static final Gson GSON = new Gson();
    public static final int DEFAULT_HTTP_PORT = 8080;
    public static final String WS_PATH = "/ws";
    public static final int IZOMBIE_SURVIVAL_SECONDS = 120;
    public static final int INVITE_TIMEOUT_MS = 30_000;
    public static final int QUICK_MSG_RATE_LIMIT_MS = 1_500;

    private Protocol() {
    }

    public static String envelope(WsMessageType type, Object payload) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type.name());
        if (payload != null) {
            root.add("payload", GSON.toJsonTree(payload));
        }
        return GSON.toJson(root);
    }

    public static ParsedMessage parse(String raw) {
        JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
        String typeName = root.get("type").getAsString();
        WsMessageType type = WsMessageType.valueOf(typeName);
        JsonObject payload = root.has("payload") && root.get("payload").isJsonObject()
                ? root.getAsJsonObject("payload")
                : new JsonObject();
        return new ParsedMessage(type, payload);
    }

    public static final class ParsedMessage {
        public final WsMessageType type;
        public final JsonObject payload;

        public ParsedMessage(WsMessageType type, JsonObject payload) {
            this.type = type;
            this.payload = payload;
        }

        public <T> T as(Class<T> clazz) {
            return GSON.fromJson(payload, clazz);
        }

        public String getString(String key) {
            return payload.has(key) ? payload.get(key).getAsString() : null;
        }

        public int getInt(String key, int defaultValue) {
            return payload.has(key) ? payload.get(key).getAsInt() : defaultValue;
        }
    }
}
