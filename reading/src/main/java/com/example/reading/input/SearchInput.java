package com.example.reading.input;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class SearchInput {
	
	private String title;
	
	private String genre;
	
	private String author;
	
	@DateTimeFormat(pattern = "yyyy-MM")
	private YearMonth roughStartDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate specificStartDate;
}
