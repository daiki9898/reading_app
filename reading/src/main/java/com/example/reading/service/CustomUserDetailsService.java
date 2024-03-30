package com.example.reading.service;

import java.time.LocalDateTime;
import java.util.Optional;

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
		Account account = accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "was not found"));
		return User.withUsername(account.getUsername())
				.password(account.getPassword())
				.roles(account.getRole().toString())
				.build();
	}
	
	public void insert(UserRegistrationInput userInput) {
		String hashedPassword = passwordEncoder.encode(userInput.getPassword());
		accountRepository.customSave(userInput.getUsername(), hashedPassword);
	}
	
//	public Account findById(Integer userId) {
//		Account account = accountRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("アカウントが見つかりませんでした"));
//		return account;
//	}
	
	public Integer getUserIdbyUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "was not found"));
		return account.getUserId();
	}
	
	public Integer getUserIdByUserEmail(String userEmail) {
		Account account = accountRepository.findByUserEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("アカウントが見つかりませんでした"));
		return account.getUserId();
	}
	
	public Optional<String> getUserEmailById(Integer userId) {
		Account account = accountRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("アカウントが見つかりませんでした"));
		return Optional.ofNullable(account.getUserEmail());
	}
	
	public boolean isUsernameExists(String username) {
		return accountRepository.existsByUsername(username);
	}
	
	public boolean isUserEmailExists(String email) {
		return accountRepository.existsByUserEmail(email);
	}
	
	public void updateLastLoginDateByUsername(LocalDateTime lastLoginDate, String username) {
		accountRepository.updateLastLoginDateByUsername(lastLoginDate, username);
	}
	
	public void updateEmailById(String email, Integer userId) {
		accountRepository.updateEmailById(email, userId);
	}
	
	public void updateUsernameById(String username, Integer userId) {
		accountRepository.updateUsernameById(username, userId);
	}
	
	public void deleteById(Integer userId) {
		accountRepository.deleteById(userId);
	}
	
}
