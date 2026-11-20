package vinsearch.logic;
import vinsearch.logic_classes.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
/** In-memory dataset loaded from the exact KT2 src/data.json, no database until KT4. */
public final class DataStore {
    private final List<Customer> customers;
    private final List<Vehicle> vehicles;
    private final List<Request> requests;
    private final List<Part> parts;
    private final List<Compatibility> compatibilities;
    private final List<Analog> analogs;
    private final List<Supplier> suppliers;
    private final List<Offer> offers;
    private final List<Selection> selections;
    private DataStore(Map<String,Object> m) {
        customers = items(m, "customers", o -> new Customer(s(o,"customer_id"),s(o,"name"),s(o,"contact")));
        vehicles = items(m, "vehicles", o -> new Vehicle(s(o,"vehicle_id"),s(o,"vin"),s(o,"make"),s(o,"model"),s(o,"configuration")));
        requests = items(m, "requests", o -> new Request(s(o,"request_id"),s(o,"customer_id"),s(o,"vehicle_id"),s(o,"vin"),s(o,"part_group"),s(o,"received_at")));
        parts = items(m, "parts", o -> new Part(s(o,"part_id"),s(o,"article"),s(o,"name"),s(o,"part_group")));
        compatibilities = items(m, "compatibilities", o -> new Compatibility(s(o,"compatibility_id"),s(o,"vehicle_id"),s(o,"part_id"),s(o,"basis")));
        analogs = items(m, "analogs", o -> new Analog(s(o,"analog_id"),s(o,"original_part_id"),s(o,"replacement_part_id"),b(o,"verified"),s(o,"basis")));
        suppliers = items(m, "suppliers", o -> new Supplier(s(o,"supplier_id"),s(o,"name")));
        offers = items(m, "offers", o -> new Offer(s(o,"offer_id"),s(o,"supplier_id"),s(o,"part_id"),n(o,"price"),n(o,"stock"),n(o,"delivery_days"),s(o,"checked_at")));
        selections = items(m, "selections", o -> new Selection(s(o,"selection_id"),s(o,"request_id"),s(o,"offer_id"),b(o,"confirmed"),s(o,"reason")));
    }
    @SuppressWarnings("unchecked")
    private static <T> List<T> items(Map<String,Object> m, String key, Function<Map<String,Object>, T> creator) {
        List<T> result = new ArrayList<>();
        Object raw = m.getOrDefault(key, List.of());
        for (Object o : (List<?>)raw) result.add(creator.apply((Map<String,Object>)o));
        return List.copyOf(result);
    }
    private static String s(Map<String,Object> m, String k) { return (String)m.get(k); }
    private static int n(Map<String,Object> m, String k) { return ((Number)m.get(k)).intValue(); }
    private static boolean b(Map<String,Object> m, String k) { return Boolean.TRUE.equals(m.get(k)); }
    @SuppressWarnings("unchecked")
    public static DataStore load(Path path) throws IOException {
        return new DataStore((Map<String,Object>)Json.parse(Files.readString(path)));
    }
    public List<Customer> getCustomers() { return customers; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public List<Request> getRequests() { return requests; }
    public List<Part> getParts() { return parts; }
    public List<Compatibility> getCompatibilities() { return compatibilities; }
    public List<Analog> getAnalogs() { return analogs; }
    public List<Supplier> getSuppliers() { return suppliers; }
    public List<Offer> getOffers() { return offers; }
    public List<Selection> getSelections() { return selections; }
}
