# Paquete de actividades pendientes G05 - LabPréstamo

Este paquete prepara el cambio funcional de rama, el conflicto controlado, su resolución y las operaciones posteriores de merge, etiqueta, recuperación y respaldo. **No contiene capturas falsas ni commits ya ejecutados.** Los pasos marcados como pendientes deben ejecutarse en el equipo donde está el repositorio SVN.

## Situación real confirmada por capturas del usuario

| Revisión | Operación observada | Estado |
|---|---|---|
| r1 | Repositorio SVN creado | Realizada |
| r2 | Estructura `trunk/branches/tags` y carpeta `trunk/LabPrestamo` | Realizada |
| r3 | Carga inicial de 58 elementos en `trunk/LabPrestamo` | Realizada |
| r4 | Política de gestión de configuración (DOCX y MD) | Realizada |
| r5 | Comparación SVN, Git y GitHub (DOCX y MD) | Realizada |
| r6 | Rama `branches/feature/G05-tiempo-preparacion` creada desde r5 | Realizada |
| r7 | Cambio funcional de 15 min en la rama | Pendiente |
| r8 | Cambio concurrente controlado en trunk (10 min) | Pendiente |
| r9 | Merge rama -> trunk y resolución a 15 min | Pendiente |
| r10 | Actualización del registro G05 con resultados y evidencias | Pendiente |
| r11 | Etiqueta `tags/v1.0.0` | Pendiente |

Las revisiones r1 y r2 acreditan operaciones de creación/estructura, pero para el mínimo de ocho revisiones significativas conviene documentar claramente las revisiones de contenido y de integración. La secuencia r3-r10 ofrece ocho cambios/operaciones rastreables; el docente puede decidir si la copia de rama r6 y el tag r11 cuentan en su criterio de significancia. No se deben fabricar commits vacíos.

## A. Cambio funcional que se implementará

Se propone exigir **15 minutos de separación entre dos reservas del mismo equipo**. La validación se aplica a:

- consulta de disponibilidad, para que la interfaz no ofrezca un equipo como disponible si no respeta el margen;
- creación de reserva, para impedir la reserva incompatible;
- aprobación de una solicitud, porque usa el mismo control de disponibilidad.

Los archivos `codigo/rama_15min/.../LabService.java` y `codigo/resolucion_final_15min/.../LabService.java` contienen el código con 15 minutos. El archivo `codigo/trunk_conflicto_10min/.../LabService.java` representa una modificación concurrente de 10 minutos solo para producir el conflicto controlado de la práctica.

### Prueba manual propuesta (no marcar como ejecutada hasta realizarla)

1. Crear/aprobar una reserva de prueba del mismo equipo de 10:00 a 11:00.
2. Intentar otra reserva desde 11:14: debe rechazarse por no respetar los 15 minutos.
3. Intentar otra reserva desde 11:15: debe aceptarse, si el equipo está operativo y se cumplen las demás reglas.
4. Repetir el criterio en consulta de disponibilidad y aprobación.

Use fechas futuras y un equipo de pruebas. Limpie las reservas de demostración o use otro equipo después de la verificación. No cambie el reloj del equipo.

## B. Registrar r7: cambio en la rama

1. En `LabPrestamo_Rama_G05_WC2`, reemplace `src/main/java/pe/labprestamo/LabService.java` con el archivo `codigo/rama_15min/src/main/java/pe/labprestamo/LabService.java`.
2. Desde la raíz de `LabPrestamo_Rama_G05_WC2`, ejecute `mvn test` o `mvn package` si Maven está disponible. Si no puede ejecutarlo, registre el impedimento; no afirme que la prueba pasó.
3. Realice la prueba manual anterior y guarde capturas reales.
4. Clic derecho en la copia de trabajo -> `SVN Commit...`.
5. Mensaje: `feat(reservas): exigir 15 minutos entre reservas`.

## C. Registrar r8: cambio concurrente en trunk para conflicto controlado

Esta modificación es una prueba académica de concurrencia de cambios. No representa un requisito aprobado ni una regla que deba quedar liberada.

1. En `LabPrestamo_SVN_WC`, ejecute primero `SVN Update` para llevar la copia de trabajo a la revisión más reciente (actualmente r6).
2. Reemplace `src/main/java/pe/labprestamo/LabService.java` con `codigo/trunk_conflicto_10min/src/main/java/pe/labprestamo/LabService.java`.
3. Ejecute la compilación/pruebas aplicables.
4. Realice commit en la copia de trabajo de trunk con mensaje: `test(G05): registrar propuesta concurrente de margen de 10 minutos`.
5. Guarde la captura del commit r8.

## D. Merge y conflicto: registrar r9

1. Compruebe que la copia de trunk está en la última revisión (`SVN Update`).
2. Clic derecho sobre la raíz de `LabPrestamo_SVN_WC` -> `TortoiseSVN` -> `Merge...`.
3. Seleccione `Merge a range of revisions`.
4. URL to merge: la URL local del repositorio que termina en `/branches/feature/G05-tiempo-preparacion`.
5. Rango: integrar los cambios de la revisión r6 a r7 (si el cuadro pide desde/hasta, use From 6, To 7). No seleccione reintegración de rama si el asistente propone otra operación; siga la opción de rango.
6. Ejecutar la fusión. Se espera un conflicto textual en la línea `PREPARATION_MINUTES`, porque trunk tiene 10 y la rama tiene 15.
7. En la copia de trunk, reemplace el `LabService.java` conflictivo con `codigo/resolucion_final_15min/src/main/java/pe/labprestamo/LabService.java`. Este archivo conserva 15 minutos y la lógica de margen aplicada a disponibilidad y reserva.
8. Clic derecho en el archivo -> `TortoiseSVN` -> `Resolved` (o marque como resuelto desde la ventana de conflictos).
9. Ejecute pruebas/compilación y repita la prueba manual. Conserve capturas del conflicto y de la resolución.
10. Realice commit de la fusión en trunk con mensaje: `merge(G05): integrar margen de preparacion de 15 minutos y resolver conflicto`.

## E. r10: cerrar el registro de actividad G05

Después de que r7-r9 hayan ocurrido, completar `Registro_Actividad_G05.md` con los números reales de revisión, fechas, autores y rutas de evidencia. Copiarlo a `guias/G05/` y realizar commit con mensaje `docs(G05): registrar rama, conflicto e integracion`. No escribir números o resultados hasta verlos en Show Log.

## F. Etiqueta v1.0.0

Solo después de validar trunk:

1. Desde la copia de trabajo de trunk, abrir `TortoiseSVN -> Branch/Tag...`.
2. To path: `/tags/v1.0.0`.
3. Copy from: HEAD revision in the repository.
4. Mensaje: `G05: etiquetar version final validada v1.0.0`.
5. Guardar captura de Show Log y Repo-browser donde se vea `tags/v1.0.0`.

La etiqueta identifica una revisión; no modifica el código fuente.

## G. Recuperar una versión anterior sin perder la actual

Usar `Repo-browser` sobre `trunk/LabPrestamo`, opción `Export...`, seleccionar la revisión que se quiere recuperar (por ejemplo r3) y exportar a una carpeta separada, como `LabPrestamo_Recuperacion_r3`. No hacer `Update to revision` sobre la copia de trabajo vigente para esta demostración. Guardar captura de la carpeta exportada y anotar la revisión seleccionada.

## H. Respaldo portable y restauración

Cuando estén cerradas las operaciones, crear una copia portable del repositorio. Si `svnadmin` está disponible, puede utilizarse `svnadmin hotcopy` sobre la carpeta raíz del repositorio (la que contiene `conf`, `db`, `hooks`, `locks`, no `trunk`). Destino sugerido: `SVN_LabPrestamo/Respaldo/Repositorio_hotcopy`. Verificar abriendo Repo-browser desde la ruta de respaldo o realizando una restauración de prueba en otra carpeta.

Si `svnadmin` no se reconoce, revisar el instalador de TortoiseSVN y añadir las herramientas de línea de comandos; no afirmar que el respaldo se generó hasta comprobarlo.

## Evidencias a conservar

- Capturas Show Log con r1-r6 y luego r7-r11.
- Captura del cambio 15 minutos en la copia de rama.
- Captura del commit concurrente de 10 minutos en trunk.
- Captura del cuadro de conflicto y de su estado resuelto.
- Captura del merge commit en trunk.
- Captura de `tags/v1.0.0`.
- Captura de exportación de una revisión anterior en carpeta separada.
- Captura o verificación del respaldo portable y de su apertura/restauración.

**Regla de honestidad del informe:** separar siempre lo realizado de lo pendiente. No incluir capturas simuladas ni indicar pruebas exitosas que no se hayan ejecutado.
