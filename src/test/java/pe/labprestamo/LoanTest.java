package pe.labprestamo;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
class LoanTest extends BusinessTestSupport {
 void act(String action,boolean admin,String note,boolean damaged) { service.transition(7,action,"ana",admin,note,damaged); }
 void pending() { transition("PENDIENTE",future,future.plusHours(1),"OPERATIVO"); }
 @Test void PU23_cancelOwnReservation() { pending();act("cancel",false,"",false);assertTrue(db.writes.get(0).sql().contains("CANCELADA"));assertEquals(7L,db.writes.get(0).args().get(0)); }
 @Test void PU24_cancelOtherForbidden() { pending();error(403,()->service.transition(7,"cancel","otro",false,"",false)); }
 @Test void PU25_cancelStartedRejected() { transition("APROBADA",future.minusDays(3),future,"OPERATIVO");error(409,()->act("cancel",false,"",false)); }
 @Test void PU26_adminRequired() { pending();error(403,()->act("approve",false,"",false)); }
 @Test void PU27_approvePending() { pending();db.counts.add(0);act("approve",true,"Conforme",false);assertEquals("APROBADA",db.writes.get(0).args().get(0));assertEquals("ana",db.writes.get(0).args().get(1)); }
 @Test void PU28_rejectWithReason() { pending();act("reject",true," Sin disponibilidad ",false);assertEquals("RECHAZADA",db.writes.get(0).args().get(0));assertEquals("Sin disponibilidad",db.writes.get(0).args().get(2)); }
 @Test void PU29_rejectWithoutReason() { pending();error(400,()->act("reject",true," ",false)); }
 @Test void PU30_decideAlreadyProcessed() { transition("APROBADA",future,future.plusHours(1),"OPERATIVO");error(409,()->act("approve",true,"",false)); }
 @Test void PU31_deliverApproved() { transition("APROBADA",LocalDateTime.now().minusMinutes(5),future,"OPERATIVO");db.counts.add(0);act("deliver",true,"Completo",false);assertTrue(db.writes.get(0).sql().contains("PRESTADO"));assertEquals("Completo",db.writes.get(0).args().get(2)); }
 @Test void PU32_deliverPendingRejected() { pending();error(409,()->act("deliver",true,"Completo",false)); }
 @Test void PU33_deliverTooEarlyRejected() { transition("APROBADA",future,future.plusHours(1),"OPERATIVO");error(409,()->act("deliver",true,"Completo",false)); }
 @Test void PU34_returnDamaged() { transition("PRESTADO",future.minusDays(3),future,"OPERATIVO");act("return",true,"Pantalla rota",true);assertEquals(2,db.writes.size());assertTrue(db.writes.get(0).sql().contains("DEVUELTO"));assertEquals("Pantalla rota",db.writes.get(0).args().get(2));assertTrue(db.writes.get(1).sql().contains("MANTENIMIENTO")); }
 @Test void PU35_returnUndamaged() { transition("PRESTADO",future.minusDays(3),future,"OPERATIVO");act("return",true,"Sin daños",false);assertEquals(1,db.writes.size());assertTrue(db.writes.get(0).sql().contains("DEVUELTO")); }
 @Test void PU36_returnPendingRejected() { pending();error(409,()->act("return",true,"Bien",false)); }
 @Test void PU37_unknownAction() { pending();error(400,()->act("inventada",true,"",false)); }
 @Test void PU38_approveExpiredRejected() { transition("PENDIENTE",future.minusDays(4),future.minusDays(3),"OPERATIVO");error(409,()->act("approve",true,"",false)); }
 @Test void PU39_approveMaintenanceRejected() { transition("PENDIENTE",future,future.plusHours(1),"MANTENIMIENTO");error(409,()->act("approve",true,"",false)); }
 @Test void PU40_deliverMaintenanceRejected() { transition("APROBADA",LocalDateTime.now().minusMinutes(5),future,"MANTENIMIENTO");error(409,()->act("deliver",true,"Completo",false)); }
 @Test void PU41_deliverUnreturnedRejected() { transition("APROBADA",LocalDateTime.now().minusMinutes(5),future,"OPERATIVO");db.counts.add(1);error(409,()->act("deliver",true,"Completo",false)); }
}
