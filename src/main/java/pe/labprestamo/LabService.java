package pe.labprestamo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LabService {
    private final JdbcTemplate db;
    public LabService(JdbcTemplate db) { this.db = db; }

    static String required(String value, int max) {
        if (value == null || value.isBlank() || value.trim().length() > max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completa los campos y respeta su longitud máxima.");
        }
        return value.trim();
    }
    static void interval(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El fin debe ser posterior al inicio.");
        }
    }
    private Map<String, Object> one(String sql, Object... args) {
        var rows = db.queryForList(sql, args);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado.");
        return rows.get(0);
    }
    private void conflict(String message) { throw new ResponseStatusException(HttpStatus.CONFLICT, message); }

    public List<Map<String, Object>> equipment(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return db.queryForList("SELECT * FROM equipment ORDER BY code");
        interval(start, end);
        return db.queryForList("""
            SELECT e.*, (e.state='OPERATIVO' AND NOT EXISTS (
              SELECT 1 FROM reservation r WHERE r.equipment_id=e.id AND
              ((r.status IN ('PENDIENTE','APROBADA','PRESTADO') AND r.starts_at < ? AND r.ends_at > ?)
              OR (r.status='PRESTADO' AND r.ends_at < CURRENT_TIMESTAMP))
            )) AS available FROM equipment e ORDER BY e.code
            """, end, start);
    }

    @Transactional
    public void saveEquipment(Long id, String code, String name, String category, String state) {
        code = required(code,40); name = required(name,120); category = required(category,80);
        if (!List.of("OPERATIVO","MANTENIMIENTO").contains(state == null ? "" : state))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Estado inválido.");
        if (id == null) {
            db.update("INSERT INTO equipment(code,name,category,state) VALUES (?,?,?,?)",code,name,category,state);
        } else {
            one("SELECT * FROM equipment WHERE id=? FOR UPDATE",id);
            if (state.equals("MANTENIMIENTO") && db.queryForObject("SELECT COUNT(*) FROM reservation WHERE equipment_id=? AND (status='PRESTADO' OR (status IN ('PENDIENTE','APROBADA') AND ends_at>CURRENT_TIMESTAMP))",Integer.class,id)>0)
                conflict("Cancela o rechaza las reservas vigentes y recibe los préstamos antes de pasar a mantenimiento.");
            db.update("UPDATE equipment SET code=?,name=?,category=?,state=? WHERE id=?",code,name,category,state,id);
        }
    }

    @Transactional
    public void reserve(String username, long equipmentId, LocalDateTime start, LocalDateTime end, String reason) {
        interval(start,end); reason=required(reason,500);
        if (!start.isAfter(LocalDateTime.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El inicio debe estar en el futuro.");
        // Every reservation transition takes the equipment lock first to serialize conflicting requests.
        var equipment=one("SELECT * FROM equipment WHERE id=? FOR UPDATE",equipmentId);
        if (!equipment.get("STATE").equals("OPERATIVO")) conflict("El equipo está en mantenimiento.");
        checkAvailability(equipmentId,start,end,-1);
        db.update("INSERT INTO reservation(equipment_id,username,starts_at,ends_at,reason,status) VALUES (?,?,?,?,?,'PENDIENTE')",equipmentId,username,start,end,reason);
    }
    private void checkAvailability(long equipmentId, LocalDateTime start, LocalDateTime end, long excludeId) {
        int count=db.queryForObject("""
            SELECT COUNT(*) FROM reservation WHERE equipment_id=? AND id<>? AND
            ((status IN ('PENDIENTE','APROBADA','PRESTADO') AND starts_at<? AND ends_at>?)
            OR (status='PRESTADO' AND ends_at<CURRENT_TIMESTAMP))
            """,Integer.class,equipmentId,excludeId,end,start);
        if (count>0) conflict("El equipo ya está reservado en ese horario o tiene una devolución pendiente.");
    }

    public List<Map<String,Object>> reservations(String username) {
        String sql="SELECT r.*,e.name AS equipment_name,e.code,u.name AS student_name FROM reservation r JOIN equipment e ON e.id=r.equipment_id JOIN app_user u ON u.username=r.username";
        if (username==null) return db.queryForList(sql+" ORDER BY r.id DESC");
        return db.queryForList(sql+" WHERE r.username=? ORDER BY r.id DESC",username);
    }

    @Transactional
    public void transition(long id, String action, String username, boolean admin, String note, boolean damaged) {
        var original=one("SELECT equipment_id FROM reservation WHERE id=?",id);
        long equipmentId=((Number)original.get("EQUIPMENT_ID")).longValue();
        var equipment=one("SELECT * FROM equipment WHERE id=? FOR UPDATE",equipmentId);
        var row=one("SELECT * FROM reservation WHERE id=? FOR UPDATE",id);
        String status=(String)row.get("STATUS");
        LocalDateTime start=((Timestamp)row.get("STARTS_AT")).toLocalDateTime();
        LocalDateTime end=((Timestamp)row.get("ENDS_AT")).toLocalDateTime();
        LocalDateTime now=LocalDateTime.now();
        if (action.equals("cancel")) {
            if (!username.equals(row.get("USERNAME"))) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo puedes cancelar tus reservas.");
            if (!List.of("PENDIENTE","APROBADA").contains(status) || !start.isAfter(now)) conflict("La reserva ya no puede cancelarse.");
            db.update("UPDATE reservation SET status='CANCELADA' WHERE id=?",id);
            return;
        }
        if (!admin) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Esta acción requiere el rol encargado.");
        switch(action) {
            case "approve", "reject" -> {
                if (!status.equals("PENDIENTE")) conflict("La solicitud ya fue atendida.");
                if (action.equals("approve")) {
                    if (!end.isAfter(now)) conflict("El horario de la reserva ya terminó.");
                    if (!equipment.get("STATE").equals("OPERATIVO")) conflict("Equipo en mantenimiento.");
                    checkAvailability(equipmentId,start,end,id);
                } else note=required(note,500);
                db.update("UPDATE reservation SET status=?,decided_by=?,decision_note=? WHERE id=?",action.equals("approve")?"APROBADA":"RECHAZADA",username,note,id);
            }
            case "deliver" -> {
                if (!status.equals("APROBADA")) conflict("Solo se entregan reservas aprobadas.");
                if (now.isBefore(start) || !now.isBefore(end)) conflict("La entrega debe realizarse dentro del horario reservado.");
                if (!equipment.get("STATE").equals("OPERATIVO")) conflict("Equipo en mantenimiento.");
                if (db.queryForObject("SELECT COUNT(*) FROM reservation WHERE equipment_id=? AND status='PRESTADO'",Integer.class,equipmentId)>0) conflict("El equipo aún no ha sido devuelto.");
                db.update("UPDATE reservation SET status='PRESTADO',delivered_at=?,delivered_by=?,initial_condition=? WHERE id=?",now,username,required(note,500),id);
            }
            case "return" -> {
                if (!status.equals("PRESTADO")) conflict("Solo se reciben préstamos abiertos.");
                db.update("UPDATE reservation SET status='DEVUELTO',returned_at=?,returned_by=?,final_condition=? WHERE id=?",now,username,required(note,500),id);
                if (damaged) db.update("UPDATE equipment SET state='MANTENIMIENTO' WHERE id=?",equipmentId);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Acción inválida.");
        }
    }
}
