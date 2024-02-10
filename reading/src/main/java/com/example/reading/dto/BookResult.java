package com.example.reading.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BookResult {
	
	private Integer bookId;
	
	private String title;
	
	private LocalDate startDate;
	
//	null可
	private LocalDate endDate;
	
	private String imgFile;
	
	private String comment;
}
