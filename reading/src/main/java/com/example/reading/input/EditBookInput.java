package com.example.reading.input;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.example.reading.validator.MaxFileSize;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EditBookInput {
	@NotNull(message="IDがNullになっています")
	private Integer bookId;
	
	@Pattern(regexp = "^[^\\s　]+.*", message = "空白文字は無効です")
	private String title;
	
	private String author;
	
	private String genre;
	
	@NotNull(message="日付を選択してください")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	private String imageOptions;
	
	@MaxFileSize
	private MultipartFile imgFile;
	
	private String comment;
}
