package vinsearch.logic;
import vinsearch.interfaces.OfferSortRule;
import vinsearch.logic_classes.ReviewReply;
import vinsearch.logic_classes.Supplier;
import vinsearch.logic_classes.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/** Workflow ported directly from Python prepare_reply(). */
public final class RequestProcessor {
    private final DataStore data;
    private final Catalog catalog;
    private final OfferRanker offerRanker;
    public RequestProcessor(DataStore data, OfferSortRule rule) {
        this.data = data;
        this.catalog = new Catalog(data);
        this.offerRanker = new OfferRanker(rule);
    }
    public Reply prepareReply(String requestId) {
        Request request = null;
        for (Request item : data.getRequests()) {
            if (item.getRequestId().equals(requestId)) { request = item; break; }
        }
        if (request == null) return new MessageReply("error", "запрос не найден");
        List<String> errors = request.checkCompleteness();
        if (!errors.isEmpty()) return new MessageReply("clarification", String.join("; ", errors));
        boolean customerExists = false;
        for (Customer customer : data.getCustomers()) {
            if (customer.getCustomerId().equals(request.getCustomerId())) { customerExists = true; break; }
        }
        if (!customerExists) return new MessageReply("clarification", "покупатель не найден");
        Vehicle vehicle = catalog.findVehicle(request.getVin().toUpperCase(Locale.ROOT));
        if (vehicle == null) return new MessageReply("unavailable", "автомобиль по VIN не определен");
        if (!vehicle.getVehicleId().equals(request.getVehicleId())) {
            return new MessageReply("clarification", "VIN не соответствует автомобилю запроса");
        }
        List<CompatiblePart> parts = catalog.findCompatibleParts(vehicle.getVehicleId(), request.getPartGroup());
        if (parts.isEmpty()) return new MessageReply("unavailable", "совместимые детали не найдены");
        List<Offer> ranked = offerRanker.rankOffers(parts, data.getOffers());
        List<CompatiblePart> selectedParts = parts;
        if (ranked.isEmpty()) {
            selectedParts = catalog.findVerifiedAnalogs(parts);
            ranked = offerRanker.rankOffers(selectedParts, data.getOffers());
        }
        if (ranked.isEmpty()) return new MessageReply("unavailable", "актуальных предложений и проверенных аналогов нет");
        Map<String, CompatiblePart> partById = new HashMap<>();
        for (CompatiblePart p : selectedParts) partById.put(p.part().getPartId(), p);
        Map<String, String> supplierById = new HashMap<>();
        for (Supplier supplier : data.getSuppliers()) supplierById.put(supplier.getSupplierId(), supplier.getName());
        List<Map<String, Object>> choices = new ArrayList<>();
        for (Offer offer : ranked) {
            CompatiblePart p = partById.get(offer.getPartId());
            Map<String, Object> choice = new LinkedHashMap<>();
            choice.put("offer_id", offer.getOfferId());
            choice.put("article", p.part().getArticle());
            choice.put("basis", p.basis());
            choice.put("is_analog", p.isAnalog());
            choice.put("supplier", supplierById.get(offer.getSupplierId()));
            choice.put("price", offer.getPrice());
            choice.put("delivery_days", offer.getDeliveryDays());
            choice.put("checked_at", offer.getCheckedAt());
            choices.add(choice);
        }
        return new ReviewReply(request.getVin(), choices,
                "предложения подготовлены; применимость, цена и остаток требуют подтверждения менеджером");
    }
}
