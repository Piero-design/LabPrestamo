# Solicitud de cambio CC-01 – Margen de preparación entre reservas

**Proyecto:** LabPréstamo  
**Fecha de preparación:** 24/11/2026  
**Versión de aplicación base:** 0.2.0 (verificar en `pom.xml` del checkout utilizado)  
**Estado:** Propuesta para ejercicio G05; no afirmar como requisito aprobado hasta validación del equipo/interesado.

## Motivo
Evitar reservas consecutivas del mismo equipo sin tiempo operativo de revisión y preparación.

## Cambio propuesto
Exigir 15 minutos de separación entre reservas del mismo equipo. Debe aplicarse coherentemente a consulta de disponibilidad y a las operaciones que crean o aprueban solicitudes.

## Impacto
- RF-04 y RF-05: explicitar la regla en disponibilidad y solicitud.
- Diseño: centralizar la validación en el servicio de reservas.
- Código: ampliar el intervalo verificado por el servicio.
- Datos: no requiere cambios de esquema.
- Pruebas: verificar rechazos a 14 minutos y aceptación desde 15 minutos, sujeto a las demás reglas.
- Riesgos: validaciones inconsistentes podrían mostrar disponibilidad incorrecta.

## Criterios de aceptación propuestos
1. Si una reserva termina a las 11:00, una reserva del mismo equipo que comienza a las 11:14 se rechaza.
2. Una reserva que comienza a las 11:15 puede aceptarse si no infringe otras reglas.
3. Las mismas reglas se aplican a consulta, creación y aprobación.

## Trazabilidad del ejercicio
- Rama prevista: `branches/feature/G05-tiempo-preparacion` (propuesta 15 minutos).
- Cambio concurrente temporal en `trunk`: propuesta de 10 minutos para generar un conflicto de demostración.
- Resolución propuesta: conservar los 15 minutos.

**Limitación:** la diferencia de 10 minutos se utiliza únicamente para inducir el conflicto controlado requerido por la práctica. El margen de 15 minutos necesita validación del equipo/interesado antes de integrarse como regla definitiva del producto.
