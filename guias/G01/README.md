# G01 — Requerimientos de LabPréstamo

Responsable: Jael Emerson Huarca Pallani. Corte de avance: 24/11/2026, segundo entregable del 25/11/2026. ERS 0.3-borrador. Aplicación observada: 0.2.0, revisión Git `5a6840d`.

## Objetivo
Especificar y revisar los requisitos de LabPréstamo con criterios observables, usando IEEE 830 como referencia académica y manteniendo trazabilidad con las demás guías.

## Archivos de la guía

| Archivo | Contenido |
|---|---|
| [ERS_G01.md](ERS_G01.md) | 10 RF, 5 RNF, reglas, criterios, conflictos y organización. |
| [Elicitacion_G01.md](Elicitacion_G01.md) | Instrumentos de entrevista y observación. |
| [Resultados_Elicitacion_G01.md](Resultados_Elicitacion_G01.md) | Entrevista simulada, recorrido local y defecto observado. |
| [Cronograma_G01.xlsx](Cronograma_G01.xlsx) | Planificación general y seguimiento de G01/G06. |
| [Organigrama_G01.drawio](Organigrama_G01.drawio) / [PNG](Organigrama_G01.png) | Roles y distribución de las nueve guías. |
| [Comparacion_Herramientas_G01.md](Comparacion_Herramientas_G01.md) | Comparación GitHub Issues y REM, ficha técnica y resultados. |
| [Revision_Requisitos_G01.md](Revision_Requisitos_G01.md) | Revisión documental individual de los 15 requisitos y observaciones. |
| [Registrar_Requisitos_G01.ps1](Registrar_Requisitos_G01.ps1) | Script de registro y exportación de incidencias. |
| [Registro_Requisitos_G01.csv](Registro_Requisitos_G01.csv) | Registro exportado con IDs y enlaces reales. |
| [Evidencias/](Evidencias/) | Seis capturas del recorrido, dos de GitHub Issues y captura del defecto. |

## Resultados y estado
- Recorrido individual ejecutado con datos ficticios: solicitud #1, EQ-001, PENDIENTE → APROBADA → PRESTADO → DEVUELTO.
- Registro de 10 RF y 5 RNF ([Issues #1–#15](https://github.com/Piero-design/LabPrestamo/issues)); defecto [#16](https://github.com/Piero-design/LabPrestamo/issues/16) abierto.
- Revisión documental preliminar preparada; no equivale a verificación completa de los requisitos.
- Entrevista individual simulada: no hubo entrevistado real. Aceptación docente y validación con interesados pendientes.
- Pendientes: ratificar revisión, gestionar conflictos con G02, confirmar responsables de implementación y comunicar defecto a G02/G09, completar pruebas y actualizar línea base final tras aprobación.

## Reproducción del registro
Desde la raíz del repositorio, con GitHub CLI 2.103.3 y una cuenta autorizada, consultar `gh issue list --repo Piero-design/LabPrestamo --state all --limit 50`. El script de registro crea incidencias faltantes y exporta CSV; no ejecutarlo si solo se quiere consultar. No guardar tokens ni credenciales reales.

## Limitaciones y asistencia
La evidencia corresponde al entorno local y a un escenario de prueba. Las pruebas de seguridad, concurrencia, rendimiento, cobertura y usabilidad requieren evidencias específicas. Se empleó asistencia de IA para borradores, revisión documental y automatización del registro, contrastada con la propuesta y las evidencias suministradas. La ratificación del responsable y la aprobación docente no se presuponen.
