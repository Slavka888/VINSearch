package vinsearch.logic;
import vinsearch.interfaces.OfferSortRule;

import java.util.Comparator;
/** This ordering exactly matches the original Python rank_offers(). */
public final class PriceFirstRule implements OfferSortRule {
    @Override public Comparator<Offer> comparator() {
        return Comparator.comparingInt(Offer::getPrice)
                .thenComparingInt(Offer::getDeliveryDays)
                .thenComparing(Offer::getOfferId);
    }
    @Override public String name() { return "price"; }
}
