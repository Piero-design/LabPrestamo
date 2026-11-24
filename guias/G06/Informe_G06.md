# G06 — Gestión de riesgos de LabPréstamo
**Versión del informe:** 1.0 | **Fecha:** 24/11/2026 | **Responsable de la guía:** Huarca Pallani Jael Emerson

## 1. Objetivo y alcance
Aplicar un análisis de riesgos basado en MAGERIT al sistema de información que soporta las reservas y préstamos de equipos del laboratorio. Identificar activos, dependencias, amenazas y salvaguardas; estimar riesgos, priorizar tratamientos y verificar dos controles existentes del producto. Se trabajó con LabPréstamo 0.2.0, Spring Boot 4.0.3, Java 21.0.10, H2 persistente local y cuentas ficticias. No se incluyen datos reales, infraestructura institucional ni auditoría de producción. La valoración es juicio técnico académico, no estadística histórica.

## 2. Inventario y dependencias
Dimensiones: D disponibilidad, I integridad, C confidencialidad, A autenticidad, T trazabilidad. Valores preliminares de 1 (bajo) a 5 (muy alto); guían la discusión y no se presentan como escala nativa de PILAR. Una dependencia X→Y significa que X necesita Y. La tabla conserva la escala auxiliar del borrador. Las dependencias se documentan en Excel; la edición BASIC utilizada se trabajó por dominios, sin modelar individualmente estas relaciones. No se duplican valores repercutidos.

| ID | Activo | Tipo | D | I | C | A | T | Dependencias |
|---|---|---|---:|---:|---:|---:|---:|---|
| A01 | Servicio de reservas y préstamos | Servicio esencial | 5 | 5 | 3 | 4 | 4 | A02,A03,A04,A05 |
| A02 | Registros de reservas y devoluciones | Información | 4 | 5 | 3 | 4 | 5 | A05 |
| A03 | Cuentas, roles y credenciales | Información | 4 | 5 | 5 | 5 | 4 | A05 |
| A04 | Aplicación web LabPréstamo | Software | 5 | 5 | 3 | 4 | 4 | A05,A06 |
| A05 | Base de datos H2 | Software/soporte de almacenamiento | 5 | 5 | 4 | 4 | 4 | A06 |
| A06 | Equipo anfitrión del sistema | Infraestructura | 5 | 4 | 3 | 3 | 3 | Ninguna dentro del alcance |

El servicio soporta consulta, reserva, aprobación, entrega y devolución. Los registros y cuentas dependen del almacenamiento; aplicación y motor dependen del entorno de ejecución. En PILAR se registraron los seis activos; los valores nativos visibles se detallan en la sección 5.

## 3. Amenazas, degradación y evaluación inicial
Método complementario para priorización preliminar, no sustituto de MAGERIT/PILAR. P = plausibilidad ordinal: 1 rara, 2 poco probable, 3 posible, 4 probable, 5 muy probable durante el horizonte académico de operación. Degradación de la dimensión afectada: 20 % limitada, 40 % parcial, 60 % considerable, 80 % grave, 100 % total; son estimaciones, no observaciones. Impacto ordinal I = techo(valor de la dimensión × degradación/100). Riesgo orientativo R=P×I; 1–4 bajo, 5–9 medio, 10–16 alto, 17–25 crítico. Los puntajes no se convierten a la escala nativa de PILAR. Las amenazas y valoraciones requieren validación del equipo.

| ID | Activo | Escenario de amenaza | Dim. | P | Degrad. | Impacto | R | Nivel | Vínculo |
|---|---|---|---|---:|---:|---:|---:|---|---|
| R01 | A04 | Acceso no autorizado sin sesión | C | 4 | 100% | 3 | 12 | Alto | RF-01/RNF-02 |
| R02 | A01 | Estudiante aprueba, entrega o devuelve sin autorización | I | 4 | 100% | 5 | 20 | Crítico | RF-02/RNF-02 |
| R03 | A05 | Borrado o corrupción del archivo persistente | D | 3 | 100% | 5 | 15 | Alto | RNF-03 |
| R04 | A03 | Exposición de credenciales o hashes | C | 3 | 100% | 5 | 15 | Alto | RNF-02 |
| R05 | A02 | Dos reservas simultáneas incompatibles | I | 4 | 100% | 5 | 20 | Crítico | RF-05/RNF-03 |
| R06 | A06 | Interrupción del servidor por fallo de entorno | D | 3 | 100% | 5 | 15 | Alto | RNF-01 |
| R07 | A02 | Entrada de fechas o motivos inválidos | I | 4 | 60% | 3 | 12 | Alto | RF-05 |
| R08 | A02 | Repetición de una devolución y alteración del historial | I | 3 | 80% | 4 | 12 | Alto | RF-09 |

Justificación: los formularios y rutas HTTP admiten interacciones repetidas, existen distintos roles y la persistencia depende del entorno local. Se priorizan permisos y concurrencia por su efecto directo sobre los tres procesos. Los niveles son potenciales, antes de acreditar efectividad de controles; no representan una auditoría de producción.

## 4. Plan de tratamiento
Responsables propuestos por función, pendientes de asignación nominal por el equipo. Las fechas son objetivos de comprobación, excepto los casos CT-01 y CT-02, ejecutados el 24/11/2026. Esfuerzo en horas-persona estimadas. Residual previsto bajo la misma escala de la matriz auxiliar suponiendo eficacia del control; el impacto se mantiene, cambia P. Se complementa con la fase target de PILAR (sección 5); no son escalas equivalentes.

| Riesgo | Salvaguarda / respuesta | Responsable propuesto | Fecha objetivo | Esfuerzo | Indicador | P residual | R residual previsto |
|---|---|---|---|---:|---|---:|---:|
| R01 | Exigir sesión en rutas protegidas | Desarrollo seguridad | 25/11 | 1 h | 100% de intentos sin sesión rechazados | 1 | 3 bajo |
| R02 | Autorizar /api/admin por rol en servidor (CT-01) | Desarrollo seguridad + responsable G06 | 24/11 | 2 h | 403 estudiante con CSRF válido; estado no cambia | 1 | 5 medio |
| R03 | Copiar data/ con aplicación detenida y comprobar restauración | Configuración/BD | 01/12 | 2 h | Registros restaurados coinciden con respaldo | 1 | 5 medio |
| R04 | BCrypt, proteger datos y restringir cuentas demo al entorno local | Desarrollo seguridad | 01/12 | 2 h | Hash adaptativo y acceso restringido | 2 | 10 alto |
| R05 | Transacción y bloqueo por equipo (CT-02) | Desarrollo negocio + responsable G06 | 24/11 | 3 h | 1 solicitud aceptada, 1 rechazada y un registro | 1 | 5 medio |
| R06 | Guía de arranque y recuperación, monitorización de entorno | Configuración | 01/12 | 2 h | Recuperación documentada y disponibilidad comprobada | 2 | 10 alto |
| R07 | Validación del lado servidor | Desarrollo negocio | 25/11 | 1 h | Fechas inválidas rechazadas sin inserción | 1 | 3 bajo |
| R08 | Comprobar estado PRESTADO antes de devolución | Desarrollo negocio | 01/12 | 1 h | Segunda devolución rechazada sin modificar historial | 1 | 4 bajo |

Controles identificados en la revisión preliminar del código: autenticación/autorización y BCrypt (SecurityConfig.java); validación, transacciones y bloqueo por equipo, y verificación de estado antes de devolver (LabService.java). Respaldo y recuperación de servicio: procedimientos propuestos; no se acredita ejecución. El riesgo residual de R04 y R06 sigue alto en la hipótesis de valoración; revisar controles adicionales y aceptación explícita del responsable.

## 5. Procedimiento realizado en PILAR BASIC
Se configuró el proyecto LABPRESTAMO-G06 (LabPréstamo – Gestión de riesgos), clasificación SIN CLASIFICAR, dominio Base, en Windows 11, PILAR BASIC 2026.1.1, licencia de evaluación (versión informada por la instalación). Se guardó el proyecto nativo `Proyecto_PILAR/LabPrestamo_G06.mgr`. Se registraron A01–A03 como activos esenciales, A04–A05 como aplicaciones/software y A06 como equipo anfitrión. Se valoraron las cinco dimensiones D/I/C/A/T y se revisaron amenazas del catálogo de la herramienta, entre ellas repudio, fallos lógicos, vulnerabilidades, errores de mantenimiento, manipulación, fallos físicos, suministro eléctrico y accesos no autorizados. Los ocho escenarios de la sección 3 son documentación complementaria: no se afirma una correspondencia individual exacta ni que sus probabilidades/degradaciones se hayan introducido en PILAR.

### 5.1 Valoración nativa
| Activo / dominio | D | I | C | A | T |
|---|---:|---:|---:|---:|---:|
| A01 | 7 | 7 | 4 | 7 | 7 |
| A02 | 7 | 7 | 7 | 7 | 7 |
| A03 | 7 | 7 | 7 | 7 | 7 |
| Base | 7 | 7 | 7 | 7 | 7 |

Fuente: Figura 03. DP quedó sin valoración. A04–A06 se registraron en el dominio Base, sin valores individuales visibles. Las dependencias se explicitaron documentalmente, no se acreditó su modelado individual en la herramienta.

### 5.2 Salvaguardas
| Código | Salvaguarda | Current | Target | Justificación |
|---|---|---|---|---|
| IA.1 | Política | L0 | L2 | Formalización propuesta |
| IA.2 | Procedimientos | L0 | L2 | Procedimientos propuestos |
| IA.3.1 | Identificación única | L1 | L3 | Cuentas diferenciadas; madurez estimada |
| IA.4.1 | Registro de usuarios | L1 | L2 | Registro de cuentas demo; formalización pendiente |
| IA.4.3 | Auditoría de gestión de cuentas | L0 | L2 | Auditoría propuesta, no verificada |
| IA.7 | Canal seguro | L0 | L2 | HTTP local; no se acreditó TLS |

Los niveles consignados no equivalen a implementación comprobada. Otras familias de salvaguardas quedaron sin valoración por las restricciones del modo evaluación. Respaldo, recuperación y configuración segura adicional son propuestas documentales.

### 5.3 Resultados de riesgo nativo
| Fase | Activo | D | I | C | A | T |
|---|---|---:|---:|---:|---:|---:|
| Potencial | A01 | 5.4 | 5.4 | 4.5 | 5.1 | 5.7 |
| Potencial | A02 | 5.4 | 5.4 | 6.3 | 5.9 | 5.7 |
| Potencial | A03 | 5.4 | 5.4 | 6.3 | 5.9 | 5.7 |
| Current | A01 | 5.4 | 5.4 | 4.5 | 5.1 | 5.6 |
| Current | A02 | 5.4 | 5.4 | 6.3 | 5.9 | 5.6 |
| Current | A03 | 5.4 | 5.4 | 6.3 | 5.9 | 5.6 |
| Target | A01 | 5.4 | 5.1 | 3.7 | 5.1 | 4.8 |
| Target | A02 | 5.4 | 5.1 | 5.4 | 5.1 | 4.8 |
| Target | A03 | 5.4 | 5.1 | 5.4 | 5.1 | 4.8 |
| PILAR | A01 | 2.4 | 2.4 | 1.6 | 2.2 | 2.7 |
| PILAR | A02 | 2.4 | 2.4 | 3.3 | 2.9 | 2.7 |
| PILAR | A03 | 2.4 | 2.4 | 3.3 | 2.9 | 2.7 |

Fuentes: Figuras 06–09. Se transcribieron los valores de pantalla; no se realizó conversión a la escala auxiliar 1–25. Leyenda de PILAR (Figura 10): 0 despreciable, 1 bajo, 2 medio, 3 alto, 4 muy alto, 5 crítico, 6 muy crítico, 7 extremadamente crítico, 8 desastre y 9 catástrofe.

El máximo actual de confidencialidad (6.3) corresponde a A02 y A03. La fase objetivo proyecta máximos de D=5.4, I=5.1, C=5.4, A=5.1, T=4.8; no se reduce el máximo de disponibilidad. La recomendación ofrece valores menores (D=2.4, I=2.4, C=3.3, A=2.9, T=2.7), pero no acredita implantación. Se recomienda revisar respaldo, restauración, continuidad y protección de credenciales antes de aceptar el residual.

### 5.4 Restricciones
La licencia de evaluación ocultó parte del catálogo y deshabilitó la exportación del mapa (Figura 11). Se conservó el archivo nativo .mgr y las capturas, opción admitida por la guía G06. Los Excel son transcripciones y estimaciones complementarias, no exportaciones originales. No se recuperaron frecuencias, degradaciones ni impactos nativos por amenaza; esos parámetros se documentan en la matriz auxiliar. No se afirma una implementación completa de MAGERIT.

## 6. Implementación existente y verificación de tratamientos
Se verificaron dos controles ya incorporados al producto: autorización en rutas administrativas (SecurityConfig.java y LabService.java) y prevención de reservas superpuestas mediante transacción y bloqueo por equipo (LabService.java), según revisión preliminar del código. Las pruebas HTTP se ejecutaron sobre `http://localhost:8080/api` con Python 3.13.9, sin dependencias externas, el 24/11/2026 a las 15:33:12 UTC-05. Commit registrado por el script: `f460c88cf85b138daf2c9473ed2b73e1614f3e5c`. El script estaba sin seguimiento de Git al ejecutarse; el hash identifica HEAD de la aplicación, no una revisión ya publicada del propio script.

| Caso | Riesgo | Esperado | Observado | Estado |
|---|---|---|---|---|
| CT-01 | R02 | Estudiante autenticado no puede aprobar reserva; 403 y estado sin cambios | HTTP 403; listado encargado HTTP 200; PENDIENTE antes y después | APROBADO |
| CT-02 | R05 | Una reserva aceptada, otra 409, un único registro | HTTP 409 y 200; un registro, ID 2 | APROBADO |

Datos: equipo ID 1; intervalo 01/12/2026 15:33–16:03; motivo G06-824d111b114e. Se usaron dos sesiones ESTUDIANTE y una ENCARGADO. Evidencia: `evidencia_controles.json`, `verificar_controles.py`, Figuras 12 y 13. Reproducción desde la raíz del repositorio: iniciar `mvn spring-boot:run` y ejecutar `python .\guias\G06\verificar_controles.py` en otra terminal. La prueba crea una reserva persistente; no usar con datos de producción. No repetir sin necesidad, pues se genera nueva evidencia y otra reserva.

Resultado: 2 de 2 casos aprobados (100% de los casos ejecutados). CT-01 verifica una aprobación específica y no todas las rutas de RF-02. CT-02 verifica un par de solicitudes concurrentes, no todas las cargas ni condiciones de carrera. No se midió el cambio de riesgo real ni se garantiza seguridad integral. La reserva se conservó como evidencia.

## 7. Comparación RMAT y PILAR
Ver `Comparacion_Herramientas_G06.md`. PILAR BASIC fue aplicado al análisis del producto; RMAT (Risk Management Additional Tools) permite personalizar perfiles de protección EVL, amenazas TSV y protecciones adicionales KB según el CCN. No se instaló RMAT. Como la guía no desarrolla la sigla, su identificación queda pendiente de validación docente.

## 8. Análisis y conclusiones
Se registraron seis activos, se documentaron sus dependencias y ocho escenarios con respuesta, responsable funcional, fecha, esfuerzo, indicador y residual previsto. Se obtuvo un proyecto nativo de PILAR y se compararon cuatro fases de riesgo. Los riesgos R02 y R05 fueron críticos en la matriz auxiliar inicial; se verificaron las medidas de autorización y concurrencia con resultados satisfactorios. R04 y R06 conservan prioridad alta en la proyección auxiliar, y el riesgo objetivo de disponibilidad no disminuye en PILAR. Los controles restantes requieren comprobación y los responsables propuestos requieren ratificación del equipo. El paquete documental satisface los componentes principales de entrega; la aceptación académica y la revisión de limitaciones corresponden al docente y al equipo.

## 9. Archivos y evidencias
- `Inventario_Activos_G06.xlsx`: inventario, dependencias y valoración nativa.
- `Matriz_Riesgos_G06.xlsx`: ocho escenarios, resultados PILAR, comparación y escalas.
- `Plan_Tratamiento_G06.xlsx`: acciones, salvaguardas y pruebas.
- `Comparacion_Herramientas_G06.md`: comparación documental y fuentes.
- `Proyecto_PILAR/LabPrestamo_G06.mgr`: proyecto original local.
- `verificar_controles.py` y `evidencia_controles.json`: reproducción y resultado real de controles.

### Registro de figuras
- Figura 01. Configuración del proyecto. [Ver evidencia](Evidencias/Figura_01_Configuracion_Proyecto.png)
- Figura 02. Inventario de seis activos. [Ver evidencia](Evidencias/Figura_02_Inventario_Activos.png)
- Figura 03. Valoración de activos y dominio Base. [Ver evidencia](Evidencias/Figura_03_Valoracion_Activos.png)
- Figura 04. Amenazas identificadas. [Ver evidencia](Evidencias/Figura_04_Identificacion_Amenazas.png)
- Figura 05. Salvaguardas configuradas parcialmente. [Ver evidencia](Evidencias/Figura_05_Configuracion_Salvaguardas.png)
- Figura 06. Riesgo potencial. [Ver evidencia](Evidencias/Figura_06_Riesgo_Potencial.png)
- Figura 07. Riesgo actual. [Ver evidencia](Evidencias/Figura_07_Riesgo_Actual.png)
- Figura 08. Riesgo residual objetivo. [Ver evidencia](Evidencias/Figura_08_Riesgo_Residual_Objetivo.png)
- Figura 09. Riesgo recomendado. [Ver evidencia](Evidencias/Figura_09_Riesgo_Recomendado.png)
- Figura 10. Leyenda de niveles. [Ver evidencia](Evidencias/Figura_10_Leyenda_Riesgos.png)
- Figura 11. Limitación de exportación. [Ver evidencia](Evidencias/Figura_11_Limitacion_Exportacion.png)
- Figura 12. Pruebas de controles HTTP. [Ver evidencia](Evidencias/Figura_12_Verificacion_Controles.png)
- Figura 13. Reserva de prueba en el listado del encargado. [Ver evidencia](Evidencias/Figura_13_Reserva_Prueba_Controles.png)

## 10. Referencias y declaración de asistencia
- Trabajo Final Calidad de Software V1, pág. 5 (G06), y pág. 8 (paquete de entrega).
- Informe técnico Hito 1 de LabPréstamo y ERS G01.
- https://pilar.ccn-cert.cni.es/metodologia/implementacion
- https://pilar.ccn-cert.cni.es/pilar/pilar-basic
- https://pilar.ccn-cert.cni.es/pilar/pilar
- https://pilar.ccn-cert.cni.es/pilar/rmat
- Repositorio: https://github.com/Piero-design/LabPrestamo ; revisión del producto registrada en la prueba: f460c88.
- Se utilizó asistencia de IA para estructurar el informe, diseñar la matriz auxiliar, preparar el script de prueba y organizar las evidencias; la configuración y ejecución local fueron realizadas por el responsable G06. Los resultados HTTP y las cifras de PILAR proceden de las evidencias compartidas. No se declara certificación ni cumplimiento total de MAGERIT.
