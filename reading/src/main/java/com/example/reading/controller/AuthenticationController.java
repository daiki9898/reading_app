package com.example.reading.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	public String displayLoginForm(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "timeout", required = false) String isTimeout, Model model) {
		if (error != null) {
			model.addAttribute("errorMessage", "ログインに失敗しました");
		} else if (isTimeout != null) {
			return "timeout";
		}
		return "login";
	}
	
	@GetMapping("/timeout")
	public String displayTimeoutPage(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, response, authentication);
		return "redirect:/login?timeout";
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
