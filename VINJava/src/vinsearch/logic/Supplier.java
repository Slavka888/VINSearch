package vinsearch.logic;

/** Supplier: entity of the KT2 logical model; fields are encapsulated. */
public final class Supplier {
    private final String supplierId;
    private final String name;

    public Supplier(String supplierId, String name) {
        this.supplierId = supplierId;
        this.name = name;
    }

    public String getSupplierId() { return supplierId; }
    public String getName() { return name; }
}
