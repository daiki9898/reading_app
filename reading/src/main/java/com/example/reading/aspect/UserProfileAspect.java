package com.example.reading.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.reading.dto.UserProfile;
import com.example.reading.model.UserStatus;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class UserProfileAspect {
	
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	
	@Before("execution(* com.example.reading.controller.BookDisplayController.*(..)) && args(model,..)")
	public void addUserProfileData(Model model) {
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
	}
}
