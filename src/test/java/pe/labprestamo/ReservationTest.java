package pe.labprestamo;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ReservationTest extends BusinessTestSupport {
 @Test void PU11_validReservation() { equipment("OPERATIVO");db.counts.add(0);service.reserve("ana",1,future,future.plusHours(1)," Practica ");assertEquals(List.of(1L,"ana",future,future.plusHours(1),"Practica"),db.writes.get(0).args());assertTrue(db.writes.get(0).sql().contains("'PENDIENTE'")); }
 @Test void PU12_equalIntervalRejected() { error(400,()->LabService.interval(future,future)); }
 @Test void PU13_reverseIntervalRejected() { error(400,()->service.reserve("ana",1,future,future.minusHours(1),"Practica")); }
 @Test void PU14_missingDateRejected() { error(400,()->service.reserve("ana",1,null,future,"Practica")); }
 @Test void PU15_pastRejected() { error(400,()->service.reserve("ana",1,future.minusDays(4),future,"Practica")); }
 @Test void PU16_maintenanceRejected() { equipment("MANTENIMIENTO");error(409,()->service.reserve("ana",1,future,future.plusHours(1),"Practica")); }
 @Test void PU17_overlapRejected() { equipment("OPERATIVO");db.counts.add(1);error(409,()->service.reserve("ana",1,future,future.plusHours(1),"Practica")); }
 @Test void PU18_reasonLimitAccepted() { equipment("OPERATIVO");db.counts.add(0);service.reserve("ana",1,future,future.plusMinutes(1),"a".repeat(500));assertEquals(500,db.writes.get(0).args().get(4).toString().length()); }
 @Test void PU19_reasonOverLimitRejected() { error(400,()->service.reserve("ana",1,future,future.plusHours(1),"a".repeat(501))); }
 @Test void PU20_nullReasonRejected() { error(400,()->service.reserve("ana",1,future,future.plusHours(1),null)); }
 @Test void PU21_ownReservations() { var rows=List.<Map<String,Object>>of(Map.of("USERNAME","ana"));db.rows.add(rows);assertEquals(rows,service.reservations("ana")); }
 @Test void PU22_allReservations() { db.rows.add(List.of());assertTrue(service.reservations(null).isEmpty()); }
}
