package vinsearch.logic;
import vinsearch.interfaces.OfferSortRule;
import vinsearch.logic_classes.Offer;

import java.util.Comparator;
/** Example of extension: prioritize delivery speed instead of price. */
public final class FastestFirstRule implements OfferSortRule {
    @Override public Comparator<Offer> comparator() {
        return Comparator.comparingInt(Offer::getDeliveryDays)
                .thenComparingInt(Offer::getPrice)
                .thenComparing(Offer::getOfferId);
    }
    @Override public String name() { return "delivery"; }
}
