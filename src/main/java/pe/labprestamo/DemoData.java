package pe.labprestamo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoData implements CommandLineRunner {
    private final JdbcTemplate db;
    private final PasswordEncoder encoder;
    public DemoData(JdbcTemplate db, PasswordEncoder encoder) { this.db = db; this.encoder = encoder; }

    @Override @Transactional
    public void run(String... args) {
        addUser("estudiante", "Ana Torres", "ESTUDIANTE", "Estudiante123!");
        addUser("estudiante2", "Luis Pérez", "ESTUDIANTE", "Estudiante123!");
        addUser("encargado", "Responsable de laboratorio", "ENCARGADO", "Encargado123!");
        if (db.queryForObject("SELECT COUNT(*) FROM equipment", Integer.class) == 0) {
            db.update("INSERT INTO equipment(code,name,category,state) VALUES (?,?,?,?)", "EQ-001", "Multímetro digital", "Medición", "OPERATIVO");
            db.update("INSERT INTO equipment(code,name,category,state) VALUES (?,?,?,?)", "EQ-002", "Osciloscopio", "Señales", "OPERATIVO");
            db.update("INSERT INTO equipment(code,name,category,state) VALUES (?,?,?,?)", "EQ-003", "Fuente de alimentación", "Electricidad", "OPERATIVO");
            db.update("INSERT INTO equipment(code,name,category,state) VALUES (?,?,?,?)", "EQ-004", "Generador de funciones", "Señales", "MANTENIMIENTO");
        }
    }
    private void addUser(String username, String name, String role, String password) {
        if (db.queryForObject("SELECT COUNT(*) FROM app_user WHERE username=?", Integer.class, username) == 0) {
            db.update("INSERT INTO app_user VALUES (?,?,?,?)", username, encoder.encode(password), name, role);
        }
    }
}
