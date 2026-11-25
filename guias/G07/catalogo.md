# Catálogo G07

Ejecución: 24/11/2026, versión 0.2.0 + CH-03. Los 41 casos PU aprobaron. El manifiesto identifica las fuentes exactas. Cada prueba crea un doble JDBC nuevo, sin red ni base real. Los riesgos R-U1/R-U2/R-U3 se definen para esta evaluación: datos inválidos, doble asignación y transición/permisos indebidos. No reemplazan el registro general de riesgos.

| ID | Requisito | Unidad/objetivo | Datos y preparación | Resultado esperado | Riesgo |
|---|---|---|---|---|---|
| PU-01 | RF03 | Equipos: Crear equipo | Código E1, nombre y categoría con espacios | Un INSERT con valores recortados | R-U1 |
| PU-02 | RF03 | Equipos: Rechazar código vacío | Código con espacios | HTTP 400; ninguna escritura | R-U1 |
| PU-03 | RF03 | Equipos: Límite válido de nombre | Nombre de 120 caracteres | INSERT conserva los 120 caracteres | R-U1 |
| PU-04 | RF03 | Equipos: Exceso de nombre | Nombre de 121 caracteres | HTTP 400; ninguna escritura | R-U1 |
| PU-05 | RF03 | Equipos: Estado inválido | Estado ROTO | HTTP 400; ninguna escritura | R-U1 |
| PU-06 | RF03/RF08 | Equipos: Mantenimiento bloqueado | Equipo con 1 reserva/préstamo vigente | HTTP 409; ninguna escritura | R-U2 |
| PU-07 | RF03 | Equipos: Mantenimiento permitido | Equipo sin compromisos vigentes | UPDATE a MANTENIMIENTO para id 1 | R-U2 |
| PU-08 | RF03 | Equipos: Equipo inexistente | Consulta sin registros para id 99 | HTTP 404; ninguna escritura | R-U3 |
| PU-09 | RF04 | Equipos: Catálogo completo | Sin fechas; fila E1 preparada | Devuelve exactamente el catálogo preparado | R-U3 |
| PU-10 | RF04 | Equipos: Catálogo por intervalo | Mañana futuro, resultado AVAILABLE=false | Conserva resultado de disponibilidad del repositorio | R-U2 |
| PU-11 | RF05/RF06 | Reservas: Reserva válida | ana; equipo 1; inicio en 2 días; 1 hora; motivo con espacios | INSERT PENDIENTE con usuario, fechas y motivo recortado | R-U3 |
| PU-12 | RF05 | Reservas: Intervalo de cero duración | Inicio igual al fin | HTTP 400; ninguna escritura | R-U1 |
| PU-13 | RF05 | Reservas: Intervalo invertido | Fin anterior al inicio | HTTP 400; ninguna escritura | R-U1 |
| PU-14 | RF05 | Reservas: Fecha ausente | Inicio nulo | HTTP 400; ninguna escritura | R-U1 |
| PU-15 | RF05 | Reservas: Fecha pasada | Inicio hace 2 días | HTTP 400; ninguna escritura | R-U1 |
| PU-16 | RF05 | Reservas: Equipo en mantenimiento | STATE=MANTENIMIENTO | HTTP 409; ninguna escritura | R-U2 |
| PU-17 | RF04/RF05 | Reservas: Reserva superpuesta | Repositorio informa 1 conflicto | HTTP 409; ninguna escritura | R-U2 |
| PU-18 | RF05 | Reservas: Límite de motivo | Motivo de 500 caracteres; intervalo 1 minuto | INSERT conserva 500 caracteres | R-U1 |
| PU-19 | RF05 | Reservas: Motivo demasiado largo | Motivo de 501 caracteres | HTTP 400; ninguna escritura | R-U1 |
| PU-20 | RF05 | Reservas: Motivo ausente | Motivo nulo | HTTP 400; ninguna escritura | R-U1 |
| PU-21 | RF06 | Reservas: Consultar reservas propias | Usuario ana; fila preparada de ana | Devuelve resultado del repositorio | R-U3 |
| PU-22 | RF06/RF07 | Reservas: Consultar todas | Usuario nulo; lista vacía | Devuelve lista vacía | R-U3 |
| PU-23 | RF06 | Préstamos: Cancelar reserva propia | ana; PENDIENTE; intervalo futuro | UPDATE CANCELADA sobre id 7 | R-U3 |
| PU-24 | RF02/RF06 | Préstamos: Cancelar reserva ajena | Actor otro; propietario ana | HTTP 403; ninguna escritura | R-U3 |
| PU-25 | RF06 | Préstamos: Cancelar reserva iniciada | APROBADA; inicio pasado | HTTP 409; ninguna escritura | R-U3 |
| PU-26 | RF02/RF07 | Préstamos: Acción sin permiso | Aprobar con admin=false | HTTP 403; ninguna escritura | R-U3 |
| PU-27 | RF07 | Préstamos: Aprobar solicitud | PENDIENTE; equipo operativo; 0 conflictos | Estado APROBADA y responsable ana | R-U3 |
| PU-28 | RF07 | Préstamos: Rechazar con motivo | PENDIENTE; motivo con espacios | Estado RECHAZADA y motivo recortado | R-U3 |
| PU-29 | RF07 | Préstamos: Rechazar sin motivo | PENDIENTE; motivo vacío | HTTP 400; ninguna escritura | R-U3 |
| PU-30 | RF07 | Préstamos: Decisión duplicada | Reserva ya APROBADA | HTTP 409; ninguna escritura | R-U3 |
| PU-31 | RF08 | Préstamos: Entregar equipo | APROBADA; horario actual; 0 préstamos; condición Completo | UPDATE PRESTADO y condición inicial Completo | R-U3 |
| PU-32 | RF08 | Préstamos: Entregar sin aprobar | PENDIENTE | HTTP 409; ninguna escritura | R-U3 |
| PU-33 | RF08 | Préstamos: Entregar antes del horario | APROBADA; inicio en 2 días | HTTP 409; ninguna escritura | R-U3 |
| PU-34 | RF09 | Préstamos: Devolución con daño | PRESTADO; Pantalla rota; damaged=true | UPDATE DEVUELTO, condición y equipo MANTENIMIENTO | R-U3 |
| PU-35 | RF09 | Préstamos: Devolución sin daño | PRESTADO; Sin daños; damaged=false | Un UPDATE DEVUELTO; no cambia equipo | R-U3 |
| PU-36 | RF09 | Préstamos: Devolver sin préstamo | PENDIENTE | HTTP 409; ninguna escritura | R-U3 |
| PU-37 | RF07/RF09 | Préstamos: Acción desconocida | Acción inventada; encargado | HTTP 400; ninguna escritura | R-U3 |
| PU-38 | RF07 | Préstamos: Aprobar horario vencido | PENDIENTE; fin pasado | HTTP 409; ninguna escritura | R-U3 |
| PU-39 | RF07 | Préstamos: Aprobar mantenimiento | PENDIENTE; equipo MANTENIMIENTO | HTTP 409; ninguna escritura | R-U3 |
| PU-40 | RF08 | Préstamos: Entregar mantenimiento | APROBADA; horario actual; MANTENIMIENTO | HTTP 409; ninguna escritura | R-U3 |
| PU-41 | RF08 | Préstamos: Entregar con préstamo abierto | APROBADA; 1 préstamo abierto | HTTP 409; ninguna escritura | R-U2 |

## Verificaciones SQL complementarias

SQL-01: equipo libre disponible. SQL-02: reserva pendiente superpuesta bloquea. SQL-03: intervalos contiguos permiten disponibilidad. SQL-04: préstamo vencido bloquea consultas futuras. SQL-05: devolución no bloquea. SQL-06: mantenimiento no disponible. Se ejecutan contra H2 efímera y una conexión exclusiva por caso; son integración, no se cuentan entre las 41 unitarias.

## Defecto y cierre

DC-01 corresponde a PU-12: reemplazar `!end.isAfter(start)` por `end.isBefore(start)` acepta intervalos iguales. JUnit registra 1 fallo de aserción (no una excepción de infraestructura). El script restaura la fuente en `finally`. La suite posterior vuelve a aprobar. Ver `evidencias/defecto-junit.txt` y `evidencias/restauracion.txt`.
