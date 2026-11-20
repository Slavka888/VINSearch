package vinsearch.logic;

/** Selection: entity of the KT2 logical model; fields are encapsulated. */
public final class Selection {
    private final String selectionId;
    private final String requestId;
    private final String offerId;
    private final boolean confirmed;
    private final String reason;

    public Selection(String selectionId, String requestId, String offerId, boolean confirmed, String reason) {
        this.selectionId = selectionId;
        this.requestId = requestId;
        this.offerId = offerId;
        this.confirmed = confirmed;
        this.reason = reason;
    }

    public String getSelectionId() { return selectionId; }
    public String getRequestId() { return requestId; }
    public String getOfferId() { return offerId; }
    public boolean isConfirmed() { return confirmed; }
    public String getReason() { return reason; }
}
