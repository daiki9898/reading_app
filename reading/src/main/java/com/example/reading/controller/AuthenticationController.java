package com.example.reading.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.reading.input.UserRegistrationInput;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	
	@GetMapping("/register-user")
	public String displayUserForm(Model model) {
		model.addAttribute("userRegistrationInput", new UserRegistrationInput());
		return "user-registerform";
	}
	
	@GetMapping("/login")
	public String displayLoginForm(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "timeout", required = false) String timeout, Model model) {
		if (timeout != null) {
			return "timeout";
		}
		if (error != null) {
			model.addAttribute("errorMessage", "ログインに失敗しました");
		}
		return "login";
	}
	
	@PostMapping("/register-user")
	public String registerUser(@Validated UserRegistrationInput userInput, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "user-registerform";
		}
		if(userService.isUserNameExists(userInput.getUsername())) {
			model.addAttribute("errorMassage", "そのユーザー名は既に登録されています");
			return "user-registerform";
		}
		userService.insert(userInput);
		userStatusService.insert(userService.getUserIdbyUsername(userInput.getUsername()));
		redirectAttributes.addFlashAttribute("successMessage", "successMessage");
		return "redirect:/login";
	}
}
