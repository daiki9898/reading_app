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
	private Integer userId;
	
	@Column(name="reading_book_number")
	private int readingBookNumber;
	
	@Column(name="finished_book_number")
	private int finishedBookNumber;
	
	@Column(name="uploaded_image_number")
	private int uploadedImageNumber;
	
	@Column(name="total_image_size")
	private long totalImageSize;
	
	@Column(name="genre_tag_open_status")
	private Boolean gnereTagOpenStatus;
}
