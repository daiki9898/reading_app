package com.example.reading.controller;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.example.reading.dto.UserProfile;
import com.example.reading.input.user_settings.UserEmailInput;
import com.example.reading.input.user_settings.UserOptionInput;
import com.example.reading.input.user_settings.UsernameInput;
import com.example.reading.model.FinishedListRegistration;
import com.example.reading.model.ReadingListRegistration;
import com.example.reading.model.UserStatus;
import com.example.reading.service.BookService;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.FinishedListService;
import com.example.reading.service.ReadingListService;
import com.example.reading.service.UserStatusService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/settings")
public class UserUpdateController {
	
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	private final ReadingListService readingListService;
	private final FinishedListService finishedListService;
	private final BookService bookService;
	
	// 共通処理
	public Map<String, Object> addUserProfileData(Model model) {
		// get username
		SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        
        // 値のセット
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);

        Integer userId = userService.getUserIdbyUsername(username);
        UserStatus userStatus = userStatusService.findById(userId);
        userProfile.setReadingNumber(userStatus.getReadingBookNumber());
        userProfile.setFinishedNumber(userStatus.getFinishedBookNumber());
        model.addAttribute("userProfile", userProfile);
        
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("userId", userId);
        userInfo.put("genreTagOpenStatus", userStatus.getGnereTagOpenStatus());
 
        return userInfo;
	}
	
	@GetMapping
	public String diplayProfile(Model model, @ModelAttribute UsernameInput usernameInput, @ModelAttribute UserEmailInput userEmailInput) {
		UserOptionInput optionInput = new UserOptionInput();
		Map<String, Object> userInfo = addUserProfileData(model);
		Integer userId = (Integer) userInfo.get("userId");
		optionInput.setUserId(userId);
		boolean genreTagOpenStatus = (boolean) userInfo.get("genreTagOpenStatus");
		if (genreTagOpenStatus == true) {
			optionInput.setGenreTagStatus("open");
		}
		model.addAttribute("optionInput", optionInput);
		
		Optional<String> userEmail = userService.getUserEmailById(userId);
		userEmail.ifPresent(email -> {
			model.addAttribute("email", email);
		});
		return "user/authentication/user-profile";
	}
	
	@PostMapping("/update-username/{id}")
	public ModelAndView updateUsernmae(@PathVariable String id, @Validated UsernameInput usernameInput, BindingResult bindingResult, Model model, HttpServletRequest request) throws NumberFormatException {
		Integer userId = Integer.valueOf(id);
		if (bindingResult.hasErrors()) {
			UserOptionInput optionInput = new UserOptionInput();
			Map<String, Object> userInfo = addUserProfileData(model);
			optionInput.setUserId(userId);
			boolean genreTagOpenStatus = (boolean) userInfo.get("genreTagOpenStatus");
			if (genreTagOpenStatus == true) {
				optionInput.setGenreTagStatus("open");
			}
			model.addAttribute("optionInput", optionInput);
			model.addAttribute("userEmailInput", new UserEmailInput());
			
			Optional<String> userEmail = userService.getUserEmailById(userId);
			userEmail.ifPresent(email -> {
				model.addAttribute("email", email);
			});
			
			return new ModelAndView("user/authentication/user-profile");
		}
		userService.updateUsernameById(usernameInput.getUsername(), userId);
		request.setAttribute(
			      View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
		
		return new ModelAndView("redirect:/username/logout");
	}
	
	@PostMapping("/update-email/{id}")
	public String updateEmail(@PathVariable String id, @Validated UserEmailInput emailInput, BindingResult bindingResult, Model model) throws NumberFormatException {
		Integer userId = Integer.valueOf(id);
		if (bindingResult.hasErrors()) {
			UserOptionInput optionInput = new UserOptionInput();
			Map<String, Object> userInfo = addUserProfileData(model);
			optionInput.setUserId(userId);
			boolean genreTagOpenStatus = (boolean) userInfo.get("genreTagOpenStatus");
			if (genreTagOpenStatus == true) {
				optionInput.setGenreTagStatus("open");
			}
			model.addAttribute("optionInput", optionInput);
			model.addAttribute("usernameInput", new UsernameInput());
			
			Optional<String> userEmail = userService.getUserEmailById(userId);
			userEmail.ifPresent(email -> {
				model.addAttribute("email", email);
			});
			
			return "user/authentication/user-profile";
		}
		userService.updateEmailById(emailInput.getEmailAddress(), userId);
		return "redirect:/user/settings";
	}
	
	@PostMapping("/update-status/{id}")
	public String editProfile(@PathVariable String id, @Validated UserOptionInput optionInput, BindingResult bindingResult, Model model) {
		Integer userId = Integer.valueOf(id);
		if (bindingResult.hasErrors()) {
			addUserProfileData(model);
			Optional<String> userEmail = userService.getUserEmailById(userId);
			userEmail.ifPresent(email -> {
				model.addAttribute("email", email);
			});
			model.addAttribute("usernameInput", new UsernameInput());
			model.addAttribute("userEmailInput", new UserEmailInput());
			
			return "user/authentication/user-profile";
		}
		if (optionInput.getGenreTagStatus() == null) {
			userStatusService.updateGenreTagStatus(false, userId);
		} else if (optionInput.getGenreTagStatus().equals("open")){
			userStatusService.updateGenreTagStatus(true, userId);
		}
		return "redirect:/user/settings";
	}
	
	@PostMapping("/delete-email/{id}")
	public String deleteEmail(@PathVariable String id) throws NumberFormatException {
		Integer userId = Integer.valueOf(id);
		userService.updateEmailById(null, userId);
		return "redirect:/user/settings";
	}
	
	@PostMapping("/delete-account/{id}")
	public ModelAndView deleteAccount(@PathVariable String id, HttpServletRequest request) throws NumberFormatException {
		Integer userId = Integer.valueOf(id);
		// 関連情報の削除
		UserStatus userStatus = userStatusService.deleteById(userId);
		if (userStatus.getReadingBookNumber() != 0) {
			List<ReadingListRegistration> readingListRegistrations = readingListService.deleteByUserId(userId);
			for (ReadingListRegistration readingListRegistration : readingListRegistrations) {
				try {
					bookService.delete(readingListRegistration.getBookId());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		if (userStatus.getFinishedBookNumber() != 0) {
			List<FinishedListRegistration> finishedListRegistrations = finishedListService.deleteByUserId(userId);
			for (FinishedListRegistration finishedListRegistration : finishedListRegistrations) {
				try {
					bookService.delete(finishedListRegistration.getBookId());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		// delete account
		userService.deleteById(userId);
		request.setAttribute(
			      View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
		
		return new ModelAndView("redirect:/delete-account/logout");
	}
}
