package com.example.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO user_login_info (user_name, password) VALUES (?1, ?2)", nativeQuery = true)
	void customSave(String userName, String password);
	
	boolean existsByUserName(String userName);
	Account findByUserName(String userName);
}
