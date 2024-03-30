package com.example.reading.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.reading.input.EmailInput;
import com.example.reading.input.UserRegistrationInput;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.EmailSenderService;
import com.example.reading.service.UserStatusService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	private final EmailSenderService emailSenderService;
	
	@GetMapping("/register-user")
	public String displayUserForm(Model model) {
		model.addAttribute("userRegistrationInput", new UserRegistrationInput());
		return "user/authentication/user-registerform";
	}
	
	@GetMapping("/login")
	public String displayLoginForm(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "timeout", required = false) String timeout, Model model) {
		if (timeout != null) {
			return "user/authentication/timeout";
		}
		if (error != null) {
			model.addAttribute("errorMessage", "ログインに失敗しました");
		}
		return "user/authentication/login";
	}
	
	@GetMapping("/delete-account/success")
	public String displayDeleteSuccessPage() {
		return "user/authentication/account-deleted";
	}
	
	@GetMapping("/password-reset-link")
	public String displayPasswordResetLink(Model model) {
		model.addAttribute("emailInput", new EmailInput());
		return "user/authentication/password-reset-link";
	}
	
	@GetMapping("/password-reset-link/sent")
	public String displaySentPasswordResetLinkPage(@ModelAttribute("emailInput") EmailInput emailInput) {
		if (emailInput.getUserEmail() == null) {
			return "redirect:/password-reset-link";
		}
		return "user/authentication/password-reset-link";
	}
	
	@PostMapping("/send-password-reset-link")
	public String sendPasswordResetLink(@Validated EmailInput emailInput, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "user/authentication/password-reset-link";
		}
		Integer userId = userService.getUserIdByUserEmail(emailInput.getUserEmail());
		emailSenderService.sendEmailWithResetLink(emailInput.getUserEmail(), userId);
		redirectAttributes.addFlashAttribute("emailInput", emailInput);
		redirectAttributes.addFlashAttribute("successMessage", "successMessage");
		return "redirect:/password-reset-link/sent";
	}
	
	SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
	
	@PostMapping("/username/logout")
	public String usernameLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.logoutHandler.logout(request, response, authentication);
		 Cookie[] cookies = request.getCookies();
		 if (cookies != null) {
			 for (Cookie cookie : cookies) {
				 if ("JSESSIONID".equals(cookie.getName())) {
					 cookie.setMaxAge(0); 
	                 response.addCookie(cookie);
	                 break; 
				 }
			 }
		 }
		 redirectAttributes.addFlashAttribute("usernameSuccessMessage", "usernameSuccessMessage");
		return "redirect:/login";
	}
	
	@PostMapping("/delete-account/logout")
	public String deleteAccountLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
		this.logoutHandler.logout(request, response, authentication);
		 Cookie[] cookies = request.getCookies();
		 if (cookies != null) {
			 for (Cookie cookie : cookies) {
				 if ("JSESSIONID".equals(cookie.getName())) {
					 cookie.setMaxAge(0); 
	                 response.addCookie(cookie);
	                 break; 
				 }
			 }
		 }
		return "redirect:/delete-account/success";
	}
	
	
	@PostMapping("/register-user")
	public String registerUser(@Validated UserRegistrationInput userInput, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "user/authentication/user-registerform";
		}
		if(userService.isUsernameExists(userInput.getUsername())) {
			model.addAttribute("errorMassage", "そのユーザー名は既に登録されています");
			return "user/authentication/user-registerform";
		}
		userService.insert(userInput);
		userStatusService.insert(userService.getUserIdbyUsername(userInput.getUsername()));
		redirectAttributes.addFlashAttribute("successMessage", "successMessage");
		return "redirect:/login";
	}
}
