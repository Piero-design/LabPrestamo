package pe.labprestamo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final LabService service;
    private final JdbcTemplate db;
    public ApiController(LabService service, JdbcTemplate db) { this.service=service; this.db=db; }
    public record EquipmentInput(Long id,String code,String name,String category,String state) {}
    public record ReservationInput(long equipmentId,LocalDateTime start,LocalDateTime end,String reason) {}
    public record ActionInput(String note,boolean damaged) {}

    @GetMapping("/csrf") Map<String,String> csrf(CsrfToken token) { return Map.of("token",token.getToken(),"header",token.getHeaderName()); }
    @GetMapping("/me") Map<String,Object> me(Authentication auth) {
        return db.queryForMap("SELECT username,name,role FROM app_user WHERE username=?",auth.getName());
    }
    @GetMapping("/equipment") List<Map<String,Object>> equipment(@RequestParam(required=false) LocalDateTime start,@RequestParam(required=false) LocalDateTime end) { return service.equipment(start,end); }
    @PostMapping("/admin/equipment") void equipment(@RequestBody EquipmentInput input) { service.saveEquipment(input.id(),input.code(),input.name(),input.category(),input.state()); }
    @GetMapping("/reservations") List<Map<String,Object>> reservations(Authentication auth) { return service.reservations(auth.getName()); }
    @PostMapping("/reservations") void reserve(@RequestBody ReservationInput input,Authentication auth) { service.reserve(auth.getName(),input.equipmentId(),input.start(),input.end(),input.reason()); }
    @PostMapping("/reservations/{id}/cancel") void cancel(@PathVariable long id,Authentication auth) { service.transition(id,"cancel",auth.getName(),false,"",false); }
    @GetMapping("/admin/reservations") List<Map<String,Object>> all() { return service.reservations(null); }
    @PostMapping("/admin/reservations/{id}/{action}") void action(@PathVariable long id,@PathVariable String action,@RequestBody ActionInput input,Authentication auth) {
        service.transition(id,action,auth.getName(),true,input.note(),input.damaged());
    }
    @GetMapping("/admin/report") Map<String,Object> report(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,@RequestParam(defaultValue="") String status) {
        if (from!=null && to!=null && to.isBefore(from)) throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Rango de fechas inválido.");
        var rows=service.reservations(null).stream().filter(r -> {
            LocalDate date=((Timestamp)r.get("STARTS_AT")).toLocalDateTime().toLocalDate();
            boolean overdue=r.get("STATUS").equals("PRESTADO") && ((Timestamp)r.get("ENDS_AT")).toLocalDateTime().isBefore(LocalDateTime.now());
            return (from==null || !date.isBefore(from)) && (to==null || !date.isAfter(to)) && (status.isEmpty() || status.equals(r.get("STATUS")) || (status.equals("VENCIDO") && overdue));
        }).toList();
        long overdue=rows.stream().filter(r->r.get("STATUS").equals("PRESTADO") && ((Timestamp)r.get("ENDS_AT")).toLocalDateTime().isBefore(LocalDateTime.now())).count();
        return Map.of("rows",rows,"total",rows.size(),"overdue",overdue,"open",rows.stream().filter(r->r.get("STATUS").equals("PRESTADO")).count());
    }
    @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> business(ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message",ex.getReason()==null?"Solicitud inválida":ex.getReason())); }
    @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> integrity() { return ResponseEntity.status(409).body(Map.of("message","El código del equipo ya existe o los datos no son válidos.")); }
}
