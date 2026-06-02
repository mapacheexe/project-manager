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

"Herramienta de gestión de proyectos y tareas. Organiza el trabajo en un tablero con columnas y tarjetas. Los proyectos se gestionan por miembros con roles. Las columnas son personalizables según el flujo de trabajo del equipo. Disponible como aplicación web, móvil y de escritorio gracias a PWA —Progressive Web App—."

---

## 4. Planificación *(30 seg)*

"El desarrollo se estructuró en 14 semanas desde marzo hasta mayo de 2026, siguiendo un enfoque incremental: cada fase añade funcionalidad sobre la anterior sin romper lo que ya funcionaba. Empecé por el backend completo para tener la API estable antes de construir el frontend encima. Luego el setup del frontend, autenticación, dashboard, kanban, modales, UI avanzada, tests y por último la seguridad JWT."

---

## 5. Roles y permisos *(1 min)*

"Hay tres roles por proyecto. MEMBER puede ver y gestionar tareas. ADMIN además gestiona el proyecto: etapas, miembros y configuración. OWNER tiene control total e incluye eliminar el proyecto."

---

## 6. Diseño visual *(1 min)*

"La navegación es jerárquica en dos niveles: el dashboard como punto de entrada y el kanban como vista de trabajo.

El recorrido visual varía según la pantalla: patrón Z en la barra de navegación, cuadrícula en el dashboard y barrido horizontal en el kanban. La jerarquía visual se construye con tres niveles de tamaño y contraste. Las acciones de las tarjetas solo aparecen al pasar el ratón por encima, para reducir el ruido visual. El modo claro y oscuro se gestionan desde un único archivo de variables CSS.

En cuanto al diseño responsivo: en móvil las columnas del tablero se encajan al deslizar gracias a scroll snap, lo que facilita la navegación sin tocar JavaScript. En escritorio se muestran en un layout fijo de columnas lado a lado."

---

## 7. Arquitectura *(1.5 min)*

"La arquitectura es cliente-servidor desacoplada: cada capa es completamente independiente, lo que facilita la escalabilidad y el mantenimiento futuro.

El frontend es una SPA —Single Page Application—, es decir, un conjunto de ficheros estáticos: HTML, JavaScript y CSS. Al no requerir ningún proceso de servidor, no necesita Docker: se despliega directamente en Vercel, que lo distribuye globalmente a través de su red de servidores CDN —Content Delivery Network—.

El backend expone una API REST: una serie de URLs a las que el frontend hace peticiones HTTP para obtener o modificar datos. Corre en Railway dentro de un contenedor Docker, junto a la base de datos PostgreSQL. PostgreSQL es una base de datos relacional gratuita, robusta y ampliamente adoptada en el sector. Además, al usar JPA como capa de acceso a datos, cambiar de base de datos sería tan sencillo como cambiar el driver de conexión.

En local, todo el entorno de desarrollo se levanta con un único comando: `docker compose up`."

---

## 8. Stack tecnológico *(45 seg)*

"Tres capas principales: frontend con Angular, TypeScript y SCSS; backend con Spring Boot 4 sobre Java 21, Spring Security y JPA con Hibernate; base de datos PostgreSQL con migraciones gestionadas por Liquibase. La infraestructura —Docker en local, GitHub Actions para CI/CD, Vercel y Railway para el despliegue— automatiza todo el ciclo. Para garantizar la calidad: JUnit 5, Mockito y JaCoCo en el backend, Vitest en el frontend."

---

## 9. Modelo de datos *(1 min)*

"El modelo tiene cinco tablas, bastante intuitivas. Destacaría dos decisiones de diseño: `user_project` modela la relación entre usuarios y proyectos como una entidad propia, lo que permite añadir el campo `role` a esa relación. Y `stage` representa las columnas del tablero: cada etapa tiene un campo `position` que determina el orden en que se muestran, y las tareas tienen una clave foránea que las vincula a su etapa. Las migraciones de esquema las gestiona Liquibase, no Hibernate."

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
