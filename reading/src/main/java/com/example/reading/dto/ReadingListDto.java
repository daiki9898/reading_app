package com.example.reading.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReadingListDto {
	
	private Integer userId;
	
    private Integer bookId;
    
    private LocalDate startDate;
    
    private BookInfoDto bookInfoDto;
}
