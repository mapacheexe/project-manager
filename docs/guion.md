# Guión de defensa — Sistema de Gestión de Proyectos

Duración estimada: 13–15 minutos

---

## 1. Portada *(30 seg)*

"Buenos días. Voy a presentar mi proyecto final de ciclo: un sistema de gestión de proyectos colaborativo. Está construido con Angular en el frontend y Spring Boot en el backend, y está desplegado y accesible ahora mismo."

---

## 2. Índice *(10 seg)*

"Haré un recorrido por qué es el proyecto, la planificación, los roles, el diseño, la arquitectura, el stack, el modelo de datos, el frontend, el backend, la seguridad, los tests y las conclusiones."

---

## 3. ¿Qué es el proyecto? *(45 seg)*

"La aplicación es una herramienta visual y colaborativa de gestión de proyectos y tareas. Organiza el trabajo en un tablero con columnas y tarjetas para seguir el progreso en equipo. Los proyectos se gestionan por miembros con roles diferenciados: OWNER, ADMIN y MEMBER. Las columnas son personalizables según el flujo de trabajo del equipo. Y está disponible como aplicación web, móvil y de escritorio gracias a PWA —Progressive Web App—, que la hace instalable en cualquier dispositivo sin pasar por ninguna tienda."

---

## 4. Planificación *(30 seg)*

"El desarrollo se estructuró en 14 semanas desde marzo hasta mayo de 2026. Empecé por el backend completo, luego el setup del frontend, autenticación, dashboard, kanban, modales, UI avanzada, tests y por último la seguridad JWT."

---

## 5. Roles y permisos *(1 min)*

"Hay tres roles por proyecto. MEMBER puede ver y gestionar tareas. ADMIN además puede renombrar el proyecto, gestionar etapas y añadir o eliminar miembros. OWNER tiene control total, incluido eliminar el proyecto, pero no puede abandonarlo —debe eliminarlo si ya no lo quiere. Solo el OWNER puede asignar el rol OWNER a otro miembro."

---

## 6. Diseño visual *(1 min)*

"La página tiene una estructura de cabecera fija más área de contenido centrada con ancho máximo, lo que limita la longitud de línea y mantiene la legibilidad en pantallas grandes. La navegación es jerárquica de dos niveles: el dashboard como punto de entrada y el kanban como vista de trabajo.

El recorrido visual sigue un patrón Z en la barra de navegación —el ojo va del logo a la izquierda a los controles de usuario a la derecha—, un patrón de cuadrícula en el dashboard donde el usuario escanea las tarjetas buscando el proyecto, y un barrido horizontal por los encabezados de columna en el kanban.

La jerarquía visual se construye por tamaño y contraste en tres niveles. Las acciones de las tarjetas aparecen solo cuando el ratón pasa por encima, para reducir el ruido visual en reposo. El sistema de tokens CSS garantiza que el modo claro y oscuro se adapten cambiando un único archivo."

---

## 7. Arquitectura *(1.5 min)*

"La arquitectura es cliente-servidor desacoplada. El frontend vive en Vercel como una SPA —Single Page Application— estática servida desde CDN, una red de distribución de contenido. El backend es una API REST en Railway. Se comunican por HTTPS con JSON. En local se levanta todo con un solo comando. Esta separación facilita escalar cada capa independientemente."

---

## 8. Stack tecnológico *(45 seg)*

"Para el stack tecnológico se ha usado Angular para el frontend, Spring Boot en el backend y PostgreSQL para la base de datos. Angular conlleva TypeScript como lenguaje, y se ha elegido SCSS —del que acabamos de hablar— para el estilo. En el backend, Spring Boot 4 sobre Java 21 incluye Spring Security para la autenticación y JPA con Hibernate para el acceso a datos. Las migraciones de esquema las gestiona Liquibase. Para testing: JUnit 5, Mockito y JaCoCo en el backend, Vitest en el frontend. Y toda la infraestructura corre en Docker en local, con integración continua en GitHub Actions y despliegue en Vercel y Railway."

---

## 9. Modelo de datos *(1 min)*

"El modelo tiene cinco tablas. `users` guarda las credenciales. `project` representa cada proyecto. `user_project` es la tabla intermedia que une usuarios con proyectos y almacena el rol de cada uno. `stage` son las columnas del tablero, ordenadas por un campo `position`. Y `task` tiene una clave foránea al stage al que pertenece. Las migraciones las gestiona Liquibase, no Hibernate."

---

## 10. Frontend Angular *(1.5 min)*

"El flujo de una petición en el frontend es: el Router evalúa el AuthGuard, que comprueba si hay token. Si no hay, redirige al login. Si hay, el componente carga los datos mediante un servicio. Antes de que la petición salga, el AuthInterceptor inyecta automáticamente la cabecera de autorización. El estado reactivo usa Signals de Angular —`signal()` para estado local y `computed()` para derivados—, sin suscripciones manuales."

---

## 11. Backend Spring Boot *(1.5 min)*

"Cada petición al backend pasa primero por el `JwtAuthFilter`, que valida la firma del token y carga el usuario en el contexto de seguridad. Luego llega al Controller, que delega en el Service. El `ProjectPermissionService` comprueba que el usuario tiene el rol necesario dentro de ese proyecto, y lanza un error 403 si no lo tiene. Bean Validation lanza automáticamente un error 400 si los datos recibidos no cumplen las restricciones."

---

## 12. Seguridad JWT *(2 min)*

"En el login el cliente manda email y contraseña. El backend consulta la base de datos, verifica la contraseña con BCrypt —que usa un hash lento con sal, nunca texto plano— y si es correcta genera un token JWT firmado con HMAC-SHA256. El cliente guarda ese token localmente. En cada petición posterior lo manda en la cabecera de autorización. El `JwtAuthFilter` verifica la firma sin tocar la base de datos. Eso hace el sistema sin estado: cualquier instancia del backend puede validar el token, lo que facilita el escalado horizontal."

---

## 13. Tests y calidad *(1 min)*

"El backend tiene 95 tests con JUnit 5 y Mockito. Los tests de persistencia usan H2 en memoria —una base de datos real, no sustitutos artificiales—. La nomenclatura sigue el patrón BDD `givenX_whenY_thenZ`. JaCoCo bloquea el build si la cobertura baja del 80%. El frontend cubre el servicio de autenticación, el interceptor, el guard y las notificaciones con Vitest. En cada subida a GitHub Actions corren los dos procesos en paralelo."

---

## 14. Conclusiones *(1 min)*

"Como resumen, la aplicación está funcional y desplegada en producción, con 95 tests y cobertura superior al 80%. La arquitectura desacoplada la hace escalable y mantenible. Como líneas de mejora futuras, destacaría: notificaciones en tiempo real con WebSockets, asignación de tareas a miembros concretos, adjuntos y comentarios en tarjetas, estadísticas de progreso por proyecto, y subtareas con dependencias entre ellas."

---

## 15. Demo en vivo

"Si queréis probarlo en tiempo real, aquí tenéis el QR. Voy a hacer una demostración en vivo."
