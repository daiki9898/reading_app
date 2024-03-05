package com.example.reading.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.example.reading.dto.BookResult;
import com.example.reading.dto.UserProfile;
import com.example.reading.input.BookInput;
import com.example.reading.input.EditBookInput;
import com.example.reading.input.FinishedEditBookInput;
import com.example.reading.input.SearchInput;
import com.example.reading.model.Book;
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
@RequestMapping("/user/update")
public class BookUpdateController {
	
	private final BookService bookService;
	private final ReadingListService readingListService;
	private final FinishedListService finishedListService;
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	
    // get userid method
	public Integer getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
        Integer userId = userService.getUserIdbyUsername(authentication.getName());
        return userId;
	}
	
	// set values to userprofile
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
	
	@GetMapping("/display-registerform")
	public String displayForm(Model model) {
		BookInput bookInput = new BookInput();
		model.addAttribute("bookInput", bookInput);
		addUserProfileData(model);
		return "registerform";
	}
	
	@GetMapping("/execute-delete/{id}")
	public String executeDelete(@PathVariable String id) throws NumberFormatException {
		Integer bookId = Integer.valueOf(id);
		// 本の削除
		readingListService.deleteByBookId(bookId);
		bookService.delete(bookId);
		// user_statusの更新
        Integer userId = getUserId();
		userStatusService.updateReadingNumber(userId, -1);
		return "redirect:/user/home";
	}
	
	@GetMapping("/execute-delete-finishedbook/{id}")
	public String executeDeleteFinishedBook(@PathVariable String id) throws NumberFormatException {
		Integer bookId = Integer.valueOf(id);
		// 本の削除
		finishedListService.deleteByBookId(bookId);
		bookService.delete(bookId);
		// user_statusの更新
        Integer userId = getUserId();
		userStatusService.updateFinishedNumber(userId, -1);
		return "redirect:/user/finished-booklist";
	}
	
	@PostMapping("/execute-register")
	public String executeRegister(@Validated BookInput bookInput, BindingResult bindingResult, Model model) throws IOException {
		if (bindingResult.hasErrors()) {
			addUserProfileData(model);
			Integer userId = getUserId();
			List<BookResult> bookList = readingListService.findAll(userId);
			model.addAttribute("bookList", bookList);
			SearchInput searchInput = new SearchInput();
			model.addAttribute("searchInput", searchInput);
			return "register-error";
		}
		// 本の登録
        Integer userId = getUserId();
		Book book = bookService.insert(bookInput, userId);
		// 登録情報
		ReadingListRegistration registration = new ReadingListRegistration(userId, book.getBookId(), bookInput.getStartDate());
		readingListService.insert(registration);
		// user_statusの更新
		userStatusService.updateReadingNumber(userId, 1);
		return "redirect:/user/home";
	}
	
	@PostMapping("/execute-edit")
	public String executeEdit(@RequestParam(name = "isFinished", required = false, defaultValue = "no") String isFinished, @Validated EditBookInput editBookInput, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			EditBookInput nowBook = bookService.findById(editBookInput.getBookId());
			model.addAttribute("nowBook", nowBook);
			addUserProfileData(model);
			return "edit-error";
		}
		readingListService.update(editBookInput);
        Integer userId = getUserId();
		bookService.update(editBookInput, userId);
		if (isFinished.equals("yes")) {
			// 読書リストから読了済みへ
			ReadingListRegistration readingListRegistration = readingListService.findByBookId(editBookInput.getBookId());
			finishedListService.toFinishedList(readingListRegistration);
			// user_statusの更新
			userStatusService.updateReadingNumber(userId, -1);
			userStatusService.updateFinishedNumber(userId, 1);
			return "redirect:/user/home";
		}
		return "redirect:/user/book-detail/" + editBookInput.getBookId();
	}
	
	@PostMapping("/execute-edit-finishedbook")
	public String executeEditFinishedBook(@RequestParam(name = "isFinished", required = false, defaultValue = "no") String isFinished, @Validated FinishedEditBookInput finishedEditBookInput, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			FinishedEditBookInput nowBook = bookService.findFinishedBookById(finishedEditBookInput.getBookId());
			model.addAttribute("nowBook", nowBook);
			addUserProfileData(model);
			return "finished-edit-error";
		}
		finishedListService.update(finishedEditBookInput);
		Integer userId = getUserId();
		bookService.updateFinishedBook(finishedEditBookInput, userId);
		if (!(isFinished.equals("yes"))) {
			// 読了済みから読書リストへ
			FinishedListRegistration finishedListRegistration = finishedListService.findByBookId(finishedEditBookInput.getBookId());
			readingListService.toReadingList(finishedListRegistration);
			// user_statusの更新
			userStatusService.updateFinishedNumber(userId, -1);
			userStatusService.updateReadingNumber(userId, 1);
			return "redirect:/user/finished-booklist";
		}
		return "redirect:/user/finished-book-detail/" + finishedEditBookInput.getBookId();
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxSixeException(Model model) {
		return "filesize-error";
	}
}
