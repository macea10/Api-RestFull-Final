package com.henrry.Api_crud.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // Indica que esta clase es una clase de configuración de Spring
@EnableWebSecurity // Habilita la configuración de seguridad web en la aplicación
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable) // Deshabilita la protección CSRF (solo para desarrollo y pruebas)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/public/api/**").permitAll() // Permite acceso público a "/api/saludo"
						.requestMatchers("/api/productos").hasRole("ADMIN") // Restringe "/api/productos" solo a usuarios con rol "ADMIN"
						.anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
				)
				.httpBasic(Customizer.withDefaults()) // Habilita la autenticación básica HTTP
				.exceptionHandling(ex -> ex // Configura el manejo de excepciones
						.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/api/productos")) // Redirige a "/dashboard" al autenticarse
				)
				.logout(logout -> logout // Configura el logout
						.invalidateHttpSession(true) // Invalida la sesión HTTP al hacer logout
						.clearAuthentication(true) // Limpia la autenticación al hacer logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Establece la URL para el logout
						.logoutSuccessUrl("/public/api/saludo").permitAll() // Redirige a "/public/api/saludo" después del logout
				)
				.sessionManagement(session -> session // Configura el manejo de sesiones
						.invalidSessionUrl("/logout") // Establece la URL para sesiones inválidas
						.sessionFixation().newSession() // Fija la sesión creando una nueva
				);
		return http.build(); // Construye y devuelve la cadena de filtros de seguridad}
	}
	@Bean // Define un bean de Spring para el codificador de contraseñas
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Usa BCrypt para codificar contraseñas
	}

	@Bean // Define un bean de Spring para el servicio de detalles de usuario
	public UserDetailsService userDetailsService() {
		// Crea un usuario administrador con nombre de usuario "admin" y contraseña "admin123"
		UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder().encode("admin123")) // Codifica la contraseña
				.roles("ADMIN") // Asigna el rol "ADMIN"
				.build();

		// Devuelve un administrador de detalles de usuario en memoria con el usuario creado
		return new InMemoryUserDetailsManager(admin);
	}
}
