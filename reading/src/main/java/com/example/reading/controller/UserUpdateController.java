package com.example.reading.controller;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.reading.dto.UserProfile;
import com.example.reading.input.UserProfileInput;
import com.example.reading.model.FinishedListRegistration;
import com.example.reading.model.ReadingListRegistration;
import com.example.reading.model.UserStatus;
import com.example.reading.service.BookService;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.FinishedListService;
import com.example.reading.service.ReadingListService;
import com.example.reading.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/profile")
public class UserUpdateController {
	
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	private final ReadingListService readingListService;
	private final FinishedListService finishedListService;
	private final BookService bookService;
	
	// common process
	public Map<String, Object> addUserProfileData(Model model) {
		// get username
		SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        
        // set values to userProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);

        Integer userId = userService.getUserIdbyUsername(username);
        UserStatus userStatus = userStatusService.findById(userId);
        userProfile.setReadingNumber(userStatus.getReadingBookNumber());
        userProfile.setFinishedNumber(userStatus.getFinishedBookNumber());

        model.addAttribute("userProfile", userProfile);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("userId", userId);
        return userInfo;
	}
	
	@GetMapping
	public String diplayProfile(Model model) {
		UserProfileInput userProfileInput = new UserProfileInput();
		Map<String, Object> userInfo = addUserProfileData(model);
		Integer userId = (Integer) userInfo.get("userId");
		
		userProfileInput.setUserId(userId);
		model.addAttribute("userProfileInput", userProfileInput);
		
		String username = (String) userInfo.get("username");
		model.addAttribute("username", username);
		
		Optional<String> userEmail = userService.getUserEmailByUsername(username);
		userEmail.ifPresent(email -> {
			model.addAttribute("email", email);
		});
		return "user-profile";
	}
	
	@PostMapping("/edit/{id}")
	public String editProfile(@PathVariable String id,  Model model, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user-profile";
		}
		
		return "redirect:/user/profile";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteAccount(@PathVariable String id) throws NumberFormatException {
		Integer userId = Integer.valueOf(id);
		// Delete related information
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
		
		return "redirect:/custom/logout";
	}
}
