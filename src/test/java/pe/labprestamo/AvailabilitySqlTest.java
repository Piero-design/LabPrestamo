package pe.labprestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.junit.jupiter.api.AfterEach;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
/** Comprobaciones complementarias de integración SQL, separadas de las 41 unitarias. */
class AvailabilitySqlTest {
 SingleConnectionDataSource ds;JdbcTemplate db;LabService service;LocalDateTime start=LocalDateTime.now().plusDays(2),end=start.plusHours(1);
 @BeforeEach void prepare() {
  ds=new SingleConnectionDataSource("jdbc:h2:mem:"+UUID.randomUUID(),"sa","",true);
  new ResourceDatabasePopulator(new ClassPathResource("schema.sql")).execute(ds);
  db=new JdbcTemplate(ds);service=new LabService(db);
  db.update("INSERT INTO app_user VALUES ('ana','test','Ana','ESTUDIANTE')");
  db.update("INSERT INTO equipment VALUES (1,'E1','Equipo','Medicion','OPERATIVO')");
 }
 @AfterEach void close() { ds.destroy(); }
 void reservation(LocalDateTime a,LocalDateTime b,String status) {db.update("INSERT INTO reservation(equipment_id,username,starts_at,ends_at,reason,status) VALUES (1,'ana',?,?,'Prueba',?)",a,b,status);}
 boolean available(){return (Boolean)service.equipment(start,end).get(0).get("AVAILABLE");}
 @Test void SQL01_freeEquipment() { assertTrue(available()); }
 @Test void SQL02_overlappingPending() { reservation(start,end,"PENDIENTE");assertFalse(available()); }
 @Test void SQL03_contiguousIsAvailable() { reservation(start.minusHours(1),start,"APROBADA");assertTrue(available()); }
 @Test void SQL04_overdueLoanBlocksFuture() { reservation(start.minusDays(4),start.minusDays(3),"PRESTADO");assertFalse(available()); }
 @Test void SQL05_returnedDoesNotBlock() { reservation(start,end,"DEVUELTO");assertTrue(available()); }
 @Test void SQL06_maintenanceUnavailable() {db.update("UPDATE equipment SET state='MANTENIMIENTO'");assertFalse(available());}
}
