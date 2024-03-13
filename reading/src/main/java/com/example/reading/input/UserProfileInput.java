package com.example.reading.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileInput {
	
	@NotNull(message="IDがNullになっています")
	private Integer userId;

	@Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",  message = "半角英数字8〜20文字で入力してください")
	private String username;
	
	@Email(message = "不正な形式です")
	private String emailAddress;
	
	@NotNull
	private String genreTagStatus;
	
	public UserProfileInput() {
        this.genreTagStatus = "open"; // set default value
    }
}
