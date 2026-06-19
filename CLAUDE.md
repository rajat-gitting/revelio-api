# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`revelio-api` is a Spring Boot 3.2 REST API (Java 17, Gradle) for the Revelio platform, package root `com.revelio.api`. It currently serves blog/homepage content from **in-memory seed data** (`BlogService.seedData()`) — there is no database or persistence layer yet, despite the README mentioning optional Redis/DB.

## Commands

```bash
./gradlew bootRun                       # run locally (activates the `dev` profile via bootRun JVM args)
./gradlew build                         # compile + test + assemble jar (build/libs/revelio-api.jar)
./gradlew test                          # run all tests (JUnit 5); finalized by jacocoTestReport
./gradlew test --tests 'com.revelio.api.service.BlogServiceTest'          # single test class
./gradlew test --tests 'com.revelio.api.service.BlogServiceTest.methodName'  # single test method
./gradlew jacocoTestReport              # coverage report -> build/reports/jacoco
./gradlew spotlessCheck                 # verify Google Java Format
./gradlew spotlessApply                 # auto-format (run this before committing)
./gradlew check                         # runs tests + spotlessCheck
```

**Formatting is enforced.** Spotless uses Google Java Format (2-space indent). `spotlessCheck` is wired into `check`, so run `./gradlew spotlessApply` before committing or the build/review will fail.

## Runtime URLs

Server runs on port **8083** with context path **`/api`** (see `application.yml`). All URLs are therefore prefixed with `/api`, e.g. `http://localhost:8083/api/health`, `/api/ping`, `/api/swagger-ui.html`, `/api/v3/api-docs`.

## Architecture

Standard layered Spring MVC: `controller/` (HTTP) → `service/` (logic + validation) → `model/` (domain) / `dto/` (wire shapes). Wiring uses constructor injection via Lombok `@RequiredArgsConstructor`; controllers stay thin and delegate to services.

Key cross-cutting conventions — follow these when adding endpoints:

- **Response envelope:** every successful response is wrapped in `ApiResponse<T>` (use `ApiResponse.ok(data)`), which adds `success`/`message`/`timestamp`. Pagination uses `PagedResponse<T>` (mirrors Spring Data `Page`: content, totalElements, totalPages, number, size).
- **Errors:** never return ad-hoc error bodies. Throw `ResourceNotFoundException` / `BadRequestException` (in `exception/`); `GlobalExceptionHandler` (`@RestControllerAdvice`) maps them — plus bean-validation errors — to an `ErrorResponse` with a stable `code` (`NOT_FOUND`, `BAD_REQUEST`, `VALIDATION_ERROR`, `INTERNAL_ERROR`), `message`, and `path`.
- **Validation** lives in both DTOs (Jakarta Bean Validation, e.g. `HealthQuery`) and defensively in services, so the controller layer stays thin.
- **OpenAPI:** annotate controllers/operations with `@Tag` and `@Operation`; config in `config/OpenApiConfig.java`.

### Routing convention (important)

Context path `/api` is applied globally (`server.servlet.context-path`), so controller `@RequestMapping` paths must **not** repeat `/api` — the context path already supplies it. All controllers follow this: `BlogController` → `/blogs` (served at `/api/blogs`), `HomepageController` → `/homepage`, `HealthController` → `/health`, `PingController` → `/ping`. Repeating the prefix (e.g. `@RequestMapping("/api/blogs")`) would push the endpoint to `/api/api/blogs` — a real bug that previously shipped; `BlogControllerRoutingTest`/`HomepageControllerRoutingTest` guard against it (they request `/api/blogs` with `contextPath("/api")` and fail if the mapping regresses).

`SecurityConfig`'s permit-list matchers are written against the **post-context-path** servlet path (i.e. `/blogs`, `/homepage`, not `/api/blogs`). When adding or moving an endpoint, update the matching permit rule and verify the actual served URL. The `revelio-ui` frontend's `VITE_API_BASE_URL` already includes `/api`, so its `endpoints.ts` paths are also written without the prefix (`/blogs`, `/blogs/search`, …) — keep the two in sync.

### Security

`SecurityConfig` permits a fixed allow-list of public paths (blogs, `/health`, `/ping`, actuator health/info, swagger/openapi) and requires authentication (`httpBasic`) for everything else; CSRF is disabled and CORS is delegated to `CorsConfigurationSource`. CORS is configured from `app.cors.*` properties (`CorsProperties`/`CorsConfig`): permissive in `dev`, origin-restricted in `prod`.

### Profiles

`dev` (default for `bootRun`): verbose/DEBUG logging, permissive CORS. `prod`: WARN logging, restricted CORS. Logging format in `logback-spring.xml` (pretty console for dev, JSON-style for prod).

## Testing

JUnit 5 + AssertJ. Controller tests use `@WebMvcTest` with `@MockBean`-ed services and `MockMvc`; `spring-security-test` is available for auth. Service/DTO/model tests are plain unit tests. `BlogService` exposes a `BlogService(List<Blog>)` constructor so tests can inject fixtures instead of the seeded data.

## CI

`.github/workflows/reviewer.yml` runs an automated agentic reviewer (`anthropics/claude-code-action`) on every PR. It reviews the diff against this `CLAUDE.md` and the linked Jira ticket's acceptance criteria — keep this file accurate, as the reviewer treats it as the source of truth for conventions.
