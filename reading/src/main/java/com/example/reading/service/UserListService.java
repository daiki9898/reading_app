package com.example.reading.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.dto.UserDto;
import com.example.reading.model.Account;
import com.example.reading.model.UserStatus;
import com.example.reading.repository.AccountRepository;
import com.example.reading.repository.UserStatusRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserListService {

	private final AccountRepository accountRepository;
	private final UserStatusRepository userStatusRepository;
	
	// ログインがどのくらい前か判定するメソッド
	public String formatTimeDifference(LocalDateTime startDate) {
		Duration duration = Duration.between(startDate, LocalDateTime.now());
		long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        if (days > 0) {
            return days + "日前";
        } else if (hours > 0) {
            return hours + "時間前";
        } else {
        	if (minutes == 0) {
        		return "たった今";
        	}
            return minutes + "分前";
        }
	}
	
	public List<UserDto> getUserList() {
		List<Account> accounts = accountRepository.findAllExceptADMIN();
		List<UserDto> userDtoList = new ArrayList<>();
		
		for (Account account : accounts) {
			UserDto userDto = new UserDto();
			userDto.setUsername(account.getUsername());
			userDto.setRegisteredDate(account.getRegisteredDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
			if (account.getLastLoginDate() != null) {
				userDto.setLoginDuration(formatTimeDifference(account.getLastLoginDate()));
			}
			
			UserStatus userStatus = userStatusRepository.findById(account.getUserId()).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
			userDto.setReadingBookNumber(userStatus.getReadingBookNumber());
			userDto.setFinishedBookNumber(userStatus.getFinishedBookNumber());
			userDto.setUploadedImageNumber(0);
			userDto.setTotalImageSize(0);
			userDtoList.add(userDto);
		}
		return userDtoList;
	}
}
