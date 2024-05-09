package com.example.reading.controller;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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
import com.example.reading.service.EmailSenderService;
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
	private final EmailSenderService emailSenderService;
	
	@Value("${base.url}")
	private String url; // URL
	
	// userIdを返すメソッド
	public Integer getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
        Integer userId = userService.getUserIdbyUsername(authentication.getName());
        return userId;
	}
	
	// usernameを返すメソッド
	public String getUsername() {
		SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getName();
	}
	
	// 共通処理
	public Map<String, Object> addUserProfileData(Model model) {
        String username = getUsername();
        
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
		return "user/authentication/user-profile/user-profile";
	}
	
	@PostMapping("/update-username")
	public ModelAndView updateUsernmae(@Validated UsernameInput usernameInput, BindingResult bindingResult, Model model, HttpServletRequest request) {
		Integer userId = getUserId();
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
			
			return new ModelAndView("user/authentication/user-profile/username-error");
		}
		userService.updateUsernameById(usernameInput.getUsername(), userId);
		request.setAttribute(
			      View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
		
		return new ModelAndView("redirect:/username/logout");
	}
	
	@PostMapping("/update-email")
	public String updateEmail(@Validated UserEmailInput emailInput, BindingResult bindingResult, Model model) {
		Integer userId = getUserId();
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
			
			return "user/authentication/user-profile/email-error";
		}
		String email = emailInput.getEmailAddress();
		userService.updateEmailById(email, userId);
		String subject = "メールアドレス連携完了";
		String body = "この度はメールアドレスのアカウント連携を行っていただき、ありがとうございます。<br>"
				+ "お客様のメールアドレスとの連携が正常に完了いたしました。<br>"
				+ "<br>"
				+ "もしパスワードを忘れてしまった場合は、本メールアドレスにリセットリンクを送信いたします。<br>"
				+ "リセットリンクの送信はログイン画面、または下記リンクから行ってください。<br>"
				+ "<a href='"+ url + "/password-reset-link'>" + url + "/password-reset-link</a><br>"
				+ "<br>"
				+ "何かご不明な点やお困りのことがございましたら、下記の連絡先までお気軽にお問い合わせください。<br>"
				+ "今後ともどうぞよろしくお願いいたします。<br>";
		emailSenderService.sendEmail(email, subject, getUsername(), body);
		return "redirect:/user/settings";
	}
	
	@PostMapping("/update-status")
	public String editProfile(@Validated UserOptionInput optionInput, BindingResult bindingResult, Model model) {
		Integer userId = getUserId();
		if (bindingResult.hasErrors()) {
			addUserProfileData(model);
			Optional<String> userEmail = userService.getUserEmailById(userId);
			userEmail.ifPresent(email -> {
				model.addAttribute("email", email);
			});
			model.addAttribute("usernameInput", new UsernameInput());
			model.addAttribute("userEmailInput", new UserEmailInput());
			
			return "user/authentication/user-profile/user-profile";
		}
		if (optionInput.getGenreTagStatus() == null) {
			userStatusService.updateGenreTagStatus(false, userId);
		} else if (optionInput.getGenreTagStatus().equals("open")){
			userStatusService.updateGenreTagStatus(true, userId);
		}
		return "redirect:/user/settings";
	}
	
	@PostMapping("/delete-email")
	public String deleteEmail() {
		Integer userId = getUserId();
		userService.updateEmailById(null, userId);
		return "redirect:/user/settings";
	}
	
	@PostMapping("/delete-account")
	public ModelAndView deleteAccount(HttpServletRequest request) {
		Integer userId = getUserId();
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
