# Агрегатор данных из REST API (Этап 1)

Консольное Java-приложение, которое опрашивает три открытых REST API, приводит полученные данные к единой модели и сохраняет их в файл (JSON или CSV). Приложение поддерживает чтение файла и фильтрацию по источнику.

## Используемые технологии
* **Java 21** (включая `java.time` и `java.net.http.HttpClient`)
* **Gradle** (система сборки)
* **Gson** (библиотека для парсинга и работы с JSON)
* **SLF4J** (логирование)
* **Java NIO** (работа с файловой системой: `Files`, `Path`)

## Выбранные API

В соответствии с требованиями задания, каждое API принимает query-параметры, возвращает JSON с более чем 5 полями, содержащими вложенные объекты или массивы, и данные которых изменяются со временем.

1. **Open-Meteo (Погода)** — `https://api.open-meteo.com/v1/forecast`
    * Возвращает текущую погоду и почасовой прогноз (вложенные объекты `current` и массивы в `hourly`).
    * Параметры: `latitude`, `longitude`, `current`, `hourly`.
2. **Frankfurter (Валюты)** — `https://api.frankfurter.dev/v1/latest`
    * Возвращает курсы валют в виде словаря (`Map< String, Double >` в поле `rates`).
    * Параметры: `base`, `symbols`.
3. **CoinGecko (Криптовалюты)** — `https://api.coingecko.com/api/v3/simple/price`
    * Возвращает цены на криптовалюты (вложенный объект для каждой монеты).
    * Параметры: `ids`, `vs_currencies`, `include_market_cap`, `include_24hr_vol`.

## Архитектура

Проект разделен на слои для соблюдения принципов ООП и разделения ответственности:
* **`cli`** — парсинг аргументов командной строки (`CliParser`, `CliOptions`).
* **`core`** — бизнес-логика (`AggregateService` — опрос источников, `AppFactory` — сборка зависимостей).
* **`source`** — реализации интерфейса `ApiSource` для каждого API (Стратегия).
* **`http`** — интерфейс `HttpClient` и его реализация `JavaHttpClient` (Внедрение зависимостей).
* **`storage`** — интерфейс `Storage` и реализации `JsonStorage`, `CsvStorage`.
* **`model`** — единая модель данных `AggregatedRecord` и параметры запроса `QueryParams`.

*Новый API добавляется созданием нового класса, реализующего `ApiSource`, без изменения существующей логики.*

## 🚀 Сборка и запуск

### Сборка проекта
```bash
./gradlew clean build
```
### Запуск проекта
#### 1. Опрос API и сохранение в JSON (создать новый файл)
```bash
./gradlew run --args="poll --apis=weather,currency,crypto --format=json --mode=create --file=data.json"
```
#### 2. Опрос API и сохранение в CSV
```bash
./gradlew run --args="poll --apis=weather,currency --format=csv --mode=create --file=data.csv"
```
#### 3. Дозапись в существующий файл (режим APPEND)
```bash
./gradlew run --args="poll --apis=weather --format=json --mode=append --file=data.json"
```
#### 4. Просмотр всего файла
```bash
./gradlew run --args="show --file=data.json"
```
#### 5. Просмотр только определенного источника
```bash
./gradlew run --args="show --file=data.json --source=crypto"
```

## Обработка ошибок
При неверных аргументах программа выводит понятную ошибку валидации. Если внешнее API недоступно, агрегатор логирует ошибку и продолжает опрос остальных источников.