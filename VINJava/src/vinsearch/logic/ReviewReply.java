package vinsearch.logic;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Proposal response: unlike MessageReply, carries the VIN and priced choices. */
public final class ReviewReply extends Reply {
    private final String vin;
    private final List<Map<String, Object>> choices;
    private final String message;
    public ReviewReply(String vin, List<Map<String, Object>> choices, String message) {
        super("review");
        this.vin = vin;
        this.choices = List.copyOf(choices);
        this.message = message;
    }
    public String getVin() { return vin; }
    public List<Map<String, Object>> getChoices() { return choices; }
    public String getMessage() { return message; }
    @Override public Map<String, Object> asMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", getStatus());
        result.put("vin", vin);
        result.put("choices", choices);
        result.put("message", message);
        return result;
    }
}
