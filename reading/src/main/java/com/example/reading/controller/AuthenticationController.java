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
import com.example.reading.input.password_reset.PasswordInput;
import com.example.reading.input.password_reset.PasswordResetVerificationInput;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.EmailSenderService;
import com.example.reading.service.PasswordResetTokenService;
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
	private final PasswordResetTokenService passwordResetTokenService;
	
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
		return "user/authentication/password-reset/password-reset-link";
	}
	
	@GetMapping("/password-reset-link/sent")
	public String displaySentPasswordResetLinkPage(@ModelAttribute("emailInput") EmailInput emailInput) {
		if (emailInput.getUserEmail() == null) {
			return "redirect:/password-reset/password-reset-link";
		}
		return "user/authentication/password-reset/password-reset-link";
	}
	
	@GetMapping("/password-reset/verify")
	public String displayPasswordResetVerifyForm(Model model, @RequestParam String token) {
		if (passwordResetTokenService.isValidOnetimeToken(token)) {
			PasswordResetVerificationInput verifyInput = new PasswordResetVerificationInput();
			verifyInput.setToken(token);
			model.addAttribute("verifyInput", verifyInput);
			model.addAttribute("successMessage", "successMessage");
		} else {
			passwordResetTokenService.deleteByOnetimeToken(token);
			model.addAttribute("tokenError", "tokenError");
		}
		return "user/authentication/password-reset/password-reset-verifyform";
	}
	
	@GetMapping("/password-reset")
	public String displayPasswordResetForm(Model model, @RequestParam String token) {
		if (!passwordResetTokenService.isValidOnetimeToken(token)) {
			return "redirect:/password-reset/verify?token=" + token;
		}
		PasswordInput passwordInput = new PasswordInput();
		passwordInput.setToken(token);
		model.addAttribute("passwordInput", passwordInput);
		return "user/authentication/password-reset/new-password-registrationform";
	}
	
	@PostMapping("/password-reset")
	public String resetPassword(@Validated PasswordInput passwordInput, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		String token = passwordInput.getToken();
		if (!passwordResetTokenService.isValidOnetimeToken(token)) {
			return "redirect:/password-reset/verify?token=" + token;
		}
		if (bindingResult.hasErrors()) {
			return "user/authentication/password-reset/new-password-registrationform";
		}
		userService.updatePasswordById(passwordInput.getPassword(), passwordResetTokenService.getUserIdByToken(token));
		passwordResetTokenService.deleteByOnetimeToken(token);
		redirectAttributes.addFlashAttribute("passwordResetSuccessMessage", "passwordResetSuccessMessage");
		return "redirect:/login";
	}
	
	
	@PostMapping("/password-reset/verify")
	public String verifyPasswordResetForm(PasswordResetVerificationInput verifyInput, RedirectAttributes redirectAttributes) {
		String token = verifyInput.getToken();
		if (!passwordResetTokenService.isValidOnetimeToken(token)) {
			return "redirect:/password-reset/verify?token=" + token;
		}
		if(!passwordResetTokenService.isPasswordResetAllowed(userService.getUserIdbyUsername(verifyInput.getUsername()), verifyInput.getSecretWord())) {
			redirectAttributes.addFlashAttribute("errorMessage", "errorMessage");
			return "redirect:/password-reset/verify?token=" + token;
		}
		return "redirect:/password-reset?token=" + token;
	}
	
	@PostMapping("/send-password-reset-link")
	public String sendPasswordResetLink(@Validated EmailInput emailInput, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "user/authentication/password-reset/password-reset-link";
		}
		Integer userId = userService.getUserIdByUserEmail(emailInput.getUserEmail());
		emailSenderService.sendEmailWithResetLink(emailInput.getUserEmail(), emailInput.getSecretWord(), userId);
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
