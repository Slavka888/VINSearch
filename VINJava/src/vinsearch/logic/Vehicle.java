package vinsearch.logic;

/** Vehicle: entity of the KT2 logical model; fields are encapsulated. */
public final class Vehicle {
    private final String vehicleId;
    private final String vin;
    private final String make;
    private final String model;
    private final String configuration;

    public Vehicle(String vehicleId, String vin, String make, String model, String configuration) {
        this.vehicleId = vehicleId;
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.configuration = configuration;
    }

    public String getVehicleId() { return vehicleId; }
    public String getVin() { return vin; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getConfiguration() { return configuration; }
}
