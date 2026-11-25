# G07 - Pruebas unitarias

[Informe Word](Informe_G07.docx) · [Informe PDF](Informe_G07.pdf) · [Catálogo de casos](catalogo.md)

## Entrega sencilla

41 pruebas unitarias de tres componentes funcionales reales de `LabService`: equipos, reservas y transiciones de préstamos. Se añaden seis comprobaciones de integración SQL. Resultado final: 47 aprobadas, cero fallos/errores/omisiones.

JUnit Jupiter 6.0.3, Maven Surefire 3.5.4 y JaCoCo 0.8.14. Java del proyecto: 17 o superior; ejecución registrada con Java 25.0.2. No se mezcla JUnit 4 con Jupiter.

```sh
# En macOS, si JAVA_HOME apunta a una instalación antigua:
export JAVA_HOME=$(/usr/libexec/java_home)
mvn clean test
```

Los resultados nuevos quedan en `target/surefire-reports/` y `target/site/jacoco/index.html`. Se conservan reportes de esta entrega en `evidencias/`.

## Cobertura y aislamiento

JaCoCo incluye `pe/labprestamo/LabService*`, toda la clase que concentra los componentes evaluados. Resultado: 71 de 72 líneas, 98,61 %. El build exige al menos 80 %. Se excluyen arranque, datos demo, seguridad HTTP y controlador REST; tampoco se mide el frontend. El porcentaje no es cobertura de todo el sistema. El filtrado de reportes en ApiController queda fuera de esta medición.

`BusinessTestSupport.FakeDb` sustituye JDBC por datos preparados y registra escrituras. Cada caso crea una instancia nueva; no depende de otros casos. Las seis pruebas SQL usan H2 independiente por caso y comprueban las condiciones reales de disponibilidad, que el doble no puede verificar.

## Defecto controlado reproducible

```sh
python3 guias/G07/defecto_controlado.py
```

Ejecutar sin otro Maven/editor modificando `LabService.java` simultáneamente. El script acepta temporalmente un intervalo de longitud cero, ejecuta PU-12, exige que JUnit reporte el fallo y restaura el archivo aunque haya error. Finalmente ejecuta `mvn clean test`. La evidencia de la entrega muestra 1 fallo durante la mutación y 41 aprobados al restaurarla; después se incorporaron seis comprobaciones SQL adicionales.

## Regresión tras cambio G02

[CH-03](../G02/CH-03.md) optimiza disponibilidad y añade un índice sin cambiar su regla. `antes-cambio.txt` y `despues-cambio.txt` conservan 47 aprobados antes y después. Los hashes de las fuentes finales están en `evidencias/manifiesto.json`. No se registró un defecto funcional permanente; el error DC-01 se introdujo únicamente para demostrar la detección.

Fuentes: guía del trabajo final, sección 9; [JaCoCo 0.8.14](https://www.jacoco.org/jacoco/trunk/doc/changes.html).
