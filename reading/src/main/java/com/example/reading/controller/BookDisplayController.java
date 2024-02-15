package com.example.reading.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.reading.dto.BookResult;
import com.example.reading.input.BookInput;
import com.example.reading.input.EditBookInput;
import com.example.reading.input.FinishedEditBookInput;
import com.example.reading.input.FinishedSearchInput;
import com.example.reading.input.SearchInput;
import com.example.reading.service.BookService;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.FinishedListService;
import com.example.reading.service.ReadingListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class BookDisplayController {
	
	private final CustomUserDetailsService userService;
	private final FinishedListService finishedListService;
	private final BookService bookService;
	private final ReadingListService readingListService;
	
	// get userid method
	public Integer getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
        Integer userId = userService.getUserIdbyUsername(authentication.getName());
        return userId;
	}
	
	@GetMapping("/home")
	public String displayList(Model model) throws IOException {
		Integer userId = getUserId();
		List<BookResult> bookList = readingListService.findAll(userId);
		model.addAttribute("bookList", bookList);
		SearchInput searchInput = new SearchInput();
		model.addAttribute("searchInput", searchInput);
		BookInput bookInput = new BookInput();
		model.addAttribute("bookInput", bookInput);
		return "reading-booklist";
	}
	
	// タグ検索
	@GetMapping("/search-reading-booklist")
	public String searchBookByGenre(Model model, @RequestParam(name= "genre", required = true) String genre) throws IOException {
		List<BookResult> bookList = readingListService.searchBookByGenre(getUserId(), genre);
		model.addAttribute("bookList", bookList);
		SearchInput searchInput = new SearchInput();
		model.addAttribute("searchInput", searchInput);
		BookInput bookInput = new BookInput();
		model.addAttribute("bookInput", bookInput);
		return "reading-booklist";
	}
	
	@GetMapping("/book-detail/{id}")
	public String displayDetail(Model model, @PathVariable String id) throws NumberFormatException {
		EditBookInput editBookInput = bookService.findById(Integer.valueOf(id));
		model.addAttribute("editBookInput", editBookInput);
		return "book-detail";
	}
	
	@GetMapping("/finished-booklist")
	public String displayFinishedList(Model model) throws IOException {
		Integer userId = getUserId();
		List<BookResult> finishedBookList = finishedListService.findAll(userId);
		model.addAttribute("finishedBookList", finishedBookList);
		FinishedSearchInput finishedSearchInput = new FinishedSearchInput();
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		return "finished-booklist";
	}
	
	// タグ検索
	@GetMapping("/search-finished-booklist")
	public String searchFinishedBookByGenre(Model model, @RequestParam(name= "genre", required = true) String genre) throws IOException {
		List<BookResult> finishedBookList = finishedListService.searchBook(getUserId(), genre);
		model.addAttribute("finishedBookList", finishedBookList);
		FinishedSearchInput finishedSearchInput = new FinishedSearchInput();
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		return "finished-booklist";
	}
	
		
	@GetMapping("/finished-book-detail/{id}")
	public String displayFinishedDetail(Model model, @PathVariable String id) throws NumberFormatException {
		FinishedEditBookInput finishedEditBookInput = bookService.findFinishedBookById(Integer.valueOf(id));
		model.addAttribute("finishedEditBookInput", finishedEditBookInput);
		return "finished-book-detail";
	}
	
	
	// 読書リスト検索関係
	@PostMapping("/search-reading-booklist")
	public String searchBook(Model model, SearchInput searchInput) throws IOException {
		if (searchInput == null) {
			return "redirect:/user/home";
		}
		String title = searchInput.getTitle();
		String genre = searchInput.getGenre();
		String author = searchInput.getAuthor();
		YearMonth roughStartDate = searchInput.getRoughStartDate();
		LocalDate specificStartDate = searchInput.getSpecificStartDate();
		if (title.isBlank() && genre.isEmpty() && author.isBlank() && roughStartDate == null && specificStartDate == null) {
			return "redirect:/user/search-reading-booklist-error";
		}
		Integer userId = getUserId();
		List<BookResult> searchBookList = readingListService.searchBook(userId, searchInput);
		model.addAttribute("searchBookList", searchBookList);
		model.addAttribute("searchInput", searchInput);
		return "search/search-result";
	}
	
	@GetMapping("/search-reading-booklist-error")
	public String showSearchError(Model model) throws IOException {
		Integer userId = getUserId();
		List<BookResult> bookList = readingListService.findAll(userId);
		model.addAttribute("bookList", bookList);
		SearchInput searchInput = new SearchInput();
		model.addAttribute("searchInput", searchInput);
		BookInput bookInput = new BookInput();
		model.addAttribute("bookInput", bookInput);
		return "search/search-result-error";
	}
	
    // 本棚(読了済みリスト検索関係)
	@PostMapping("/search-finished-booklist")
	public String searchFinishedBook(Model model, FinishedSearchInput finishedSearchInput) throws IOException {
		if (finishedSearchInput == null) {
			return "redirect:/user/finished-booklist";
		}
		String title = finishedSearchInput.getTitle();
		String genre = finishedSearchInput.getGenre();
		String author = finishedSearchInput.getAuthor();
		YearMonth roughStartDate = finishedSearchInput.getRoughStartDate();
		YearMonth roughEndDate = finishedSearchInput.getRoughEndDate();
		LocalDate specificEndDate = finishedSearchInput.getSpecificEndDate();
		if (title.isBlank() && genre.isEmpty() && author.isBlank() && roughStartDate == null && roughEndDate == null && specificEndDate == null) {
			return "redirect:/user/search-finished-booklist-error";
		}
		Integer userId = getUserId();
		List<BookResult> finishedSearchBookList = finishedListService.searchBook(userId, finishedSearchInput);
		model.addAttribute("finishedSearchBookList", finishedSearchBookList);
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		return "search/finished-search-result";
	}
	
	@GetMapping("/search-finished-booklist-error")
	public String showFinishedSearchError(Model model) throws IOException {
		Integer userId = getUserId();
		List<BookResult> finishedBookList = finishedListService.findAll(userId);
		model.addAttribute("finishedBookList", finishedBookList);
		FinishedSearchInput finishedSearchInput = new FinishedSearchInput();
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		return "search/finished-search-result-error";
	}
}
