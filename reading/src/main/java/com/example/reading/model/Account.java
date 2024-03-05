package com.example.reading.model;

import java.time.LocalDateTime;

import com.example.reading.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user_login_info")
public class Account {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="user_name")
	private String username;
	
	@Column(name="password")
	private String password;
	
	@Column(name="registered_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime registeredDate;
	
	@Column(name="last_login_date")
	private LocalDateTime lastLoginDate;
	
	@Column(name="role", columnDefinition="UserRole DEFAULT 'USER'::UserRole")
	@Enumerated(EnumType.STRING)
	private UserRole role;
}
