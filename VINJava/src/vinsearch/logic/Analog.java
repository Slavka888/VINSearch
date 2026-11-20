package vinsearch.logic;

/** Analog: entity of the KT2 logical model; fields are encapsulated. */
public final class Analog {
    private final String analogId;
    private final String originalPartId;
    private final String replacementPartId;
    private final boolean verified;
    private final String basis;

    public Analog(String analogId, String originalPartId, String replacementPartId, boolean verified, String basis) {
        this.analogId = analogId;
        this.originalPartId = originalPartId;
        this.replacementPartId = replacementPartId;
        this.verified = verified;
        this.basis = basis;
    }

    public String getAnalogId() { return analogId; }
    public String getOriginalPartId() { return originalPartId; }
    public String getReplacementPartId() { return replacementPartId; }
    public boolean isVerified() { return verified; }
    public String getBasis() { return basis; }
}
