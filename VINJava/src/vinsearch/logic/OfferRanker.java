package vinsearch.logic;
import vinsearch.interfaces.OfferSortRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/** KT2's rank_offers(): sort IDs, binary search offer parts, filter stock, sort by policy. */
public final class OfferRanker {
    private final OfferSortRule rule;
    public OfferRanker(OfferSortRule rule) { this.rule = rule; }
    public List<Offer> rankOffers(List<CompatiblePart> compatibleParts, List<Offer> offers) {
        List<String> ids = new ArrayList<>();
        for (CompatiblePart part : compatibleParts) ids.add(part.part().getPartId());
        Collections.sort(ids);
        List<Offer> candidates = new ArrayList<>();
        for (Offer offer : offers) {
            if (Collections.binarySearch(ids, offer.getPartId()) >= 0 && offer.getStock() > 0) {
                candidates.add(offer);
            }
        }
        candidates.sort(rule.comparator());
        return candidates;
    }
}
