package pe.labprestamo;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class EquipmentTest extends BusinessTestSupport {
 @Test void PU01_createTrimsFields() { service.saveEquipment(null," E1 "," Equipo "," Medicion ","OPERATIVO");assertEquals(List.of("E1","Equipo","Medicion","OPERATIVO"),db.writes.get(0).args());assertTrue(db.writes.get(0).sql().startsWith("INSERT")); }
 @Test void PU02_rejectEmptyCode() { error(400,()->service.saveEquipment(null," ","Equipo","Medicion","OPERATIVO")); }
 @Test void PU03_acceptMaximumName() { service.saveEquipment(null,"E1","a".repeat(120),"Medicion","OPERATIVO");assertEquals(120,db.writes.get(0).args().get(1).toString().length()); }
 @Test void PU04_rejectLongName() { error(400,()->service.saveEquipment(null,"E1","a".repeat(121),"Medicion","OPERATIVO")); }
 @Test void PU05_rejectInvalidState() { error(400,()->service.saveEquipment(null,"E1","Equipo","Medicion","ROTO")); }
 @Test void PU06_rejectMaintenanceWithLoans() { equipment("OPERATIVO");db.counts.add(1);error(409,()->service.saveEquipment(1L,"E1","Equipo","Medicion","MANTENIMIENTO")); }
 @Test void PU07_allowMaintenanceWithoutLoans() { equipment("OPERATIVO");db.counts.add(0);service.saveEquipment(1L,"E1","Equipo","Medicion","MANTENIMIENTO");assertEquals("MANTENIMIENTO",db.writes.get(0).args().get(3));assertEquals(1L,db.writes.get(0).args().get(4)); }
 @Test void PU08_unknownEquipment() { db.rows.add(List.of());error(404,()->service.saveEquipment(99L,"E1","Equipo","Medicion","OPERATIVO")); }
 @Test void PU09_catalogWithoutInterval() { var rows=List.<Map<String,Object>>of(Map.of("CODE","E1"));db.rows.add(rows);assertEquals(rows,service.equipment(null,null)); }
 @Test void PU10_catalogWithInterval() { var rows=List.<Map<String,Object>>of(Map.of("AVAILABLE",false));db.rows.add(rows);assertEquals(rows,service.equipment(future,future.plusHours(1))); }
}
