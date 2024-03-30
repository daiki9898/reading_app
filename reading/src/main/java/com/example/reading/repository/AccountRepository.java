package com.example.reading.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.reading.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	@Modifying
	@Query(value = "INSERT INTO user_login_info (user_name, password) VALUES (?1, ?2)", nativeQuery = true)
	void customSave(String userName, String password);
	
	@Query(value = "SELECT * FROM user_login_info WHERE role = 'USER'", nativeQuery = true)
	List<Account> findAllExceptADMIN();
	
	boolean existsByUsername(String username);
	boolean existsByUserEmail(String userEmail);
	Optional<Account> findByUsername(String username);
	Optional<Account> findByUserEmail(String userEmail);
	
	@Modifying
	@Query(value = "UPDATE user_login_info SET last_login_date = ?1 WHERE user_name = ?2", nativeQuery = true)
	void updateLastLoginDateByUsername(LocalDateTime localDateTime, String usernanme);
	
	@Modifying
	@Query(value = "UPDATE user_login_info SET user_email = ?1 WHERE user_id = ?2", nativeQuery = true)
	void updateEmailById(String email, Integer userId);
	
	@Modifying
	@Query(value = "UPDATE user_login_info SET user_name = ?1 WHERE user_id = ?2", nativeQuery = true)
	void updateUsernameById(String username, Integer userId);
	
}
