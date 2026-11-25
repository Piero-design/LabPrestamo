package pe.labprestamo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

/** Doble sin conexiones: resultados preparados y registro de escrituras. */
class BusinessTestSupport {
    final FakeDb db = new FakeDb();
    final LabService service = new LabService(db);
    final LocalDateTime future = LocalDateTime.now().plusDays(2);
    static class FakeDb extends JdbcTemplate {
        final Deque<List<Map<String,Object>>> rows = new ArrayDeque<>();
        final Deque<Integer> counts = new ArrayDeque<>();
        final List<Write> writes = new ArrayList<>();
        @Override public List<Map<String,Object>> queryForList(String sql,Object... args) { return rows.removeFirst(); }
        @Override public List<Map<String,Object>> queryForList(String sql) { return rows.removeFirst(); }
        @Override public <T> T queryForObject(String sql,Class<T> type,Object... args) { return type.cast(counts.removeFirst()); }
        @Override public int update(String sql,Object... args) { writes.add(new Write(sql,List.of(args)));return 1; }
    }
    record Write(String sql,List<Object> args) {}
    void equipment(String state) { db.rows.add(List.of(Map.of("STATE",state))); }
    void transition(String status, LocalDateTime start, LocalDateTime end, String state) {
        db.rows.add(List.of(Map.of("EQUIPMENT_ID",1L)));equipment(state);
        db.rows.add(List.of(Map.of("STATUS",status,"STARTS_AT",Timestamp.valueOf(start),"ENDS_AT",Timestamp.valueOf(end),"USERNAME","ana")));
    }
    void error(int status, org.junit.jupiter.api.function.Executable body) {
        var ex=assertThrows(ResponseStatusException.class,body);assertEquals(status,ex.getStatusCode().value());assertTrue(db.writes.isEmpty(),"Un rechazo no debe escribir");
    }
}
