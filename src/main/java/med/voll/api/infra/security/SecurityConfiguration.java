package med.voll.api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF porque usamos JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT es stateless
                .authorizeHttpRequests(auth -> auth

                        // Configuración de accesos según roles y rutas

                        // Rutas accesibles para ADMIN, USER_MEDIC y USER_PATIENT (GET en /medicos/**)
                        .requestMatchers(HttpMethod.GET, "/medicos/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER_MEDIC", "ROLE_USER_PATIENT")

                        // Acceso público para las rutas de login y Swagger
                        .requestMatchers(HttpMethod.POST, "/login").permitAll() // Permite POST en /login
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Permite acceso público a Swagger

                        // Acceso completo para administradores (todas las rutas y métodos)
                        .requestMatchers("/medicos/**").hasAuthority("ROLE_ADMIN")

                        // Permisos para médicos (todas las operaciones en pacientes)
                        .requestMatchers("/pacientes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER_MEDIC")

                        // Cualquier otra solicitud debe estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Filtro para JWT
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt para manejar contraseñas
    }
}