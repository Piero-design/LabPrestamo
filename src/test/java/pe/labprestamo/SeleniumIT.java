package pe.labprestamo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;

/** Browser acceptance tests. Each test has a fresh browser and isolated database fixtures. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SeleniumIT {
    private ConfigurableApplicationContext app;
    private JdbcTemplate db;
    private ChromeDriver driver;
    private WebDriverWait wait;
    private String base;

    @RegisterExtension
    final AfterTestExecutionCallback evidence = context -> {
        if (driver == null) return;
        Path directory = Path.of("target", "selenium", context.getRequiredTestMethod().getName());
        Files.createDirectories(directory);
        Files.write(directory.resolve(context.getExecutionException().isPresent() ? "fallo.png" : "resultado.png"), driver.getScreenshotAs(OutputType.BYTES));
        Files.writeString(directory.resolve("entorno.txt"), driver.getCapabilities().toString());
    };

    @BeforeAll void startApp() {
        app = SpringApplication.run(Application.class,
            "--server.port=0", "--spring.datasource.url=jdbc:h2:mem:selenium;DB_CLOSE_DELAY=-1",
            "--spring.datasource.username=sa", "--spring.datasource.password=");
        db = app.getBean(JdbcTemplate.class);
        base = "http://127.0.0.1:" + app.getEnvironment().getProperty("local.server.port");
    }
    @AfterAll void stopApp() { if (app != null) app.close(); }
    @BeforeEach void browser() {
        db.update("DELETE FROM reservation");
        db.update("UPDATE equipment SET state='OPERATIVO' WHERE code='EQ-001'");
        ChromeOptions options = new ChromeOptions();
        options.setBrowserVersion(System.getProperty("selenium.browserVersion", "stable"));
        if (!Boolean.getBoolean("selenium.visible")) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1000", "--lang=es-PE");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(base);
        visible("#login-form");
    }
    @AfterEach void closeBrowser() { if (driver != null) { driver.quit(); driver=null; } }
    private WebElement visible(String css) { return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css))); }
    private void click(String css) { wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(css))).click(); }
    private void fill(String css,String value) { WebElement element=visible(css);element.clear();element.sendKeys(value); }
    private void login(String username,String password) {
        fill("#login-form [name=username]",username);fill("#login-form [name=password]",password);click("#login-form button");
    }
    private void student() { login("estudiante","Estudiante123!");visible(".card"); }
    private void manager() { login("encargado","Encargado123!");visible(".card"); }
    private void dateInput(String css,LocalDateTime date) {
        WebElement input=visible(css);
        driver.executeScript("arguments[0].value=arguments[1];arguments[0].dispatchEvent(new Event('input',{bubbles:true}));arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",input,date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    }
    private long equipmentId() { return db.queryForObject("SELECT id FROM equipment WHERE code='EQ-001'",Long.class); }
    private void reservationForm(LocalDateTime start,LocalDateTime end) {
        click("[data-reserve='"+equipmentId()+"']");
        dateInput("#modal-form [name=start]",start);dateInput("#modal-form [name=end]",end);
        fill("#modal-form [name=reason]","Práctica Selenium G09");click("#modal-form button[type=submit]");
    }
    private void seedPending() {
        db.update("INSERT INTO reservation(equipment_id,username,starts_at,ends_at,reason,status) VALUES (?,?,?,?,?,'PENDIENTE')",equipmentId(),"estudiante",LocalDateTime.now().minusMinutes(5),LocalDateTime.now().plusHours(1),"Préstamo preparado para verificar transiciones");
    }
    private void submitAction(String action,String note) {
        click("#all-reservations [data-action='"+action+"']");
        if (note!=null) fill("#modal-form [name=note]",note);
        click("#modal-form button[type=submit]");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("modal")));
    }

    @Test @DisplayName("SEL-01 Acceso correcto RF-01")
    void successfulLogin() { student();assertEquals("Ana Torres",visible("#user-name").getText());assertEquals("ESTUDIANTE",visible("#user-role").getText()); }

    @Test @DisplayName("SEL-02 Credenciales inválidas RF-01")
    void invalidLogin() {
        login("estudiante","incorrecta");
        assertTrue(visible("#notice.error").getText().contains("incorrectos"));
        assertTrue(visible("#login-form").isDisplayed());assertFalse(driver.findElement(By.id("workspace")).isDisplayed());
    }

    @Test @DisplayName("SEL-03 Restricción por rol RF-02")
    void roleRestriction() {
        student();assertFalse(driver.findElement(By.cssSelector("[data-view=manage]")).isDisplayed());
        Object status=driver.executeAsyncScript("const done=arguments[arguments.length-1];fetch('/api/admin/reservations').then(r=>done(r.status)).catch(()=>done(0));");
        assertEquals(403L,((Number)status).longValue());
    }

    @Test @DisplayName("SEL-04 Registro válido RF-05 RF-06")
    void validReservation() {
        student();reservationForm(LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(1).plusHours(1));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("modal")));
        click("[data-view=mine]");assertTrue(visible("#my-reservations tbody").getText().contains("PENDIENTE"));
        assertTrue(visible("#my-reservations tbody").getText().contains("Práctica Selenium G09"));
        assertEquals(1,db.queryForObject("SELECT COUNT(*) FROM reservation",Integer.class));
    }

    @Test @DisplayName("SEL-05 Datos inválidos RF-05")
    void invalidInterval() {
        student();reservationForm(LocalDateTime.now().plusDays(1).plusHours(1),LocalDateTime.now().plusDays(1));
        assertTrue(visible("#notice.error").getText().contains("posterior"));
        assertEquals(0,db.queryForObject("SELECT COUNT(*) FROM reservation",Integer.class));
        assertTrue(visible("#modal").isDisplayed());
    }

    @Test @DisplayName("SEL-06 Consulta de disponibilidad RF-04")
    void availability() {
        seedPending();student();
        dateInput("#availability [name=start]",LocalDateTime.now().plusMinutes(10));
        dateInput("#availability [name=end]",LocalDateTime.now().plusMinutes(30));click("#availability button");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("equipment-cards"),"RESERVADO"));
        assertFalse(driver.findElement(By.cssSelector("[data-reserve='"+equipmentId()+"']")).isEnabled());
        assertEquals(4,driver.findElements(By.cssSelector(".card")).size());
    }

    @Test @DisplayName("SEL-07 Aprobación entrega y devolución RF-07 RF-08 RF-09")
    void loanLifecycle() {
        seedPending();manager();click("[data-view=manage]");visible("#all-reservations tbody");
        submitAction("approve",null);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("all-reservations"),"APROBADA"));
        submitAction("deliver","Equipo completo y operativo");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("all-reservations"),"PRESTADO"));
        submitAction("return","Devuelto sin daños");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("all-reservations"),"DEVUELTO"));
        assertTrue(visible("#all-reservations tbody").getText().contains("Devuelto sin daños"));
        assertEquals("DEVUELTO",db.queryForObject("SELECT status FROM reservation",String.class));
    }

    @Test @DisplayName("SEL-08 Cierre de sesión RF-01")
    void logout() {
        student();click("#logout");visible("#login-form");
        driver.navigate().refresh();visible("#login-form");assertFalse(driver.findElement(By.id("workspace")).isDisplayed());
        Object status=driver.executeAsyncScript("const done=arguments[arguments.length-1];fetch('/api/me').then(r=>done(r.status)).catch(()=>done(0));");
        assertEquals(401L,((Number)status).longValue());
    }
}
