# G01 — Lista de revisión de requisitos

Proyecto: LabPréstamo. Documento revisado: ERS 0.3-borrador. Fecha de preparación: 24/11/2026. Responsable de G01: Jael Emerson Huarca Pallani. Revisión documental asistida por IA, pendiente de ratificación por el responsable y el equipo. Aplicación observada: 0.2.0, revisión Git `5a6840d`.

## 1. Criterios
C = cumple documentalmente el criterio; O = observación que requiere aclaración o corrección. Claridad: significado y actor identificables. Consistencia: compatibilidad con reglas y demás requisitos. Completitud: datos, restricciones y casos relevantes especificados al nivel del alcance. Verificabilidad: condición observable y prueba prevista. Esta revisión evalúa la redacción; C no significa que la función haya superado pruebas ni que el requisito esté aprobado por interesados.

## 2. Revisión individual

| ID | Claridad | Consistencia | Completitud | Verificabilidad | Observación o acción | Evidencia de implementación disponible |
|---|---|---|---|---|---|---|
| RF-01 | C | C | C | C | Mantener prueba de credenciales inválidas y acceso tras cierre. | Fig. 1 y cambio de sesión; faltan escenarios negativos. |
| RF-02 | C | C | C | C | Verificar permisos por HTTP directo, no solo visibilidad de botones. | Interfaces diferenciadas; control de servidor pendiente de prueba. |
| RF-03 | C | C | C | C | Probar registro válido, código duplicado y mantenimiento. | Fig. 2 muestra mantenimiento; registro no probado en G01. |
| RF-04 | C | O | C | C | Confirmar con G02 la precisión sobre préstamo vencido y distinción de disponibles/no disponibles. | Fig. 2; faltan superposición y límites. |
| RF-05 | C | C | C | C | Ejecutar escenarios inválidos, límites y concurrencia. | Fig. 3; caso válido observado. |
| RF-06 | C | C | C | C | Probar cancelación propia y rechazo de acceso a solicitudes ajenas. | Fig. 3; consulta propia observada. |
| RF-07 | C | O | C | C | Resolver discrepancia de fecha de decisión con el esquema y evaluar DEF-G01-01. | Fig. 4 e incidencia #16; aprobación observada con mensaje incorrecto. |
| RF-08 | O | O | O | C | Confirmar si el solicitante es el receptor y precisar alcance de entrega dentro del intervalo; gestionar con G02. | Fig. 5; entrega válida observada; duplicidad no probada. |
| RF-09 | C | C | C | C | Probar devolución con daños y rechazo de repetición. | Fig. 6; devolución sin daños observada. |
| RF-10 | C | C | C | C | Preparar conjunto conocido y contrastar filtros, vencidos y totales. | Pendiente de prueba específica. |
| RNF-01 | C | C | C | C | Definir transacciones críticas y respuestas de error en plan G08. | Medición JMeter pendiente de incorporar. |
| RNF-02 | C | C | O | C | Definir catálogo de casos de acceso restringido y evidencia del hash. | Pendiente de verificación integral G06/G09. |
| RNF-03 | C | C | C | C | Ejecutar conjunto de 20 reservas y concurrencia. | Una solicitud persistió tras reinicio; insuficiente para el umbral. |
| RNF-04 | C | C | C | C | Ejecutar prueba con cinco participantes y registrar tiempos. | No medido; recorrido individual no acredita la meta. |
| RNF-05 | C | C | C | C | Identificar paquete de negocio, inclusiones/exclusiones y reporte JaCoCo. | Pendiente de incorporar resultados de G07. |

Todos los requisitos tienen identificador, fuente y prioridad común declarada en la ERS, condición observable y prueba prevista. Se vincularon a Issues #1–#15 en el mismo orden. Consultar `Registro_Requisitos_G01.csv` para enlaces individuales.

## 3. Revisión global de la ERS

| Criterio | Resultado documental | Observación |
|---|---|---|
| Propósito, alcance y restricciones | C | Un laboratorio, exclusiones y tres procesos. |
| Interesados y elicitación | O | Cliente e interesados propuestos; entrevista individual simulada, pendiente de aceptación y validación real. |
| Interfaces y supuestos | C | Entorno web y H2 declarados; cambio desde PostgreSQL pendiente de formalización. |
| 10 RF y 5 RNF | C | IDs únicos. |
| Prioridad y fuente | C | Alta propuesta; fuente común Hito 1/enunciado, pendiente de validación. |
| Criterios de aceptación | C | Condiciones observables; revisar precisiones RF-04/RF-08. |
| Conflictos | O | C-01 a C-05 documentados; resoluciones propuestas, no aprobadas. |
| Cronograma y roles | C | Archivos editables y reparto confirmado de guías; cargos propuestos. |
| Herramienta y trazabilidad | C | 15 requisitos registrados, defecto separado, CSV y capturas. |
| Aprobación de interesados | O | No obtenida. |

## 4. Dictamen documental preliminar
ERS utilizable como borrador de trabajo para el segundo entregable, con observaciones abiertas. No constituye línea base aprobada. Priorizar la validación de interesados, resolución de C-03 a C-05, comunicación del defecto #16 y enlace de pruebas reales de G06–G09. La revisión asistida debe ser ratificada por el responsable; no se atribuye una firma ni aprobación a terceros.

Fecha de ratificación: pendiente. Decisión del responsable/equipo: pendiente. Evidencia de validación: pendiente.
