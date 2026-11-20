package vinsearch.logic;

/** Customer: entity of the KT2 logical model; fields are encapsulated. */
public final class Customer {
    private final String customerId;
    private final String name;
    private final String contact;

    public Customer(String customerId, String name, String contact) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
}
