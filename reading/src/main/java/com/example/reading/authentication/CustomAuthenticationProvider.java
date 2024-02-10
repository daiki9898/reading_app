package com.example.reading.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider{
	private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String inputPassword = (String)authentication.getCredentials();
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if(passwordEncoder.matches(inputPassword, userDetails.getPassword())) {
			return new UsernamePasswordAuthenticationToken(username, inputPassword, userDetails.getAuthorities());
		} else {
			throw new BadCredentialsException("認証に失敗しました");
		}
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
