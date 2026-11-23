package pe.labprestamo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService users(JdbcTemplate db) {
        return username -> db.query("SELECT * FROM app_user WHERE username = ?", (rs, row) ->
            User.withUsername(rs.getString("username")).password(rs.getString("password"))
                .roles(rs.getString("role")).build(), username)
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Usuario inválido"));
    }

    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/app.js", "/style.css", "/api/csrf", "/error").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ENCARGADO")
            .anyRequest().authenticated())
            .formLogin(form -> form.loginProcessingUrl("/api/login")
                .successHandler((req, res, auth) -> res.setStatus(204))
                .failureHandler((req, res, ex) -> res.sendError(401, "Credenciales inválidas")))
            .logout(logout -> logout.logoutUrl("/api/logout")
                .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                .accessDeniedHandler((req, res, ex) -> res.sendError(403)));
        return http.build();
    }
}
