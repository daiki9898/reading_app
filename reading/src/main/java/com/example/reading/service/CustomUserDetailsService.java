package com.example.reading.service;

//import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.example.reading.enums.UserRole;
import com.example.reading.input.UserRegistrationInput;
import com.example.reading.model.Account;
import com.example.reading.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByUserName(username);
		if (account == null) {
			throw new UsernameNotFoundException(username + "was not found");
		}
		return User.withUsername(account.getUserName())
				.password(account.getPassword())
				.roles(account.getRole().toString())
				.build();
	}
	
	public void insert(UserRegistrationInput userInput) {
		String hashedPassword = passwordEncoder.encode(userInput.getPassword());
//		Account account = new Account();
//		account.setUserName(userInput.getUserName());
//		account.setPassword(hashedPassword);
//		account.setRegisteredDate(LocalDateTime.now());
//		account.setRole(UserRole.USER);
		accountRepository.customSave(userInput.getUsername(), hashedPassword);
	}
	
	public Integer getUserIdbyUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByUserName(username);
		if (account == null) {
			throw new UsernameNotFoundException(username + "was not found");
		}
		return account.getUserId();
	}
	
	public boolean isUserNameExists(String username) {
		return accountRepository.existsByUserName(username);
	}
	
}