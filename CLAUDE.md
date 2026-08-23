# Project

This is a recruitment task — see [task.md](task.md) for the full spec.

# Tests

- Unit tests: `assistant-app/src/test/java` (`*Test.java`), run via `./mvnw test`.
- Integration tests: `assistant-app/src/it/java` (`*IT.java`, Testcontainers), run via `./mvnw verify`.
- Fixed test values go in `private static final` constants, UPPER_SNAKE_CASE, not inline string literals.
- After finishing any code change, run the unit tests (`./mvnw test`) before considering the change done.
- Never write a test for pure delegation (a method that just forwards its call/arguments to another bean with no logic of its own). Only test methods that actually do something (mapping, branching, validation, error handling, etc.).

# Code style

- Follow SOLID principles.
- Always separate the data access layer (e.g. `JdbcTemplate`/SQL) into its own repository class — never mix it into a service.
- Tunable/config-like values (window sizes, limits, thresholds) go in `application.yml`, not hardcoded as Java constants — read them via `@ConfigurationProperties` or `@Value`, whichever fits. This doesn't apply to fixed test values (see Tests section above).
- Never put logic in controllers (no null-checks, fallbacks, branching, etc.). Controllers only translate HTTP <-> calls to services/beans; resolve defaults and business rules elsewhere (config classes, services) and have Spring inject the result.
