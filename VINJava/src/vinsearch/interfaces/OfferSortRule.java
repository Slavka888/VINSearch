package vinsearch.interfaces;
import vinsearch.logic_classes.Offer;

import java.util.Comparator;
/** Strategy to choose offer ordering without modifying the catalog matching algorithm. */
public interface OfferSortRule {
    Comparator<Offer> comparator();
    String name();
}
