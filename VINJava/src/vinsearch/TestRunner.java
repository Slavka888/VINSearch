package vinsearch;
import vinsearch.logic_classes.DataStore;
import vinsearch.logic_classes.FastestFirstRule;
import vinsearch.logic_classes.PriceFirstRule;
import vinsearch.logic_classes.RequestProcessor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
/** No external test framework: executable integration and algorithm checks. */
public final class TestRunner {
    private static int tests;
    private static void check(boolean ok, String message) {
        tests++;
        if (!ok) throw new AssertionError(message);
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Path path = Path.of(args.length == 0 ? "VINJava/data.json" : args[0]);
        DataStore data = DataStore.load(path);
        RequestProcessor processor = new RequestProcessor(data, new PriceFirstRule());
        Map<String,Object> a = processor.prepareReply("R-001").asMap();
        check("review".equals(a.get("status")), "R-001 review");
        List<Map<String,Object>> choices = (List<Map<String,Object>>)a.get("choices");
        check(choices.size() == 2, "R-001 has 2 offers");
        check("DEMO-F102".equals(choices.get(0).get("article")), "R-001 cheaper first");
        check(((Number)choices.get(0).get("price")).intValue() == 1100, "R-001 price 1100");
        check("clarification".equals(processor.prepareReply("R-002").getStatus()), "R-002 validation");
        check("unavailable".equals(processor.prepareReply("R-003").getStatus()), "R-003 unavailable");
        Map<String,Object> d = processor.prepareReply("R-004").asMap();
        check("review".equals(d.get("status")), "R-004 review");
        List<Map<String,Object>> analogs = (List<Map<String,Object>>)d.get("choices");
        check(analogs.size() == 1 && Boolean.TRUE.equals(analogs.get(0).get("is_analog")), "R-004 analog only");
        check("DEMO-D202".equals(analogs.get(0).get("article")), "R-004 article");
        check("error".equals(processor.prepareReply("R-404").getStatus()), "Unknown request");
        RequestProcessor fastest = new RequestProcessor(data, new FastestFirstRule());
        List<Map<String,Object>> fast = (List<Map<String,Object>>)fastest.prepareReply("R-001").asMap().get("choices");
        check("DEMO-F101".equals(fast.get(0).get("article")), "Alternative strategy prefers delivery");
        check(data.getSelections().isEmpty(), "Empty selections represented by model entity");
        System.out.println("OK: " + tests + " tests passed");
    }
}
