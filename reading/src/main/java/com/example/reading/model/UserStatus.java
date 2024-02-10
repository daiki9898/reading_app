package com.example.reading.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user_status")
public class UserStatus {
	
	@Id
	@Column(name="user_id")
	private Integer id;
	
	@Column(name="reading_book_number")
	private int reading_book_number;
	
	@Column(name="reading_image_number")
	private int reading_image_number;
	
	@Column(name="finished_book_number")
	private int finished_book_number;
	
	@Column(name="finished_image_number")
	private int finished_image_number;
}
