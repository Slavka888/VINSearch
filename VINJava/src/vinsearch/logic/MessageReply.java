package vinsearch.logic;
import vinsearch.logic_classes.Reply;

import java.util.LinkedHashMap;
import java.util.Map;
/** Error, clarification and unavailable responses contain a status and message only. */
public final class MessageReply extends Reply {
    private final String message;
    public MessageReply(String status, String message) {
        super(status);
        this.message = message;
    }
    public String getMessage() { return message; }
    @Override public Map<String, Object> asMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", getStatus());
        result.put("message", message);
        return result;
    }
}
