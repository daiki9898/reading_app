package com.example.reading.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="finished_list_registration")
public class FinishedListRegistration {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="book_id")
	private Integer bookId;
	
	@Column(name="start_date")
	private LocalDate startDate;
	
	@Column(name="end_date")
	private LocalDate endDate;
	
	public FinishedListRegistration(Integer userId, Integer bookId, LocalDate startDate, LocalDate endDate) {
		this.userId = userId;
		this.bookId = bookId;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}