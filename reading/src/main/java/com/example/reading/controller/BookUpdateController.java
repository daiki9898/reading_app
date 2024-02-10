package com.example.reading.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.reading.input.BookInput;
import com.example.reading.input.EditBookInput;
import com.example.reading.input.FinishedEditBookInput;
import com.example.reading.model.Book;
import com.example.reading.model.FinishedListRegistration;
import com.example.reading.model.ReadingListRegistration;
import com.example.reading.service.BookService;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.FinishedListService;
import com.example.reading.service.ReadingListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/update")
public class BookUpdateController {
	private final BookService bookService;
	private final ReadingListService readingListService;
	private final FinishedListService finishedListService;
	private final CustomUserDetailsService userService;
	
	// userIdを取得する関数
	public Integer getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
        Integer userId = userService.getUserIdbyUsername(authentication.getName());
        return userId;
	}
	
	@GetMapping("/display-registerform")
	public String displayForm(Model model) {
		BookInput bookInput = new BookInput();
		model.addAttribute("bookInput", bookInput);
		return "registerform";
	}
	
	@GetMapping("/display-editform")
	public String displayEditForm(@RequestParam String id, Model model) throws NumberFormatException {
		EditBookInput editBookInput = bookService.findById(Integer.valueOf(id));
		model.addAttribute("editBookInput", editBookInput);
		return "editform";
	}
	
	@GetMapping("/display-finished-editform")
	public String displayFinishedEditForm(@RequestParam String id, Model model) throws NumberFormatException {
		FinishedEditBookInput finishedEditBookInput = bookService.findFinishedBookById(Integer.valueOf(id));
		model.addAttribute("finishedEditBookInput", finishedEditBookInput);
		return "finished-editform";
	}
	
	@GetMapping("/execute-delete")
	public String executeDelete(@RequestParam String id) throws NumberFormatException {
		Integer bookId = Integer.valueOf(id);
//		userId&bookIdでなくて良いか確認
		readingListService.deleteByBookId(bookId);
		bookService.delete(bookId);
		return "redirect:/user/home";
	}
	
	@GetMapping("/execute-delete-finishedbook")
	public String executeDeleteFinishedBook(@RequestParam String id) throws NumberFormatException {
		Integer bookId = Integer.valueOf(id);
//		userId&bookIdでなくて良いか確認
		finishedListService.deleteByBookId(bookId);
		bookService.delete(bookId);
		return "redirect:/user/finished-booklist";
	}
	
	@PostMapping("/execute-register")
	public String executeRegister(@Validated BookInput bookInput, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "registerform";
		}
        Integer userId = getUserId();
		Book book = bookService.insert(bookInput, userId);
		ReadingListRegistration registration = new ReadingListRegistration(userId, book.getBookId(), bookInput.getStartDate());
		readingListService.insert(registration);
		return "redirect:/user/home";
	}
	
	@PostMapping("/execute-edit")
	public String executeEdit(@RequestParam(name = "isFinished", required = false, defaultValue = "no") String isFinished, @Validated EditBookInput editBookInput, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "editform";
		}
		readingListService.update(editBookInput);
        Integer userId = getUserId();
		bookService.update(editBookInput, userId);
		if (isFinished.equals("yes")) {
			ReadingListRegistration readingListRegistration = readingListService.findByBookId(editBookInput.getBookId());
			finishedListService.toFinishedList(readingListRegistration);
			return "redirect:/user/home";
		}
		return "redirect:/user/book-detail?id=" + editBookInput.getBookId();
	}
	
	@PostMapping("/execute-edit-finishedbook")
	public String executeEditFinishedBook(@RequestParam(name = "isFinished", required = false, defaultValue = "no") String isFinished, @Validated FinishedEditBookInput finishedEditBookInput, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "finished-editform";
		}
		finishedListService.update(finishedEditBookInput);
		Integer userId = getUserId();
		bookService.updateFinishedBook(finishedEditBookInput, userId);
		if (!(isFinished.equals("yes"))) {
			FinishedListRegistration finishedListRegistration = finishedListService.findByBookId(finishedEditBookInput.getBookId());
			readingListService.toReadingList(finishedListRegistration);
			return "redirect:/user/finished-booklist";
		}
		return "redirect:/user/finished-book-detail?id=" + finishedEditBookInput.getBookId();
	}
}
