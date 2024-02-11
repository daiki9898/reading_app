package com.example.reading.dto;

import lombok.Data;

@Data
public class UserProfile {
	
	private String username;
	
	private int readingNumber;
	
	private int finishedNumber;
}
