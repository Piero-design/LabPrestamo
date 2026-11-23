# LabPréstamo

Aplicación web académica para reservar equipos de un laboratorio y gestionar su entrega y devolución. Incluye los dos roles, el flujo de préstamos y los diez requisitos funcionales propuestos.

## Tecnología

Java 17 o superior, Maven 3.6.3 o superior, Spring Boot 4.0.3, Spring Security, JDBC y H2 en archivo. Frontend en HTML, CSS y JavaScript, sin compilación de frontend. Un solo proceso para ejecutar la app.

Se eligió H2 local para simplificar la instalación respecto a la propuesta inicial de PostgreSQL. Las tablas se crean automáticamente y los datos se guardan en `data/`. Las fechas de reservas usan la hora local del servidor. Para trabajar en Perú ejecutar el servidor con la zona `America/Lima`.

## Ejecutar

```sh
./ejecutar.sh
```

En Windows o con Java ya configurado:

```sh
mvn spring-boot:run
```

Abrir http://localhost:8080. La primera ejecución descarga dependencias y crea las cuentas demo. Requiere conexión a Internet durante esa descarga.

También se puede generar un ejecutable:

```sh
mvn package
java -Duser.timezone=America/Lima -jar target/labprestamo-0.2.0.jar
```

Si JAVA_HOME es inválido, apuntarlo a la instalación de Java. El script lo resuelve automáticamente en macOS. Cambiar el puerto mediante `PORT=8081 ./ejecutar.sh`. El servidor escucha por defecto únicamente en el equipo local.

## Cuentas de demostración

- `estudiante` / `Estudiante123!` — Ana Torres.
- `estudiante2` / `Estudiante123!` — Luis Pérez.
- `encargado` / `Encargado123!` — responsable del laboratorio.

Son cuentas académicas públicas en este repositorio. Se crean solo si faltan. Las contraseñas se guardan con BCrypt. Las sesiones caducan tras 30 minutos de inactividad y las operaciones de escritura requieren token CSRF.

## Recorrido de uso

1. Ingresar como estudiante y consultar equipos en un horario futuro.
2. Solicitar una reserva indicando equipo, inicio, fin y motivo. Revisarla en Mis reservas.
3. Ingresar como encargado y aprobar o rechazar desde Gestión de préstamos.
4. Dentro del horario reservado, registrar entrega y condición inicial.
5. Registrar devolución y condición final. Marcar mantenimiento si existe daño.
6. Consultar el reporte por fecha de inicio y estado. Descargar los registros filtrados como CSV.

Para una demostración rápida, reservar desde el minuto siguiente y aprobar antes de ese momento. La entrega se habilita por la regla de negocio cuando el horario haya comenzado.

## Funcionalidades

- RF-01. Inicio y cierre de sesión.
- RF-02. Permisos del servidor por rol.
- RF-03. Registro y edición de equipos con código único y mantenimiento.
- RF-04. Consulta de disponibilidad por intervalo.
- RF-05. Reserva con validaciones y control de concurrencia.
- RF-06. Consulta y cancelación de reservas propias antes del inicio.
- RF-07. Aprobación o rechazo con motivo.
- RF-08. Entrega con responsable y condición inicial.
- RF-09. Devolución y cambio a mantenimiento por daño.
- RF-10. Reporte filtrado, préstamos vencidos y descarga CSV.

Las reservas pendientes y aprobadas ocupan el intervalo. Intervalos contiguos están permitidos. Un préstamo vencido abierto bloquea nuevas reservas. Cada equipo se bloquea dentro de una transacción al crear o cambiar reservas, evitando que dos solicitudes simultáneas asignen el mismo horario. Un equipo dañado puede conservar reservas futuras pendientes, pero su aprobación o entrega queda bloqueada hasta que vuelva a operativo. El encargado puede rechazar las pendientes y el estudiante puede cancelar las aprobadas antes del inicio.

## Estructura y nueve guías

- `src/main/java/pe/labprestamo/` — seguridad, persistencia y reglas de negocio.
- `src/main/resources/static/` — frontend.
- `src/main/resources/schema.sql` — esquema reproducible.
- [Guías G01 a G09](guias/README.md) — aplicación de los nueve temas y entregables pendientes.
- [Informe técnico](documentos/Informe_tecnico_Hito_1.docx).
- [Presentación](documentos/LabPrestamo_Hito_1.pptx).
- `mockups/` — diseños conceptuales de la propuesta.

## Datos y respaldo

Los archivos de `data/` están excluidos de Git. Para respaldar, detener la app y copiar toda esa carpeta. Para restaurar, detener la app y recuperar la copia completa. No eliminarla para actualizar la aplicación. `schema.sql` usa creación condicional y conserva registros existentes.

## Estado académico

La aplicación es la base para realizar después las pruebas JUnit, JMeter y Selenium. Los objetivos de rendimiento, usabilidad y cobertura todavía deben medirse. G05 requiere además SVN aunque el proyecto se comparta mediante GitHub. Los documentos originales describen la propuesta del primer entregable y conservan su fecha. La implementación actual usa H2 local y corresponde a una etapa posterior.

Se empleó asistencia de IA para elaborar la propuesta, mockups e implementación. El equipo debe validar el alcance con el docente y ejecutar las nueve guías con evidencias propias.
