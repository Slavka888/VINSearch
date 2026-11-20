package vinsearch.logic;

/** Offer: entity of the KT2 logical model; fields are encapsulated. */
public final class Offer {
    private final String offerId;
    private final String supplierId;
    private final String partId;
    private final int price;
    private final int stock;
    private final int deliveryDays;
    private final String checkedAt;

    public Offer(String offerId, String supplierId, String partId, int price, int stock, int deliveryDays, String checkedAt) {
        this.offerId = offerId;
        this.supplierId = supplierId;
        this.partId = partId;
        this.price = price;
        this.stock = stock;
        this.deliveryDays = deliveryDays;
        this.checkedAt = checkedAt;
    }

    public String getOfferId() { return offerId; }
    public String getSupplierId() { return supplierId; }
    public String getPartId() { return partId; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
    public int getDeliveryDays() { return deliveryDays; }
    public String getCheckedAt() { return checkedAt; }
}
