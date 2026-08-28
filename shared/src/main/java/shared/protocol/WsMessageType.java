package shared.protocol;

public enum WsMessageType {
    // client → server
    AUTH,
    QUEUE_JOIN,
    QUEUE_LEAVE,
    INVITE,
    INVITE_ACCEPT,
    INVITE_REJECT,
    PLACE_PLANT,
    PLACE_ZOMBIE,
    BRAIN_COLLECTED,
    REPORT_NO_RESOURCES,
    QUICK_MSG,
    LOOKUP_USER,
    PING,

    // server → client
    AUTH_OK,
    AUTH_FAIL,
    ERROR,
    PRESENCE,
    QUEUE_STATUS,
    INVITE_INCOMING,
    INVITE_RESULT,
    MATCH_START,
    MATCH_STATE,
    MATCH_END,
    QUICK_MSG_RECV,
    LOOKUP_RESULT,
    PONG
}
