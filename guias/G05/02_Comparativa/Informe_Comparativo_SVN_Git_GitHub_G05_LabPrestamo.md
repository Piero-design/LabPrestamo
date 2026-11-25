# Informe comparativo: Apache Subversion (SVN), Git y GitHub

**Proyecto:** LabPréstamo  
**Asignatura:** Calidad de Software  
**Guía:** G05 – Gestión de Configuración  
**Código:** INF-G05-002  
**Versión:** 1.0  
**Fecha:** 24/11/2026  
**Estado:** Informe inicial para revisión del equipo

## I. Objetivo

Comparar Apache Subversion (SVN), Git y GitHub considerando su arquitectura, forma de trabajo, historial, ramas, integración de cambios, colaboración y recuperación de versiones. La comparación se realiza para justificar el uso de SVN en las actividades de la Guía G05 y distinguirlo de Git y de la plataforma GitHub utilizada por el equipo.

## II. Descripción del procedimiento

1. Se identificó el papel de cada tecnología: SVN y Git son sistemas de control de versiones; GitHub es una plataforma de alojamiento de repositorios Git y colaboración.
2. Se revisaron los conceptos de repositorio central, copia de trabajo, clon local, commit, push/pull, ramas, etiquetas, historial y solicitudes de integración.
3. Se tomó como caso práctico la configuración SVN realizada para LabPréstamo mediante TortoiseSVN, sin modificar el repositorio GitHub original.
4. Se contrastaron los procedimientos observados con los objetivos de la Guía 5 y los requisitos del Trabajo Final.

## III. Resultados obtenidos

### 3.1 Diferencia fundamental

- **Apache Subversion (SVN):** sistema de control de versiones centralizado. Existe un repositorio central y los usuarios obtienen una copia de trabajo mediante Checkout; los cambios se envían al repositorio con Commit.
- **Git:** sistema de control de versiones distribuido. Cada clon de trabajo mantiene el historial del repositorio y permite crear commits locales; los cambios se comparten con otros repositorios mediante operaciones como Push y Pull.
- **GitHub:** servicio de alojamiento y colaboración para repositorios Git. Añade funciones de trabajo en equipo, como Pull Requests, revisión de cambios, incidencias, permisos y automatización. GitHub no es, por sí mismo, el sistema de control de versiones: utiliza Git.

### 3.2 Cuadro comparativo

| Criterio | Apache Subversion (SVN) | Git | GitHub |
|---|---|---|---|
| Tipo | Sistema de control de versiones centralizado | Sistema de control de versiones distribuido | Plataforma de alojamiento y colaboración basada en Git |
| Repositorio | Un repositorio central contiene el historial compartido | Cada clon contiene el historial del repositorio | Aloja repositorios Git remotos y sus herramientas de colaboración |
| Trabajo local | Se trabaja sobre una copia de trabajo obtenida con Checkout | Se trabaja sobre un repositorio clonado con historial local | Se utiliza normalmente junto con un clon local de Git |
| Registro de cambios | Commit envía los cambios al repositorio SVN | Commit registra cambios localmente; Push los publica en un remoto | Muestra los commits recibidos en el repositorio alojado |
| Identificación del historial | Revisiones globales y secuenciales, por ejemplo r3 o r4 | Commits identificados mediante hashes y conectados en un grafo | Presenta el historial de commits Git del repositorio alojado |
| Ramas | Se organizan como rutas dentro del repositorio, por ejemplo `branches/` | Son referencias ligeras que permiten desarrollar líneas paralelas | Permite colaborar sobre ramas Git y proponer su integración con Pull Requests |
| Etiquetas | Habitualmente se crean rutas bajo `tags/` para señalar una versión | Usa tags para identificar commits concretos | Muestra y aloja las etiquetas Git publicadas en el remoto |
| Integración | Se utiliza Merge para incorporar cambios entre rutas | Se integra mediante Merge o Rebase, según el flujo elegido | Pull Request facilita revisión, conversación y aprobación antes de integrar |
| Conflictos | Pueden aparecer al actualizar o fusionar cambios concurrentes; se resuelven en la copia de trabajo | Pueden aparecer al fusionar o aplicar cambios; se resuelven antes de completar la integración | La interfaz ayuda a revisar conflictos de una solicitud, pero la resolución sigue correspondiendo al contenido Git |
| Trabajo sin conexión | Se pueden editar archivos localmente; para confirmar al repositorio central se requiere acceso a este | Se pueden crear commits y consultar el historial local sin conexión; se necesita conexión para compartirlos con un remoto | La colaboración web y las operaciones sobre el remoto requieren conexión |
| Colaboración | Comparte un repositorio central; los permisos se administran en el servidor o repositorio | Comparte cambios mediante repositorios remotos y ramas | Añade Pull Requests, revisiones, Issues, permisos y otras funciones del equipo |
| Recuperación | Permite consultar revisiones anteriores, actualizar a una revisión o exportar una versión anterior | Permite inspeccionar commits, restaurar archivos o crear ramas desde commits previos | Facilita navegar por el historial alojado y revisar commits y releases |
| Interfaz utilizada en este trabajo | TortoiseSVN desde el Explorador de Windows | Git y GitHub Desktop para el repositorio del proyecto | Sitio web de GitHub y GitHub Desktop para colaboración |
| Uso en G05 | Herramienta obligatoria para demostrar repositorio, revisiones, ramas, conflicto, etiqueta y recuperación | Tecnología de comparación y flujo existente del equipo | Plataforma que se compara con SVN; no reemplaza la práctica obligatoria de SVN |

### 3.3 Ventajas y limitaciones en el contexto de LabPréstamo

**SVN**

- Ventajas: historial centralizado fácil de consultar; revisiones secuenciales; TortoiseSVN permite ejecutar operaciones desde el Explorador de Windows; la estructura `trunk/branches/tags` es clara para la práctica.
- Limitaciones: las operaciones de commit dependen de acceder al repositorio central; las ramas y fusiones requieren una gestión cuidadosa para evitar conflictos.

**Git**

- Ventajas: cada clon mantiene el historial; permite realizar commits locales sin conexión al servidor; las ramas son ligeras y adecuadas para desarrollar cambios de forma independiente.
- Limitaciones: requiere comprender el flujo entre repositorio local y remoto y resolver conflictos cuando se integran líneas de trabajo diferentes.

**GitHub**

- Ventajas: facilita la colaboración del equipo mediante Pull Requests, revisiones, incidencias, permisos y visualización del historial.
- Limitaciones: depende de la plataforma y del acceso a Internet para las funciones alojadas; no sustituye los comandos ni el repositorio local de Git.

### 3.4 Evidencia práctica observada en LabPréstamo

| Elemento | Resultado observado | Evidencia disponible |
|---|---|---|
| Instalación de TortoiseSVN | La integración con el Explorador y la ventana Settings abrieron correctamente | Capturas del menú contextual y Settings |
| Repositorio SVN | Repositorio local creado | Captura de confirmación “Repository created” |
| Estructura | Se observan `trunk/`, `branches/` y `tags/` en Repo-browser | Captura del Repo-browser |
| Checkout | Se obtuvo la copia de trabajo `LabPrestamo_SVN_WC` desde `trunk/LabPrestamo` | Captura “Checkout Finished”, revisión 2 |
| Incorporación del proyecto | Se agregaron los elementos del proyecto sin copiar `.git` ni reemplazar `.svn` | Captura “Add Finished” |
| Commit inicial del proyecto | Revisión 3; mensaje “G05: incorporar codigo fuente y documentacion inicial de LabPrestamo” | Captura “Commit Finished” y Show Log |
| Política de configuración | Revisión 4; se agregaron los documentos Word y Markdown en `guias/G05/` | Captura de Show Log con revisión 4 |

**Estado de la práctica al emitir este informe:** se han comprobado las revisiones r1–r4 y la estructura básica SVN. La creación y uso de una rama, la integración con resolución de conflicto, la etiqueta `v1.0.0`, la recuperación de una revisión anterior y el respaldo portable aún deben ejecutarse y documentarse. No se consideran actividades completadas hasta contar con sus evidencias.

## IV. Análisis de resultados

SVN y Git cumplen la función de controlar versiones, pero utilizan modelos de trabajo diferentes. SVN mantiene un repositorio central con revisiones secuenciales; Git distribuye el historial entre los clones y permite registrar commits localmente. GitHub complementa Git como plataforma de alojamiento y colaboración, por lo que comparar SVN directamente con GitHub sin distinguir Git confundiría un sistema de control de versiones con un servicio de colaboración.

Para LabPréstamo se mantienen ambos flujos con propósitos distintos: Git/GitHub continúa siendo el flujo de colaboración del proyecto, mientras que SVN se utiliza para cumplir y evidenciar la práctica G05. La revisión 3 contiene la incorporación inicial del proyecto y la revisión 4 registra la política de configuración. Estas revisiones prueban el inicio del historial, pero todavía no satisfacen por sí solas el requisito de ocho revisiones significativas ni las demostraciones de rama, conflicto, etiqueta, recuperación y respaldo.

## V. Conclusiones

1. SVN y Git son sistemas de control de versiones; SVN es centralizado y Git distribuido.
2. GitHub es una plataforma para alojar repositorios Git y facilitar la colaboración; no debe confundirse con Git.
3. TortoiseSVN proporciona una interfaz gráfica integrada con el Explorador de Windows para ejecutar operaciones sobre SVN.
4. En LabPréstamo se creó un repositorio SVN con `trunk/`, `branches/` y `tags/`, se realizó Checkout y se registraron las revisiones observadas r3 y r4 para el código inicial y la política de configuración.
5. SVN se utiliza para demostrar las actividades obligatorias de G05, mientras Git/GitHub se conserva como flujo de colaboración del proyecto.
6. La práctica G05 permanece parcialmente ejecutada hasta completar ocho revisiones significativas, rama, integración con conflicto, etiqueta `v1.0.0`, recuperación y respaldo portable.

## Referencias

1. Universidad Nacional de San Agustín. *Guía de Laboratorio 5: Gestión de Configuración*, sección de actividades y ejercicio propuesto de comparación con GitHub.
2. *Trabajo Final Integrador de Calidad de Software*, sección 8 “Configuración y gestión de riesgos”, requisitos de la Guía 5 y entregables G05.
3. Apache Subversion: https://subversion.apache.org/
4. Git: https://git-scm.com/about
5. GitHub Docs: https://docs.github.com/

## Anexo. Respuestas breves al cuestionario de PRAC05

**a) ¿Hasta qué nivel de recuperación de versiones pasadas se puede realizar?**  
Mientras la revisión esté conservada en el historial del repositorio, se puede consultar una revisión anterior y recuperar una copia de los archivos correspondientes. La recuperación debe hacerse de forma controlada, sin sobrescribir accidentalmente la versión vigente. La disponibilidad real depende de que el repositorio y sus respaldos se conserven.

**b) ¿Cuál es la estructura del nombre o etiqueta propuesta para cada versión?**  
Se propone `vMAJOR.MINOR.PATCH`: MAJOR para cambios incompatibles, MINOR para funcionalidad compatible y PATCH para correcciones compatibles. La etiqueta final exigida para este trabajo será `v1.0.0`, una vez validada la versión de entrega.

**c) Grafique el diagrama de actualización de versión luego de un cambio.**

```text
Versión vigente en trunk
          |
          v
Identificar y registrar el cambio
          |
          v
Actualizar copia de trabajo
          |
          v
Modificar archivos y ejecutar pruebas
          |
          v
Revisar cambios y resolver conflictos
          |
          v
Commit de la modificación
          |
          v
Nueva revisión SVN
          |
          v
Validar versión integrada
          |
          v
Crear tag cuando corresponda
```
