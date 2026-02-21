# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/lxf/demo`: Spring Boot backend code, organized by `common`, `config`, `modules` (feature slices), `security`, and `utils`.
- `src/main/resources`: configuration and assets such as `application.yml`, `application-*.yml`, `logback-spring.xml`, and `init.sql`.
- `src/test/java/com/lxf/demo`: JUnit tests (e.g., `SpringBootDemoApplicationTests.java`).
- `frontend/`: Vue 3 + Vite app. Source in `frontend/src`, build output in `frontend/dist`.
- Build outputs: backend `target/`, frontend `dist/`.

## Build, Test, and Development Commands
Backend (Windows):
```bash
.\mvnw.cmd clean install         # Build
.\mvnw.cmd spring-boot:run       # Run (dev profile, port 8888)
.\mvnw.cmd test                  # Run all tests
.\mvnw.cmd test -Dtest=ClassName # Run a single test class
```
Backend (Unix/Mac):
```bash
./mvnw clean install
./mvnw spring-boot:run
```
Frontend:
```bash
cd frontend
npm install
npm run dev       # Dev server
npm run build     # Production build
npm run preview   # Serve build output
```

## Coding Style & Naming Conventions
- Java: 4-space indentation, `PascalCase` for classes, `camelCase` for methods/fields, `UPPER_SNAKE_CASE` for constants.
- Package base: `com.lxf.demo`; keep new packages under existing module folders.
- Vue: `PascalCase` component names, `kebab-case` file names for routes/assets where applicable.
- No formatter/linter is configured; keep style consistent with neighboring files.

## Testing Guidelines
- Framework: JUnit via `spring-boot-starter-test`.
- Test naming: `*Tests.java` (see `SpringBootDemoApplicationTests.java`).
- No frontend test runner is currently configured.

## Commit & Pull Request Guidelines
- Commit messages follow a prefix pattern like `add: ...`, `mod: ...`, `fix: ...`.
- PRs should include a concise summary, list of tests run (or note “not run”), and screenshots for UI changes in `frontend/`.

## Configuration & Data
- Profiles: `application-dev.yml`, `application-test.yml`, `application-prod.yml`.
- Database initialization: run `src/main/resources/init.sql` for MySQL schemas.
- Token auth relies on Redis; ensure local Redis/MySQL are available when running backend.
