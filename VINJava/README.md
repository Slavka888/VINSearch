# VINSearch — объектно-ориентированная версия на Java (КТ3)

Проект реализует тот же подбор автозапчастей по VIN, что и процедурная версия Python из КТ2. Сохранены идентификаторы `R-001`–`R-004`, тестовые данные и структура JSON-ответов. Используются **JDK 21** и стандартная библиотека; Maven/Gradle и внешние зависимости не требуются. Сборка и тесты проверены на OpenJDK **21.0.12.1**; это версия тестового окружения, а не утверждение о JDK, установленном у студента.

## Сборка и запуск

Команды выполняются из папки **src/java/** в терминале Bash, PowerShell (если нужен вариант с расширением шаблона, запустите команду в Bash) или терминале IntelliJ IDEA с Bash:

```bash
java -version
javac -version
javac -encoding UTF-8 -d out src/vinsearch/*.java
java -Dfile.encoding=UTF-8 -cp out vinsearch.Main --request R-001
java -Dfile.encoding=UTF-8 -cp out vinsearch.Main --request R-002
java -Dfile.encoding=UTF-8 -cp out vinsearch.Main --request R-003
java -Dfile.encoding=UTF-8 -cp out vinsearch.Main --request R-004
java -Dfile.encoding=UTF-8 -cp out vinsearch.TestRunner
```

Для Windows `cmd` UTF-8 вывод включается командой `chcp 65001`. Аргумент `--data data.json` позволяет указать путь к JSON из КТ2.

## Пример ожидаемого вывода

```
$ java -Dfile.encoding=UTF-8 -cp out vinsearch.Main --request R-001
```

Программа печатает JSON со статусом `review`, VIN `WVWZZZ1JZXW000001` и двумя предложениями, отсортированными по цене:

1. `DEMO-F102`, цена 1100, срок доставки 4 дня, `is_analog=false`;
2. `DEMO-F101`, цена 1250, срок доставки 2 дня, `is_analog=false`.

Другие сценарии:

- `R-002` → `clarification`: «VIN должен содержать 17 допустимых символов»;
- `R-003` → `unavailable`: «совместимые детали не найдены»;
- `R-004` → `review`: `DEMO-D202`, цена 2450, 3 дня, `is_analog=true`;
- `R-404` → `error`: «запрос не найден».

## Тесты и сопоставление

`TestRunner` выполняет 12 проверок без JUnit. Кроме того, результаты Java были сопоставлены с **реальным кодом Python из переданного ZIP КТ2**: для пяти запросов `R-001`–`R-004` и `R-404` получены полностью одинаковые объекты JSON (включая массивы предложений, порядок и текст сообщений).

## ООП-решение

- Девять классов `Customer`, `Vehicle`, `Request`, `Part`, `Compatibility`, `Analog`, `Supplier`, `Offer`, `Selection` соответствуют логической модели КТ2.
- `Request.checkCompleteness()` соответствует `check_request()`; `Catalog.findCompatibleParts()` — `find_compatible_parts()`; `Catalog.findVerifiedAnalogs()` — `find_verified_analogs()`; `OfferRanker.rankOffers()` — `rank_offers()`.
- `RequestProcessor.prepareReply()` координирует сценарий подбора. Классы хранят данные в закрытых полях, коллекции представлены `List`, `Map`, `Set`.
- Наследование: `Reply` → `MessageReply`, `ReviewReply`. Переопределение `asMap()` различает статусное сообщение и ответ с предложениями.
- Интерфейс: `OfferSortRule`; реализации `PriceFirstRule` и `FastestFirstRule`. По умолчанию `PriceFirstRule` повторяет порядок Python. Дополнительный режим запуска:

```bash
java -Dfile.encoding=UTF-8 -cp out vinsearch.Main --request R-001 --ranking delivery
```

Он ставит первым более быстрое, но более дорогое предложение `DEMO-F101`. Эта возможность демонстрирует расширяемость без изменения алгоритма фильтрации.
