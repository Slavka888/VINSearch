package vinsearch.logic;

/** Compatibility: entity of the KT2 logical model; fields are encapsulated. */
public final class Compatibility {
    private final String compatibilityId;
    private final String vehicleId;
    private final String partId;
    private final String basis;

    public Compatibility(String compatibilityId, String vehicleId, String partId, String basis) {
        this.compatibilityId = compatibilityId;
        this.vehicleId = vehicleId;
        this.partId = partId;
        this.basis = basis;
    }

    public String getCompatibilityId() { return compatibilityId; }
    public String getVehicleId() { return vehicleId; }
    public String getPartId() { return partId; }
    public String getBasis() { return basis; }
}
