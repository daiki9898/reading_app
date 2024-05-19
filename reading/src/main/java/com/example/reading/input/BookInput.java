package com.example.reading.input;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.example.reading.validator.MaxFileSize;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookInput {
	@Pattern(regexp = "^[^\\s　]+.*", message = "空白文字は無効です")
	private String title;
	
	private String genre;
	
	private String author;
	
	@NotNull(message="日付を選択してください")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@MaxFileSize
	private MultipartFile imgFile;
	
	private String comment;
}
