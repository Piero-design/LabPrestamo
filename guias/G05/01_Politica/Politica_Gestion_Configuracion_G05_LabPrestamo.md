# Política de Gestión de Configuración – LabPréstamo

| Campo | Valor |
|---|---|
| Código | POL-G05-001 |
| Versión del documento | 1.0 |
| Fecha | 24/11/2026 |
| Proyecto | LabPréstamo |
| Guía | G05 – Gestión de Configuración |
| Estado | Versión inicial para revisión del equipo |

## 1. Objetivo

Establecer las reglas para identificar, organizar, versionar, modificar, integrar, etiquetar y recuperar los elementos de configuración del proyecto LabPréstamo. La política busca que los cambios sean trazables y que pueda reconstruirse la versión del producto utilizada en cada evidencia.

## 2. Alcance

Esta política aplica al código fuente, configuración, esquema de base de datos, scripts, documentación, requisitos, diagramas, mockups, pruebas y evidencias técnicas que formen parte de LabPréstamo.

El repositorio SVN se utiliza para desarrollar y demostrar las actividades obligatorias de la Guía 5. Git y GitHub se mantienen como plataforma de colaboración del proyecto y se comparan con SVN; no se consideran sustitutos de la evidencia SVN exigida en G05.

## 3. Herramientas y repositorio

| Elemento | Situación registrada |
|---|---|
| Cliente gráfico | TortoiseSVN instalado y utilizado desde el Explorador de Windows. La versión exacta debe registrarse en la evidencia de herramientas. |
| Repositorio | Repositorio local SVN creado mediante TortoiseSVN. |
| URL local utilizada | Repositorio local en la carpeta `SVN_LabPrestamo/Repositorio` del equipo de trabajo. |
| Estructura | `trunk/`, `branches/` y `tags/`. |
| Ruta del proyecto en SVN | `trunk/LabPrestamo/` |
| Copia de trabajo | `LabPrestamo_SVN_WC` |
| Revisión observada | Revisión 3: incorporación inicial del proyecto (58 archivos agregados). |

**Nota:** la ruta local anterior corresponde al equipo donde se creó el repositorio. Para trasladarlo a otro equipo se debe utilizar un respaldo portable del repositorio, no copiar manualmente solo la carpeta de trabajo.

## 4. Elementos de configuración

Se identifican como elementos de configuración (EC) los siguientes:

| Código | Elemento | Ubicación o ejemplo | Control requerido |
|---|---|---|---|
| EC-01 | Código fuente Java | `src/main/java/` | Revisiones SVN y pruebas |
| EC-02 | Recursos y configuración | `src/main/resources/` | Revisar cambios y no incluir secretos |
| EC-03 | Construcción y dependencias | `pom.xml` | Versionar cambios de dependencias |
| EC-04 | Scripts de ejecución | `ejecutar.sh` | Revisar y documentar cambios |
| EC-05 | Documentación técnica | `documentos/`, `README.md` | Mantener versión y fecha |
| EC-06 | Guías y evidencias | `guias/` | Conservar fuentes y evidencias verificables |
| EC-07 | Mockups e interfaz de referencia | `mockups/` | Versionar junto con cambios relacionados |
| EC-08 | Base de datos y scripts | `schema.sql` y scripts aplicables | Mantener cambios reproducibles |
| EC-09 | Pruebas | Ubicación de pruebas del proyecto | Vincularlas con requisitos y cambios |

### 4.1 Elementos excluidos

No se deben agregar al repositorio:

- La carpeta `.git/`, porque pertenece al historial interno de Git.
- La carpeta `.svn/` de la copia de trabajo, porque es metadato administrativo de SVN.
- Salidas de compilación como `target/`, archivos temporales, logs y cachés del IDE.
- Contraseñas, tokens, claves privadas, archivos `.env` locales o datos personales reales.
- Bases de datos locales generadas durante pruebas, salvo que el equipo las identifique expresamente como datos de prueba versionables.

El archivo `.gitignore` se conserva como parte del proyecto. No debe confundirse con la carpeta `.git`.

## 5. Estructura del repositorio

```text
Repositorio/
├── branches/
├── tags/
└── trunk/
    └── LabPrestamo/
        ├── documentos/
        ├── guias/
        ├── mockups/
        ├── src/
        ├── .gitignore
        ├── ejecutar.sh
        ├── pom.xml
        └── README.md
```

- **trunk:** línea principal del proyecto. Contiene la versión integrada y revisada.
- **branches:** ramas temporales para cambios que deban desarrollarse o probarse de manera aislada.
- **tags:** etiquetas inmutables para identificar entregas o versiones liberadas.

## 6. Convención de nombres

### 6.1 Ramas

Formato:

`feature/<id>-<descripcion-corta>`

Ejemplos:

- `feature/G05-politica-configuracion`
- `feature/G05-conflicto-demo`

Para correcciones:

`fix/<id>-<descripcion-corta>`

Los nombres deben ser breves, descriptivos y sin espacios.

### 6.2 Etiquetas

Se utilizará el formato:

`vMAJOR.MINOR.PATCH`

Criterios:

- **MAJOR:** cambio incompatible o versión principal del producto.
- **MINOR:** funcionalidad nueva compatible.
- **PATCH:** corrección compatible.

La primera versión final exigida para el ejercicio se identificará como `v1.0.0`, una vez que el equipo haya verificado el contenido que se etiquetará.

### 6.3 Mensajes de commit

Formato recomendado:

`<tipo>(<área>): <descripción en infinitivo>`

Tipos permitidos:

- `feat`: funcionalidad.
- `fix`: corrección.
- `docs`: documentación.
- `test`: pruebas.
- `build`: construcción o dependencias.
- `config`: configuración del repositorio.

Ejemplos:

- `docs(G05): agregar politica de gestion de configuracion`
- `feat(reservas): validar solapamiento de solicitudes`
- `test(reservas): agregar pruebas de disponibilidad`

Cada commit debe representar un cambio coherente y no mezclar tareas sin relación.

## 7. Roles y responsabilidades

El trabajo final establece un equipo de cuatro integrantes y solicita que cada alumno documente los roles de analista, desarrollador, responsable de configuración y evaluador de calidad. La asignación nominal se completará con los nombres reales del equipo.

| Rol en la actividad | Responsabilidad |
|---|---|
| Analista | Identificar el requisito o motivo del cambio y actualizar su documentación. |
| Desarrollador | Modificar el código y explicar el alcance del cambio. |
| Responsable de configuración | Verificar estado, estructura, commits, ramas, integración, etiquetas y respaldo SVN. |
| Evaluador de calidad | Ejecutar las pruebas afectadas y conservar los resultados. |

Antes de cerrar la entrega, el equipo debe registrar quién desempeñó cada rol en cada cambio significativo.

## 8. Procedimiento de trabajo

### 8.1 Inicio de una actividad

1. Verificar que se trabaja en la copia de trabajo correcta (`LabPrestamo_SVN_WC`).
2. Ejecutar **SVN Update** antes de empezar para obtener la última revisión.
3. Comprobar que no existan cambios locales ajenos a la tarea.
4. Identificar el requisito, defecto o documento que motiva el cambio.

### 8.2 Modificación y registro

1. Modificar únicamente los archivos relacionados con la tarea.
2. Revisar los cambios y retirar archivos temporales o sensibles.
3. Ejecutar la compilación o pruebas aplicables.
4. Usar **TortoiseSVN → Add** para incorporar archivos nuevos.
5. Revisar la lista de cambios en **SVN Commit**.
6. Escribir un mensaje descriptivo según la convención.
7. Confirmar el commit y registrar el número de revisión.

### 8.3 Uso de ramas e integración

1. Crear una rama bajo `branches/` cuando el cambio requiera aislamiento.
2. Desarrollar y probar el cambio en la rama.
3. Actualizar la copia de trabajo y revisar si existen cambios concurrentes.
4. Integrar la rama en `trunk` mediante la operación **Merge**.
5. Si aparece un conflicto, conservar el estado previo, revisar ambas versiones, acordar la resolución y comprobar el resultado.
6. Ejecutar las pruebas afectadas y registrar la integración.

No se debe marcar una integración como completada hasta que el conflicto esté resuelto y las pruebas pertinentes hayan sido ejecutadas.

### 8.4 Etiquetado y recuperación

1. Antes de etiquetar, verificar que `trunk` contiene la versión que se pretende entregar.
2. Crear una etiqueta desde la revisión validada en `tags/v1.0.0`.
3. Registrar la revisión de origen, fecha, responsable y evidencias.
4. Para recuperar una versión anterior, consultar el historial y realizar una actualización o exportación a una revisión concreta en una ubicación separada.
5. No sobrescribir la versión vigente para hacer una demostración de recuperación.

## 9. Respaldo y recuperación del repositorio

El repositorio debe conservarse de manera portable para permitir su traslado y recuperación. El respaldo debe realizarse con una herramienta compatible con SVN (por ejemplo, `svnadmin hotcopy` o `svnadmin dump`, cuando las herramientas de administración estén disponibles).

El respaldo debe incluir fecha, ruta de origen, revisión más reciente y ubicación de destino. Después de generarlo, se debe comprobar que el archivo o copia de respaldo puede abrirse o restaurarse en una ubicación de prueba.

**Estado:** respaldo portable y prueba de restauración pendientes de ejecución y evidencia.

## 10. Historial observado y estado de G05

| Revisión SVN | Descripción observada | Estado |
|---|---|---|
| r1 | Creación del repositorio y estructura inicial `trunk/branches/tags`. | Registrada |
| r2 | Creación de `trunk/LabPrestamo`. | Registrada |
| r3 | Incorporación inicial de 58 archivos del proyecto. | Registrada |
| r4 | Incorporación de esta política de configuración. | Pendiente de commit |
| r5–r8 | Cambios significativos adicionales del proyecto, con evidencia y mensajes descriptivos. | Pendientes |

Las revisiones futuras deben corresponder a cambios reales del proyecto o sus artefactos. No se crearán commits vacíos ni cambios artificiales únicamente para alcanzar el mínimo solicitado.

## 11. Evidencias obligatorias pendientes

| Evidencia | Estado |
|---|---|
| Repositorio y estructura `trunk/branches/tags` | Realizada |
| Checkout de `trunk/LabPrestamo` | Realizada |
| Commit inicial del proyecto (58 elementos) | Realizada, r3 |
| Política de configuración versionada | Pendiente, siguiente commit |
| Ocho revisiones significativas | Parcial: r1–r3 registradas |
| Creación y uso de una rama | Pendiente |
| Integración de rama y resolución de conflicto | Pendiente |
| Etiqueta `v1.0.0` | Pendiente |
| Recuperación de una revisión anterior | Pendiente |
| Respaldo portable y comprobación de restauración | Pendiente |
| Comparación SVN con Git y GitHub | Pendiente de documentar |

## 12. Referencias

1. Trabajo Final Integrador de Calidad de Software, sección 8, “Gestión de configuración”, y apartado “Paquete de entrega”.
2. Guía de Laboratorio 5, “Gestión de Configuración”, actividades y cuestionario.
3. Documentación de Apache Subversion: `https://subversion.apache.org/`
4. Documentación de TortoiseSVN: `https://tortoisesvn.net/docs/release/TortoiseSVN_en/`

## 13. Control de cambios de esta política

| Versión | Fecha | Descripción | Estado |
|---|---|---|---|
| 1.0 | 24/11/2026 | Emisión inicial de la política para revisión del equipo. | Propuesta para aprobación |
