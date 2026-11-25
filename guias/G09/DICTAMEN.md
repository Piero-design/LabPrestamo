# Dictamen de la verificación funcional G09

Fecha de evaluación 24 de noviembre de 2026. Aplicación LabPréstamo 0.2.0. Código identificado mediante hashes SHA-256 en el manifiesto de la segunda ejecución.

## Resultado observado

Se ejecutaron ocho casos Selenium en dos corridas independientes. Ambas finalizaron con ocho aprobados, cero fallos, cero errores y cero omitidos. Los reportes de Maven Failsafe se conservan en `evidencias/ejecucion-1/` y `evidencias/ejecucion-2/`. La segunda ejecución incluye un XML de resultados sin propiedades locales ni logs de arranque, el manifiesto de fuentes y las ocho capturas de resultado.

Los casos verifican acceso correcto, credenciales inválidas, restricción por rol, registro válido, intervalo inválido, consulta de disponibilidad, aprobación con entrega y devolución, y cierre de sesión. La suite usa datos temporales y sesiones nuevas por caso.

## Decisión

Apto con observaciones para demostrar los ocho recorridos funcionales cubiertos por G09 en el entorno probado. El dictamen de liberación integral sigue pendiente de las evidencias de requisitos y riesgos, la suite unitaria de negocio y las pruebas de rendimiento G07 y G08.

La ejecución usa H2 en memoria y Chrome sin ventana sobre macOS ARM64. La persistencia tras reinicio, otros navegadores, carga concurrente y usabilidad con participantes requieren sus verificaciones específicas. Selenium emitió avisos de versión CDP al utilizar Chrome 158. Los ocho casos usan WebDriver estándar y finalizaron correctamente. No se probaron funciones avanzadas de DevTools.

No se registraron defectos funcionales en estos ocho casos. Las capturas conservadas corresponden a resultados aprobados. La suite está configurada para guardar `fallo.png` si falla el cuerpo de una prueba.

## Repetición

Ejecutar `./selenium.sh`. Para repetir la versión observada del navegador, ejecutar `./selenium.sh -Dselenium.browserVersion=158.0.8127.88`. Para ver el navegador, agregar `-Dselenium.visible=true`.
