# Демонстрационные данные находятся в data.json; никаких внешних сервисов нет.

from bisect import bisect_left


def find_compatible_parts(vehicle_id, part_group, compatibilities, parts):
    matches = []

    for link in compatibilities:
        if link["vehicle_id"] != vehicle_id:
            continue
        for part in parts:
            if part["part_id"] == link["part_id"] and part["part_group"].casefold() == part_group.casefold():
                matches.append({"part_id": part["part_id"], "article": part["article"],
                                "name": part["name"], "basis": link["basis"]})
                break

    return matches


def rank_offers(compatible_parts, offers):
    part_ids = sorted(part["part_id"] for part in compatible_parts)

    candidates = []

    for offer in offers:
        position = bisect_left(part_ids, offer["part_id"])
        if position < len(part_ids) and part_ids[position] == offer["part_id"] and offer["stock"] > 0:
            candidates.append(offer)

    return sorted(candidates, key=lambda offer: (offer["price"], offer["delivery_days"], offer["offer_id"]))


def check_request(request):
    errors = []
    if not request.get("customer_id"):
        errors.append("не указан покупатель")

    vin = str(request.get("vin", "")).strip().upper()
    if len(vin) != 17 or any(ch not in "ABCDEFGHJKLMNPRSTUVWXYZ0123456789" for ch in vin):
        errors.append("VIN должен содержать 17 допустимых символов")

    if not str(request.get("part_group", "")).strip():
        errors.append("не указан требуемый узел")

    return errors


def find_vehicle(vin, vehicles):
    for vehicle in vehicles:
        if vehicle["vin"] == vin:
            return vehicle

    return None
