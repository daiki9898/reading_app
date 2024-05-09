package com.example.reading.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.reading.authentication.CustomAuthenticationProvider;
import com.example.reading.authentication.CustomAuthenticationSuccessHandler;
import com.example.reading.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationProvider customAuthenticationProvider;
	private final DataSource dataSource;
	private final CustomUserDetailsService userService;
	
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.addAllowedOrigin("https://reading-app.onrender.com");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authenticationProvider(customAuthenticationProvider)
			.cors(customizer -> customizer.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf
				.csrfTokenRepository(new HttpSessionCsrfTokenRepository())
			)
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers("/", "/register-user", "/login", "/delete-account/success", "/password-reset-link", "/send-password-reset-link", "/password-reset-link/sent", "/password-reset/verify", "password-reset").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/admin/**").fullyAuthenticated()
				.anyRequest().authenticated()
			)
			.formLogin((formLogin) -> formLogin
				.loginPage("/login")
				.loginProcessingUrl("/execute-login")
				.successHandler(authenticationSuccessHandler())
			)
			.logout(logout -> logout
				.deleteCookies("JSESSIONID")
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
			).rememberMe(remember -> remember
				.tokenRepository(persistentTokenRepository())
			).sessionManagement(session -> session
				.maximumSessions(1).expiredUrl("/logout")
			).exceptionHandling(exception -> exception
				.authenticationEntryPoint(authenticationEntryPoint())
			);
		return http.build();
	}
	
	@Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new TimeoutUrlAuthenticationEntryPoint("/login");
    }
	
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler(userService);
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}
}
