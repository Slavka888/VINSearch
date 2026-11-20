package vinsearch;
import vinsearch.interfaces.OfferSortRule;
import vinsearch.logic.*;

import java.nio.file.Path;
/** CLI compatible with Python's --request; extra --ranking switches the strategy. */
public class Main {
    public static void main(String[] args) throws Exception {
        String request = "R-001"; Path dataPath = Path.of("VINJava/data.json");
        String ranking = "price";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--request" -> { if (++i == args.length) throw new IllegalArgumentException("Missing request ID"); request = args[i]; }
                case "--data" -> { if (++i == args.length) throw new IllegalArgumentException("Missing data path"); dataPath = Path.of(args[i]); }
                case "--ranking" -> { if (++i == args.length) throw new IllegalArgumentException("Missing ranking mode"); ranking = args[i]; }
                default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        OfferSortRule rule = switch (ranking) {
            case "price" -> new PriceFirstRule();
            case "delivery" -> new FastestFirstRule();
            default -> throw new IllegalArgumentException("--ranking must be price or delivery");
        };
        DataStore data = DataStore.load(dataPath);
        Reply reply = new RequestProcessor(data, rule).prepareReply(request);
        System.out.println(Json.stringify(reply.asMap()));
    }
}
