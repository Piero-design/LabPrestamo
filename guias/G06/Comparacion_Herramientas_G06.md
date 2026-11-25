# G06 — Comparación documental de RMAT y PILAR
**Fecha:** 24/11/2026 | **Producto:** LabPréstamo 0.2.0

## Objetivo
Comparar las actividades que cubren ambas herramientas según la guía G06, sin atribuir a RMAT una ejecución que no se ha realizado.

| Actividad | PILAR BASIC / PILAR RM | RMAT (Risk Management Additional Tools) |
|---|---|---|
| Inventario y valoración | Identifica activos y valora dimensiones de seguridad. En este trabajo se registraron seis activos. | Personaliza la biblioteca que consumen las herramientas EAR; no sustituye el inventario del proyecto. |
| Amenazas | Permite identificar amenazas y analizar sus riesgos. Se observaron las amenazas generadas por la biblioteca. | TSV: configura perfiles de amenazas según el contexto de operación. |
| Salvaguardas | Permite valorar medidas y estimar riesgos residuales; seis salvaguardas configuradas parcialmente en el trabajo. | KB: define protecciones complementarias para clases de activos. |
| Evaluación y protección | Compara fases potencial, actual, objetivo y recomendada. | EVL: adapta perfiles de protección y criterios sectoriales de evaluación. |
| Resultados | Proyecto nativo .mgr y capturas de riesgos; exportación restringida en evaluación. | Personalizaciones reutilizables de la biblioteca; no se generaron archivos RMAT. |
| Uso en el proyecto | Aplicación práctica con PILAR BASIC 2026.1.1 (versión reportada por la instalación). | Comparación documental; no se instaló ni ejecutó RMAT. |

## Conclusión y limitación
PILAR BASIC fue suficiente para construir un análisis inicial del sistema local y examinar distintas fases de tratamiento. RMAT serviría para adaptar perfiles a un contexto institucional específico, fuera del alcance del prototipo. La documentación pública del CCN identifica RMAT con el nombre indicado arriba; la guía académica no desarrolla la sigla ni adjunta su manual, por lo que la equivalencia queda pendiente de confirmación docente. No se declara haber probado RMAT ni una implementación completa de MAGERIT.

## Fuentes oficiales
- CCN, PILAR Basic: https://pilar.ccn-cert.cni.es/pilar/pilar-basic
- CCN, PILAR: https://pilar.ccn-cert.cni.es/pilar/pilar
- CCN, RMAT: https://pilar.ccn-cert.cni.es/pilar/rmat
- CCN, implementación: https://pilar.ccn-cert.cni.es/metodologia/implementacion
- Trabajo Final Calidad de Software V1, pág. 5, G06.
