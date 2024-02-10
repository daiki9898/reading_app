package com.example.reading.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FinishedListDto {
	
	private Integer userId;
	
    private Integer bookId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BookInfoDto bookInfoDto;
}
