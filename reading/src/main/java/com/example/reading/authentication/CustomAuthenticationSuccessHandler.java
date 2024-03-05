package com.example.reading.authentication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.reading.service.CustomUserDetailsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private final CustomUserDetailsService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		Collection<? extends GrantedAuthority> authorities= authentication.getAuthorities();
		boolean isAdmin = authorities.stream()
			.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
		
		if (isAdmin) {
			response.sendRedirect("/admin/home");
			userService.updateLastLoginDateByUsername(LocalDateTime.now(), authentication.getName());
		} else {
			response.sendRedirect("/user/home");
			userService.updateLastLoginDateByUsername(LocalDateTime.now(), authentication.getName());
		}
	}
}
