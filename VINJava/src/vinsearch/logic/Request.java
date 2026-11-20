package vinsearch.logic;

import java.util.ArrayList;
import java.util.List;

/** Request: entity of the KT2 logical model; fields are encapsulated. */
public final class Request {
    private final String requestId;
    private final String customerId;
    private final String vehicleId;
    private final String vin;
    private final String partGroup;
    private final String receivedAt;

    public Request(String requestId, String customerId, String vehicleId, String vin, String partGroup, String receivedAt) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.vin = vin;
        this.partGroup = partGroup;
        this.receivedAt = receivedAt;
    }

    public String getRequestId() { return requestId; }
    public String getCustomerId() { return customerId; }
    public String getVehicleId() { return vehicleId; }
    public String getVin() { return vin; }
    public String getPartGroup() { return partGroup; }
    public String getReceivedAt() { return receivedAt; }

    /** The same checks, message order, and VIN alphabet as Python check_request(). */
    public List<String> checkCompleteness() {
        List<String> errors = new ArrayList<>();
        if (customerId == null || customerId.isEmpty()) {
            errors.add("не указан покупатель");
        }
        String normalized = vin == null ? "" : vin.trim().toUpperCase(java.util.Locale.ROOT);
        if (normalized.length() != 17 || !normalized.matches("[ABCDEFGHJKLMNPRSTUVWXYZ0123456789]{17}")) {
            errors.add("VIN должен содержать 17 допустимых символов");
        }
        if (partGroup == null || partGroup.trim().isEmpty()) {
            errors.add("не указан требуемый узел");
        }
        return errors;
    }
}
