# revelio-api

Production-oriented Spring Boot 3.2 REST API skeleton for the Revelio platform (`com.revelio.api`).

## Prerequisites

- **Java 17** (JDK)
- Optional: local Redis/DB clients if you extend the service layer later; the skeleton runs standalone.

## Run

```bash
./gradlew bootRun
```

Boot run activates the **`dev`** profile by default (see `build.gradle` `bootRun` JVM args).

```bash
./gradlew build
./gradlew test
```

## URLs (default: port `8083`, context path `/api`)

| Endpoint | URL |
| --- | --- |
| Health (JSON) | [http://localhost:8083/api/health](http://localhost:8083/api/health) |
| Actuator health (probe) | [http://localhost:8083/api/actuator/health](http://localhost:8083/api/actuator/health) |
| Ping | [http://localhost:8083/api/ping](http://localhost:8083/api/ping) |
| Swagger UI | [http://localhost:8083/api/swagger-ui.html](http://localhost:8083/api/swagger-ui.html) |
| OpenAPI docs | [http://localhost:8083/api/v3/api-docs](http://localhost:8083/api/v3/api-docs) |

The `/health` endpoint supports optional query parameters validated via `HealthQuery` (`notifyEmail`, `traceId`).

## Project layout

```
src/main/java/com/revelio/api/
├── RevelioApiApplication.java
├── config/          # Security, CORS, Jackson, OpenAPI
├── controller/      # HTTP layer (constructor injection, delegates to services)
├── service/         # Application services
├── model/           # Domain/value objects (e.g. HealthStatus)
├── dto/             # Api/Error responses and validated query DTOs
├── exception/       # Global handler + domain exceptions
└── util/            # Shared helpers (placeholder package-info)
```

## Profiles

- **`dev`**: verbose logging, permissive CORS (`application-dev.yml`).
- **`prod`**: warn-level logging, stricter CORS (`application-prod.yml`).

Structured logging is configured in `logback-spring.xml` (pretty console for `dev`, JSON-style lines for `prod`).

## Quality checks

- **JaCoCo** test reports: `./gradlew jacocoTestReport`
- **Spotless** (Google Java Format): `./gradlew spotlessCheck` (also runs as part of `./gradlew check`)
