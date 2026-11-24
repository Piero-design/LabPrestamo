# G06 — Gestión de riesgos

Aplicación de MAGERIT con PILAR BASIC 2026.1.1 en entorno académico de LabPréstamo 0.2.0. Responsable: Huarca Pallani Jael Emerson. Fecha de análisis y pruebas: 24/11/2026.

## Entregables
| Archivo | Contenido |
|---|---|
| [Informe_G06.md](Informe_G06.md) | Objetivo, procedimiento, resultados, análisis y conclusiones |
| [Inventario_Activos_G06.xlsx](Inventario_Activos_G06.xlsx) | Seis activos, dependencias y valoración |
| [Matriz_Riesgos_G06.xlsx](Matriz_Riesgos_G06.xlsx) | Ocho escenarios auxiliares y cuatro fases de PILAR |
| [Plan_Tratamiento_G06.xlsx](Plan_Tratamiento_G06.xlsx) | Salvaguardas, responsables propuestos e indicadores |
| [Comparacion_Herramientas_G06.md](Comparacion_Herramientas_G06.md) | RMAT frente a PILAR |
| [Proyecto_PILAR/LabPrestamo_G06.mgr](Proyecto_PILAR/LabPrestamo_G06.mgr) | Proyecto original editable |
| [Evidencias/](Evidencias/) | Figuras 01–13 |
| [verificar_controles.py](verificar_controles.py) | Script HTTP reproducible |
| [evidencia_controles.json](evidencia_controles.json) | Resultado de ejecución local |

## Reproducción de los controles
Desde la raíz del repositorio, con Java 21, Maven y Python 3.9 o superior:

```powershell
mvn spring-boot:run
```
En otra terminal:
```powershell
python .\guias\G06\verificar_controles.py
```
Ejecutar exclusivamente en la base de demostración local. El script genera una reserva persistente y sobrescribe el JSON anterior. Conservar la evidencia original si se realizan pruebas adicionales.

## Resultados del 24/11/2026
- CT-01: APROBADO; estudiante HTTP 403, estado PENDIENTE sin cambios.
- CT-02: APROBADO; HTTP 409 y 200, un registro (ID 2).
- HEAD evaluado: `f460c88cf85b138daf2c9473ed2b73e1614f3e5c`.
- PILAR: máximo current 6.3 y target 5.4 en confidencialidad; la disponibilidad permanece 5.4. Target no es reducción comprobada.

## Limitaciones
Modo evaluación: exportación bloqueada y catálogo de salvaguardas parcialmente disponible. Dependencias individuales documentadas fuera de PILAR; matriz auxiliar con escalas propias y residual previsto. RMAT comparado documentalmente, identificación pendiente de confirmación docente. Responsable de cada tratamiento propuesto por función; ratificación pendiente. Se declara asistencia IA en el informe.
