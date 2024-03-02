package com.example.reading.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.reading.dto.UserDto;
import com.example.reading.service.UserListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final UserListService userListService;
	
	@GetMapping("/home")
	public String displayHome(Model model) {
		List<UserDto> userDtoList = userListService.getUserList();
		model.addAttribute("userList", userDtoList);
		return "admin-home";
	}
}
