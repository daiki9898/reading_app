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
@Table(name="reading_list_registration")
public class ReadingListRegistration {

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
	
	public ReadingListRegistration(Integer userId, Integer bookId, LocalDate startDate) {
		this.userId = userId;
		this.bookId = bookId;
		this.startDate = startDate;
	}
}
