package com.example.reading.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.model.UserStatus;
import com.example.reading.repository.UserStatusRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserStatusService {
	
	private final UserStatusRepository userStatusRepository;
	
	public void insert(Integer userId) {
		userStatusRepository.customSave(userId);
	}
	
	public UserStatus findById(Integer userId) {
		UserStatus userStatus = userStatusRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("ユーザー情報は見つかりませんでした"));
		return userStatus;
	}
	
	public void updateReadingNumber(Integer userId, int bookNumber) {
		UserStatus userStatus = userStatusRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("ユーザー情報は見つかりませんでした"));
		int nowBookNumber = userStatus.getReadingBookNumber();
		userStatus.setReadingBookNumber(nowBookNumber += bookNumber);
		userStatusRepository.save(userStatus);
	}
	
	public void updateFinishedNumber(Integer userId, int bookNumber) {
		UserStatus userStatus = userStatusRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("ユーザー情報は見つかりませんでした"));
		int nowBookNumber = userStatus.getFinishedBookNumber();
		userStatus.setFinishedBookNumber(nowBookNumber += bookNumber);
		userStatusRepository.save(userStatus);
	}
	
	public UserStatus deleteById(Integer userId) {
		UserStatus userStatus = userStatusRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("ユーザー情報は見つかりませんでした"));
		userStatusRepository.deleteById(userId);
		return userStatus;
	}
	
}
