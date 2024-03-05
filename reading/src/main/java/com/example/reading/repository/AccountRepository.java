package com.example.reading.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;

import com.example.reading.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	@Modifying
	@Query(value = "INSERT INTO user_login_info (user_name, password) VALUES (?1, ?2)", nativeQuery = true)
	void customSave(String userName, String password);
	
	@Query(value = "SELECT * FROM user_login_info WHERE role = 'USER'", nativeQuery = true)
	List<Account> findAllExceptADMIN();
	
	boolean existsByUsername(String username);
	Optional<Account> findByUsername(String username);
	
	@Modifying
	@Query(value = "UPDATE user_login_info SET last_login_date = ?1 WHERE user_name = ?2", nativeQuery = true)
	void updateLastLoginDateByUsername(LocalDateTime localDateTime, String usernanme);
	
}
