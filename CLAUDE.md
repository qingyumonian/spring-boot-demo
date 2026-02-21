# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 2.3.6 REST API with Spring Security, MyBatis-Plus ORM, Redis token storage, and MySQL. Uses Java 8. Includes a Vue 3 frontend with Element Plus.

**Base Package**: `com.lxf.demo`

## Build and Run Commands

### Backend (Spring Boot)
```bash
# Build
mvnw.cmd clean install          # Windows
./mvnw clean install            # Unix/Mac

# Run (uses dev profile by default, port 8888)
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

### Frontend (Vue 3 + Vite)
```bash
cd frontend
npm install                     # Install dependencies
npm run dev                     # Dev server (hot reload)
npm run build                   # Production build to dist/
npm run preview                 # Preview production build
```

**Frontend Stack**: Vue 3, Vite, Element Plus, Pinia, Vue Router, Axios

## Architecture

### Package Structure
```
com.lxf.demo/
├── common/                 # Shared utilities
│   ├── enums/             # ResponseCode enum
│   ├── exception/         # BusinessException + GlobalExceptionHandler
│   ├── filter/            # RequestLoggingFilter
│   └── result/            # R<T> unified response wrapper
├── config/                # Spring configurations
│   └── sso/               # SSO provider configs
│       ├── cas/           # CasProperties, CasSecurityConfig
│       └── oidc/          # KeycloakProperties, KeycloakSecurityConfig
├── modules/               # Feature modules (vertical slices)
│   ├── controller/        # AuthController, UserController, RoleController, MenuController
│   ├── dto/               # Request DTOs for all modules
│   ├── entity/            # SysUser, SysRole, SysMenu, SysUserRole, SysRoleMenu
│   ├── mapper/            # MyBatis-Plus mappers
│   └── service/           # Services + impls (Token, User, Role, Menu, OidcUserSync)
├── security/              # Spring Security components
│   ├── encoder/           # Md5PasswordEncoder
│   ├── filter/            # TokenAuthenticationFilter
│   ├── handler/           # Form/OIDC auth success/failure handlers
│   ├── oauth2/            # HttpCookieOAuth2AuthorizationRequestRepository
│   ├── sso/               # SSO integration
│   │   └── cas/           # CasAuthenticationFilter, CasTicketValidator, handlers
│   └── userdetails/       # CustomUserDetails + service
└── utils/                 # JwtTokenUtil
```

### Authentication Flow

**Form Login** (default):
1. `POST /api/auth/form` authenticates via `CustomUserDetailsService` (MD5 password check)
2. On success, `TokenService` generates token stored in Redis
3. Token returned in response body and `am_access_token` cookie
4. Subsequent requests: `TokenAuthenticationFilter` validates token from header/cookie
5. `POST /api/auth/logout` removes token from Redis

**SSO Options** (configurable via `sso.*` properties):
- **Keycloak OIDC** (`sso.keycloak.enabled=true`): OAuth2 login via `/oauth2/authorization/keycloak`, callback at `/api/auth/keycloak`. Logout callback: `/api/auth/logout/callBack/oidc`
- **CAS 2.0** (`sso.cas.enabled=true`): CAS login at `/login/cas`, validates tickets via `CasTicketValidator`

**Token sources** (checked in order): `am_access_token` header → `Authorization: Bearer` header → `am_access_token` cookie

### API Endpoints
- `POST /api/auth/form` - Form login (public)
- `POST /api/auth/logout` - Logout (public)
- `GET/POST/PUT/DELETE /api/users` - User CRUD (requires `system:user:*` authorities)
- `GET/POST/PUT/DELETE /api/roles` - Role CRUD (requires `system:role:*` authorities)
- `GET/POST/PUT/DELETE /api/menus` - Menu CRUD (requires `system:menu:*` authorities)
- `GET /api/menus/tree` - Menu tree structure
- `PUT /api/users/{id}/roles` - Assign roles to user
- `PUT /api/roles/{id}/menus` - Assign menus to role
- Pagination: `?pageNum=1&pageSize=10`

### Key Dependencies
- **Spring Security**: Stateless token-based auth (session disabled)
- **MyBatis-Plus 3.5.1**: ORM with `BaseMapper<T>` for CRUD
- **Redis**: Token storage via `spring-boot-starter-data-redis`
- **Druid 1.2.16**: Connection pool with monitoring
- **Lombok**: Entity boilerplate reduction
- **spring-security-cas**: CAS 2.0 protocol support
- **spring-boot-starter-oauth2-client**: Keycloak OIDC integration

### SSO Configuration
SSO providers are configured in `application.yml` under `sso.*`:
```yaml
sso:
  keycloak:
    enabled: true/false
    client-id: ...
    client-secret: ...
    realm: ...
    base-url: http://keycloak-server:8080
  cas:
    enabled: true/false
    server-url-prefix: https://cas-server/cas
    client-host-url: http://localhost:8888
    login-path: /login/cas
```
Properties bound via `SsoProperties` → `KeycloakProperties` / `CasProperties`. Security configs conditionally load based on `enabled` flags.

### Response Format
All API responses use `R<T>` wrapper:
```json
{"code": 200, "message": "成功", "data": {...}}
```

### Database Setup
Run `src/main/resources/init.sql` to create MySQL database. Databases by profile:
- `lxf-demo` (dev), `lxf-demo-test` (test), `lxf-demo-prod` (prod)
