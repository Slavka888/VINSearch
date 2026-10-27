# Демонстрационные данные находятся в data.json; никаких внешних сервисов нет.

import argparse
import json
from pathlib import Path
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


def find_verified_analogs(compatible_parts, analogs, parts):
    result = []

    seen = set()

    for link in analogs:
        if not link["verified"]:
            continue
        for original in compatible_parts:
            if original["part_id"] != link["original_part_id"]:
                continue
            for candidate in parts:
                if candidate["part_id"] == link["replacement_part_id"] and candidate["part_id"] not in seen:
                    result.append({"part_id": candidate["part_id"], "article": candidate["article"],
                                   "name": candidate["name"], "basis": link["basis"], "is_analog": True})
                    seen.add(candidate["part_id"])
                    break

    return result


def prepare_reply(data, request_id):
    request = next((r for r in data["requests"] if r["request_id"] == request_id), None)

    if request is None:
        return {"status": "error", "message": "запрос не найден"}

    errors = check_request(request)

    if errors:
        return {"status": "clarification", "message": "; ".join(errors)}

    if not any(c["customer_id"] == request["customer_id"] for c in data["customers"]):
        return {"status": "clarification", "message": "покупатель не найден"}

    vehicle = find_vehicle(request["vin"].upper(), data["vehicles"])

    if vehicle is None:
        return {"status": "unavailable", "message": "автомобиль по VIN не определен"}

    if vehicle["vehicle_id"] != request["vehicle_id"]:
        return {"status": "clarification", "message": "VIN не соответствует автомобилю запроса"}

    parts = find_compatible_parts(vehicle["vehicle_id"], request["part_group"],
                                  data["compatibilities"], data["parts"])
    if not parts:
        return {"status": "unavailable", "message": "совместимые детали не найдены"}

    ranked = rank_offers(parts, data["offers"])

    selected_parts = parts

    if not ranked:
        analog_parts = find_verified_analogs(parts, data.get("analogs", []), data["parts"])
        ranked = rank_offers(analog_parts, data["offers"])
        selected_parts = analog_parts

    if not ranked:
        return {"status": "unavailable", "message": "актуальных предложений и проверенных аналогов нет"}

    part_by_id = {part["part_id"]: part for part in selected_parts}

    supplier_by_id = {s["supplier_id"]: s["name"] for s in data["suppliers"]}

    choices = [{"offer_id": offer["offer_id"], "article": part_by_id[offer["part_id"]]["article"],
                "basis": part_by_id[offer["part_id"]]["basis"],
                "is_analog": part_by_id[offer["part_id"]].get("is_analog", False),
                "supplier": supplier_by_id[offer["supplier_id"]], "price": offer["price"],
                "delivery_days": offer["delivery_days"], "checked_at": offer["checked_at"]}
               for offer in ranked]

    return {"status": "review", "vin": request["vin"], "choices": choices,
            "message": "предложения подготовлены; применимость, цена и остаток требуют подтверждения менеджером"}


def main():
    parser = argparse.ArgumentParser(description="Подбор автозапчастей по VIN")
    parser.add_argument("--data", default=str(Path(__file__).with_name("data.json")))
    parser.add_argument("--request", default="R-001")
    args = parser.parse_args()
    with open(args.data, encoding="utf-8") as stream:
        data = json.load(stream)
    print(json.dumps(prepare_reply(data, args.request), ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
