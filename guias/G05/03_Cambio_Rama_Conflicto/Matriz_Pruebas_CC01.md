# Matriz de verificación CC-01

**Versión de aplicación base:** 0.2.0 (verificar con el `pom.xml` de la copia de trabajo probada).  
**Cambio:** margen de preparación de 15 minutos entre reservas del mismo equipo.

| ID | Preparación | Resultado esperado | Estado |
|---|---|---|---|
| CC01-01 | La reserva A termina 11:00; la reserva B empieza 11:14 para el mismo equipo | Rechazar B | Pendiente de ejecutar |
| CC01-02 | La reserva A termina 11:00; la reserva B empieza 11:15 para el mismo equipo | Aceptar B, si las otras reglas se cumplen | Pendiente de ejecutar |
| CC01-03 | Mismos horarios que CC01-02, pero equipo diferente | Aceptar si el otro equipo está disponible | Pendiente de ejecutar |
| CC01-04 | La nueva reserva se superpone directamente con la existente | Rechazar | Pendiente de ejecutar |
| CC01-05 | Consultar disponibilidad a 14 minutos del final de una reserva | Mostrar el equipo como no disponible | Pendiente de ejecutar |
| CC01-06 | Aprobar una solicitud que no respeta el margen | Rechazar la aprobación | Pendiente de ejecutar |

## Registro de ejecución
Completar para cada caso: fecha, revisión SVN, datos usados, resultado obtenido, captura/reporte y responsable. No cambiar el estado a Aprobado sin ejecución real.
