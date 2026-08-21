# Tests

- Unit tests: `assistant-app/src/test/java` (`*Test.java`), run via `./mvnw test`.
- Integration tests: `assistant-app/src/it/java` (`*IT.java`, Testcontainers), run via `./mvnw verify`.
- Fixed test values go in `private static final` constants, UPPER_SNAKE_CASE, not inline string literals.

# Code style

- Follow SOLID principles.
