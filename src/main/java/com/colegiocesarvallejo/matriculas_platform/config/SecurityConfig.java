package com.colegiocesarvallejo.matriculas_platform.config;

import com.colegiocesarvallejo.matriculas_platform.security.JwtAuthenticationEntryPoint;
import com.colegiocesarvallejo.matriculas_platform.security.JwtRequestFilter;
import com.colegiocesarvallejo.matriculas_platform.service.usuario.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/ws/**").permitAll()
                        .requestMatchers("/", "/index.html", "/login.html", "/registro.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/padre-dashboard.html").hasAuthority("ROLE_PADRE")
                        .requestMatchers("/admin-dashboard.html").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/Gestion-pagos.html").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/Gestion-usuarios.html").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/Gestion-profesores.html").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/Gestion-estudiante.html").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/detalle-estudiante.html").hasAuthority("ROLE_PADRE")
                        .requestMatchers("/pagos.html").hasAuthority("ROLE_PADRE")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/padre/**").hasAuthority("ROLE_PADRE")
                        .requestMatchers("/api/estudiantes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PADRE")
                        .requestMatchers("/api/matriculas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PADRE")
                        .requestMatchers("/api/cursos/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PADRE")
                        .requestMatchers("/api/dashboard/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PADRE")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .defaultSuccessUrl("/padre-dashboard.html", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        http.securityMatcher("/api/**")
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
