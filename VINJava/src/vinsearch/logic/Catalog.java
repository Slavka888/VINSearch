package vinsearch.logic;
import vinsearch.logic_classes.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/** Domain operations migrated from Python: find_vehicle, find_compatible_parts, find_verified_analogs. */
public final class Catalog {
    private final DataStore data;
    public Catalog(DataStore data) { this.data = data; }

    public Vehicle findVehicle(String vin) {
        for (Vehicle vehicle : data.getVehicles()) {
            if (vehicle.getVin().equals(vin)) return vehicle;
        }
        return null;
    }

    /** Same nested loops as the KT2 first algorithm: O(n*m) worst time. */
    public List<CompatiblePart> findCompatibleParts(String vehicleId, String partGroup) {
        List<CompatiblePart> result = new ArrayList<>();
        for (Compatibility link : data.getCompatibilities()) {
            if (!link.getVehicleId().equals(vehicleId)) continue;
            for (Part part : data.getParts()) {
                if (part.getPartId().equals(link.getPartId())
                        && part.getPartGroup().toLowerCase(Locale.ROOT)
                           .equals(partGroup.toLowerCase(Locale.ROOT))) {
                    result.add(new CompatiblePart(part, link.getBasis(), false));
                    break;
                }
            }
        }
        return result;
    }

    public List<CompatiblePart> findVerifiedAnalogs(List<CompatiblePart> compatibleParts) {
        List<CompatiblePart> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Analog link : data.getAnalogs()) {
            if (!link.isVerified()) continue;
            for (CompatiblePart original : compatibleParts) {
                if (!original.part().getPartId().equals(link.getOriginalPartId())) continue;
                for (Part candidate : data.getParts()) {
                    if (candidate.getPartId().equals(link.getReplacementPartId())
                            && !seen.contains(candidate.getPartId())) {
                        result.add(new CompatiblePart(candidate, link.getBasis(), true));
                        seen.add(candidate.getPartId());
                        break;
                    }
                }
            }
        }
        return result;
    }
}
