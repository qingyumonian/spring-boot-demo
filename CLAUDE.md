# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 2.3.6 REST API application with MyBatis-Plus ORM and MySQL database. Uses Java 8.

**Base Package**: `com.lxf.demo`

## Build and Run Commands

```bash
# Build
mvnw.cmd clean install          # Windows
./mvnw clean install            # Unix/Mac

# Run (uses dev profile by default)
mvnw.cmd spring-boot:run
./mvnw spring-boot:run

# Run with specific profile
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod

# Tests
mvnw.cmd test                                           # All tests
mvnw.cmd test -Dtest=SpringBootDemoApplicationTests     # Single class
mvnw.cmd test -Dtest=SpringBootDemoApplicationTests#contextLoads  # Single method
```

## Architecture

### Layered Structure
- **Controller** (`controller/`): REST endpoints at `/api/users` with CRUD + pagination
- **Service** (`service/`): Business logic interfaces and implementations
- **Mapper** (`mapper/`): MyBatis-Plus data access extending `BaseMapper<T>`
- **Entity** (`entity/`): JPA entities with Lombok `@Data` and MyBatis-Plus annotations
- **Config** (`config/`): MyBatis-Plus pagination interceptor configuration

### Key Dependencies
- **MyBatis-Plus 3.5.1**: ORM with built-in CRUD and pagination via `BaseMapper`
- **Druid 1.2.16**: Alibaba database connection pool with monitoring support
- **Lombok**: For entity boilerplate reduction

### Database Setup
Run `src/main/resources/init.sql` to create the MySQL database and `user` table. Three databases are defined:
- `lxf-demo` (dev)
- `lxf-demo-test` (test)
- `lxf-demo-prod` (prod)

### Profile Configuration
- `application.yml`: Sets active profile to `dev`
- `application-{dev,test,prod}.yml`: Environment-specific database and Druid pool configs

### Important Note
`UserServiceImpl.java` is incorrectly placed in `src/main/resources/` instead of `src/main/java/com/lxf/demo/service/`. This should be moved for the service to be properly component-scanned.
