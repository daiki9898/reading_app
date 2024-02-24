package com.example.reading.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.reading.authentication.CustomAuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationProvider customAuthenticationProvider;
	private final DataSource dataSource;
	
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.addAllowedOrigin("http://localhost:8080");
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
					.ignoringRequestMatchers(new AntPathRequestMatcher("/register-user"))
			)
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers("/", "/register-user", "/login", "/timeout").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((formLogin) -> formLogin
				.loginPage("/login")
				.loginProcessingUrl("/execute-login")
				.defaultSuccessUrl("/user/home")
			)
			.logout(logout -> logout
				.deleteCookies("JSESSIONID")
				.permitAll()
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
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new TimeoutUrlAuthenticationEntryPoint("/login");
    }
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}
}
