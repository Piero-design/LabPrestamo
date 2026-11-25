# G09 Gestión de calidad con Selenium

## Informe técnico

[Descargar informe en Word](Informe_G09_Selenium.docx) · [Consultar informe en PDF](Informe_G09_Selenium.pdf). Incluye procedimiento, ocho casos, resultados de dos ejecuciones, capturas y dictamen funcional.

## Objetivo

Verificar mediante Selenium WebDriver los resultados observables de los flujos de LabPréstamo y repetir la ejecución con datos aislados.

## Instalación y ejecución en macOS

Java 17 o posterior y Maven. El proyecto fija Selenium 4.37.0 y JUnit Jupiter 6.0.3 mediante Spring Boot 4.0.3. Maven Failsafe 3.5.4 ejecuta esta suite como pruebas de integración.

Desde la raíz del proyecto:

```sh
./selenium.sh
```

Selenium Manager descarga Chrome for Testing y ChromeDriver compatibles con el equipo en `~/.cache/selenium/`. La primera ejecución requiere Internet. Por defecto se utiliza Chrome estable sin ventana. Para observar la ejecución:

```sh
./selenium.sh -Dselenium.visible=true
```

Para reproducir una versión concreta de Chrome:

```sh
./selenium.sh -Dselenium.browserVersion=VERSION_REGISTRADA
```

En Windows o con Java ya configurado se puede utilizar `mvn -Pselenium verify`. `mvn package` compila la suite y `mvn -Pselenium verify` la ejecuta. Un fallo produce un código de salida distinto de cero.

## Suite y aislamiento

La fuente está en `src/test/java/pe/labprestamo/SeleniumIT.java`. La suite inicia la misma aplicación en un puerto libre con H2 en memoria, crea las cuentas demo y cierra el servidor al terminar. Antes de cada caso limpia las reservas de esa base temporal y abre un navegador nuevo. Se usan esperas explícitas y selectores basados en IDs y atributos de la app.

La preparación de reservas para los casos de disponibilidad y transiciones se realiza por JDBC. Las acciones verificadas de aprobación, entrega y devolución se realizan en la interfaz mediante Selenium. Los controles de permisos y cierre de sesión incluyen consultas HTTP desde el navegador autenticado. Las fechas se calculan respecto al momento de ejecución en America/Lima. Los campos de fecha reciben valores con JavaScript de Selenium para evitar diferencias de formato del control nativo de cada sistema.

## Casos y datos

- SEL-01, RF-01. Acceso correcto con `estudiante / Estudiante123!`. Debe aparecer Ana Torres con rol ESTUDIANTE.
- SEL-02, RF-01. Acceso con `estudiante / incorrecta`. Debe mostrar error y mantener el formulario de acceso.
- SEL-03, RF-02. Estudiante autenticado. La gestión del encargado está oculta y su ruta devuelve 403.
- SEL-04, RF-05 y RF-06. Reserva de EQ-001 desde mañana durante una hora, motivo Práctica Selenium G09. Debe aparecer PENDIENTE y existir un registro.
- SEL-05, RF-05. Inicio posterior al fin. Debe mostrar error y conservar cero reservas.
- SEL-06, RF-04. Reserva preparada para EQ-001 desde hace cinco minutos hasta dentro de una hora. Una consulta dentro de ese intervalo debe mostrar RESERVADO y deshabilitar su botón.
- SEL-07, RF-07, RF-08 y RF-09. Encargado autenticado y reserva pendiente dentro del horario actual. Debe pasar por APROBADA, PRESTADO y DEVUELTO con las condiciones indicadas.
- SEL-08, RF-01. Cerrar sesión y recargar. Debe aparecer el acceso y la ruta de usuario devolver 401.

Las cuentas las genera `DemoData.java`. Los equipos proceden del mismo inicializador. La preparación específica de cada caso reside en la suite y es reproducible.

## Reportes

- `target/failsafe-reports/` contiene el resultado por caso en XML y texto.
- `target/selenium/<caso>/resultado.png` guarda la pantalla al finalizar cada prueba aprobada.
- Si el cuerpo de una prueba falla se guarda `fallo.png` antes de cerrar el navegador.
- `entorno.txt` registra las capacidades y versiones del navegador y controlador.

Los directorios de `target/` son resultados locales y pueden regenerarse. Una evidencia conservada debe indicar la versión de código, fecha y comando utilizados.

## Dictamen

El resultado de esta suite permite emitir un dictamen funcional limitado a los ocho casos. El dictamen integral requiere además la evidencia de requisitos, riesgos, pruebas unitarias y rendimiento de las otras guías. Véase el resultado de la ejecución conservada antes de afirmar cumplimiento.

## Fuentes técnicas

- https://www.selenium.dev/documentation/selenium_manager/
- https://www.selenium.dev/documentation/webdriver/getting_started/

## Ejecución conservada del 24 de noviembre de 2026

Dos ejecuciones finalizaron con 8 aprobados, 0 fallos, 0 errores y 0 omitidos. [Consultar dictamen y límites](DICTAMEN.md). Los reportes se conservan en `evidencias/`. La segunda corrida incluye capturas y un manifiesto con hashes de las fuentes. Chrome for Testing y ChromeDriver utilizaron la versión 158.0.8127.88.

## Regresión de CH-03

La repetición final del 25/11/2026 aprobó los ocho casos. Se conserva también el timeout inicial y su seguimiento en [regresion-g07-g08](evidencias/regresion-g07-g08/README.md).
