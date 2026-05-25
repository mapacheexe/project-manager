# Project Manager

Aplicación web colaborativa de gestión de proyectos y tareas basada en metodología Kanban. Permite a equipos crear proyectos, definir etapas, gestionar tareas y colaborar con control de roles. Interfaz responsiva con soporte de modo claro y oscuro.

**Demo:** [project-manager-hw8b1hij5-mapacheexes-projects.vercel.app](https://project-manager-hw8b1hij5-mapacheexes-projects.vercel.app)  
**API:** [project-manager-backend-production-c6ed.up.railway.app](https://project-manager-backend-production-c6ed.up.railway.app)

---

## Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Frontend | Angular (standalone, signals) | 20 |
| Backend | Spring Boot | 4.0.5 |
| Lenguaje backend | Java LTS | 21 |
| Autenticación | Spring Security + JJWT | JWT / HMAC-SHA256 |
| Base de datos | PostgreSQL | 16 |
| ORM | Spring Data JPA / Hibernate | — |
| Tests backend | JUnit 5 + Mockito + JaCoCo | — |
| Tests frontend | Vitest | — |
| CI/CD | GitHub Actions | — |
| Estilos | SCSS propio (sin frameworks UI) | — |

---

## Funcionalidades

- Registro e inicio de sesión con JWT
- CRUD de proyectos con gestión de miembros y roles (OWNER / ADMIN / MEMBER)
- Etapas Kanban configurables por proyecto (crear, renombrar, reordenar, eliminar)
- CRUD completo de tareas con movimiento entre columnas
- Tablero Kanban interactivo
- Notificaciones toast tras cada acción
- Confirmación en acciones destructivas
- Modo claro / oscuro automático (`prefers-color-scheme`)
- Diseño responsivo: vista global en escritorio, scroll snap columna a columna en móvil

### Roles y permisos

| Acción | OWNER | ADMIN | MEMBER |
|---|:---:|:---:|:---:|
| Ver tablero y tareas | ✅ | ✅ | ✅ |
| Crear / editar / eliminar tarea | ✅ | ✅ | ✅ |
| Mover tarea de columna | ✅ | ✅ | ✅ |
| Renombrar proyecto | ✅ | ✅ | ❌ |
| Crear / editar / eliminar etapa | ✅ | ✅ | ❌ |
| Reordenar etapas | ✅ | ✅ | ❌ |
| Añadir / eliminar miembro | ✅ | ✅ | ❌ |
| Cambiar rol de miembro | ✅ | ✅ | ❌ |
| Eliminar proyecto | ✅ | ❌ | ❌ |
| Abandonar proyecto | ❌ | ✅ | ✅ |

---

## Arquitectura

```
Angular 20 (SPA)
      │  HTTP / JSON
Spring Boot (API REST)
      │  JPA / SQL
PostgreSQL 16
```

La comunicación entre frontend y backend es exclusivamente HTTP con JSON. Las peticiones autenticadas incluyen `Authorization: Bearer <token>`. El token se genera en el login y es validado por un filtro de Spring Security en cada petición.

---

## Modelo de datos

```
users
  id, name, email, password

user_project  (relación N:M con rol)
  id, user_id → users, project_id → project, role, joined_at

project
  id, name

stage
  id, name, project_id → project, position

task
  id, title, description, status, position, stage_id → stage
```

Las contraseñas se almacenan cifradas con BCrypt (factor de coste 10).

---

## API REST

Todas las rutas salvo `/users` (registro) y `/users/login` requieren `Authorization: Bearer <token>`.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/users` | Registrar usuario |
| POST | `/users/login` | Autenticar (devuelve JWT) |
| GET | `/users/{id}` | Obtener usuario |
| PATCH | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |
| GET | `/users/{id}/projects` | Proyectos de un usuario |
| POST | `/users/{id}/projects` | Crear proyecto (asigna OWNER) |
| GET | `/projects/{id}` | Obtener proyecto |
| PATCH | `/projects/{id}` | Actualizar proyecto (OWNER/ADMIN) |
| DELETE | `/projects/{id}` | Eliminar proyecto (OWNER) |
| GET | `/projects/{id}/users` | Miembros del proyecto |
| POST | `/projects/{id}/users/{uid}` | Añadir miembro (OWNER/ADMIN) |
| PATCH | `/projects/{id}/users/{uid}` | Actualizar rol (OWNER/ADMIN) |
| DELETE | `/projects/{id}/users/{uid}` | Eliminar miembro (OWNER/ADMIN) |
| GET | `/projects/{id}/stages` | Etapas del proyecto |
| POST | `/projects/{id}/stages` | Crear etapa (OWNER/ADMIN) |
| PATCH | `/stages/{id}` | Actualizar etapa (OWNER/ADMIN) |
| DELETE | `/stages/{id}` | Eliminar etapa (OWNER/ADMIN) |
| PATCH | `/projects/{id}/stages/reorder` | Reordenar etapas (OWNER/ADMIN) |
| GET | `/stages/{id}/tasks` | Tareas de una etapa |
| POST | `/stages/{id}/tasks` | Crear tarea |
| GET | `/tasks/{id}` | Obtener tarea |
| PATCH | `/tasks/{id}` | Actualizar tarea |
| PATCH | `/tasks/{id}/move` | Mover tarea de etapa |
| DELETE | `/tasks/{id}` | Eliminar tarea |

---

## Estructura del proyecto

```
project-manager/
├── backend/
│   └── src/main/java/com/projectmanager/backend/
│       ├── controller/     # UserController, ProjectController...
│       ├── service/        # Lógica de negocio por entidad
│       ├── repository/     # Interfaces JPA
│       ├── entity/         # Entidades JPA
│       └── model/          # DTOs y modelos de transferencia
├── frontend/
│   └── src/app/
│       ├── models/         # Interfaces de dominio TypeScript
│       ├── services/       # Servicios HTTP
│       ├── core/           # AuthService, interceptor, guard
│       ├── layout/         # Navbar y shell principal
│       ├── features/
│       │   ├── auth/       # Login y registro
│       │   ├── dashboard/  # Listado de proyectos
│       │   ├── kanban/     # Tablero, columnas, tarjetas
│       │   └── members/    # Gestión de miembros
│       └── shared/         # Componentes reutilizables (modal, toast, confirm)
└── docker-compose.yml
```

---

## Arranque local

### Requisitos

- Java 21
- Node.js 20 LTS
- Docker

### Base de datos

```bash
docker compose up -d
```

Levanta PostgreSQL 16 en el puerto 5432 (base de datos `projectmanager`, usuario `admin`, contraseña `admin`).

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Disponible en `http://localhost:8080`. El esquema se crea automáticamente al arrancar.

### Frontend

```bash
cd frontend
npm install
npx ng serve
```

Disponible en `http://localhost:4200`.

---

## Tests

### Backend — 99 tests, 100 % de cobertura

| Clase | Tests |
|---|---|
| UserServiceTest | 15 |
| TaskServiceTest | 14 |
| ProjectMemberServiceTest | 13 |
| StageServiceTest | 12 |
| UserControllerTest | 10 |
| ProjectServiceTest | 6 |
| TaskControllerTest | 6 |
| StageControllerTest | 5 |
| ProjectControllerTest | 5 |
| ProjectMemberControllerTest | 5 |
| ProjectPermissionServiceTest | 3 |
| ProjectMapperTest | 2 |
| UserMapperTest | 2 |
| BackendApplicationTests | 1 |

La cobertura se mide con JaCoCo sobre servicios, controladores y mappers (excluye entidades, modelos y configuración).

```bash
cd backend
./mvnw verify
# Informe en target/site/jacoco/index.html
```

### Frontend — 20 tests (Vitest)

Cubren `AuthService`, `ToastService`, `authGuard` y `authInterceptor`.

```bash
cd frontend
npx vitest run
```

### CI/CD

Dos flujos en GitHub Actions ejecutados en cada push a `main`:
- **ci.yml** — tests de frontend y backend con JaCoCo
- **gitleaks.yml** — escaneo de secretos y credenciales

---

## Decisiones técnicas destacadas

**Signals sobre Observables.** Todos los componentes exponen signals usando `toSignal()` para convertir respuestas HTTP. Simplifica los templates y elimina suscripciones manuales.

**`rxResource` para carga reactiva.** Declara el parámetro reactivo en `params` y la llamada HTTP en `stream`. Angular reejecutará la carga al cambiar el parámetro y expone `.reload()` para forzar recarga tras mutaciones.

```typescript
private readonly stagesResource = rxResource({
  params: () => this.id(),
  stream: ({ params: id }) => this.stageService.getByProject(id),
});
```

**Componentes presentacionales puros.** `TaskCardComponent` y `ProjectCardComponent` solo reciben datos por `input()` y emiten eventos por `output()`, sin inyecciones de servicios.

**Responsividad diferenciada.** En escritorio, todas las columnas son visibles en una fila con scroll horizontal. En móvil, una columna por pantalla con CSS scroll snap, sin JavaScript adicional.

**Orden determinista de tareas.** `HashSet` no garantiza orden; las tareas cambiaban de posición al actualizarse. Se resolvió con un comparador con tiebreaker por `id` en el mapper:

```java
private static final Comparator<Task> TASK_ORDER =
    Comparator.comparing(Task::getPosition, Comparator.nullsLast(Comparator.naturalOrder()))
              .thenComparing(Task::getId);
```

**Dark mode en controles nativos.** Los `<select>` del navegador no se adaptaban al modo oscuro. Se resolvió con `color-scheme: dark` en los tokens CSS, sin JavaScript.

---

## Posibles mejoras

| Horizonte | Mejora |
|---|---|
| Corto plazo | Drag & drop de tareas |
| Corto plazo | Autenticación con cookies HTTP-only: más persistente y protegida frente a XSS |
| Corto plazo | Sistema de invitaciones: los miembros aceptarían invitaciones en lugar de ser añadidos directamente |
| Corto plazo | Transferencia de propiedad de proyecto |
| Medio plazo | Notificaciones en tiempo real (WebSockets) |
| Medio plazo | Asignación de tareas a miembros |
| Medio plazo | Fechas límite y vista de calendario |
| Largo plazo | App móvil nativa (Capacitor) |
| Largo plazo | Integraciones externas (GitHub, Slack) |
