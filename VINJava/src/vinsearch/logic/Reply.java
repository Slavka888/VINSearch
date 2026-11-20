package vinsearch.logic;
import java.util.Map;
/** Meaningful polymorphic hierarchy: a message reply and a proposal reply have different payloads. */
public abstract class Reply {
    private final String status;
    protected Reply(String status) { this.status = status; }
    public String getStatus() { return status; }
    public abstract Map<String, Object> asMap();
}
