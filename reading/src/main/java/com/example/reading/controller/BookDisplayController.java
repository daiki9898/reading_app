package com.example.reading.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
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
	
	// UserIdを返すメソッド
	public Integer getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
        Integer userId = userService.getUserIdbyUsername(authentication.getName());
        return userId;
	}
	
//	読書リスト関係
	@GetMapping("/home")
	public String displayList(Model model, @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort, @RequestParam(name="search-error", required=false) String searchError) throws IOException {
		Integer userId = getUserId();
		List<BookResult> bookList = readingListService.findAll(userId);
		
		// sort
		if (bookList != null) {
			if (sort.equals("date")) {
				if (order.equals("asc")) {
					bookList.sort(Comparator.comparing(BookResult::getStartDate));
				} else if (order.equals("desc")) {
					bookList.sort(Comparator.comparing(BookResult::getStartDate).reversed());
				}
			} else if (sort.equals("title")) {
				if (order.equals("asc")) {
					bookList.sort(Comparator.comparing(BookResult::getTitle));
				} else if (order.equals("desc")) {
					bookList.sort(Comparator.comparing(BookResult::getTitle).reversed());
				}
			}
		}
		model.addAttribute("bookList", bookList);
		SearchInput searchInput = new SearchInput();
		model.addAttribute("searchInput", searchInput);
		BookInput bookInput = new BookInput();
		bookInput.setStartDate(LocalDate.now());
		model.addAttribute("bookInput", bookInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		if (searchError != null) {
			model.addAttribute("searchError", searchError);
		}
		return "user/book/reading-booklist";
	}
	
	// by genre
	@GetMapping("/home/{genre}")
	public String displayListByGenre(Model model, @PathVariable(name= "genre", required = true) String genre, @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
		Integer userId = getUserId();
		List<BookResult> bookList = readingListService.searchBookByGenre(userId, genre);
		
		// sort
		if (bookList != null) {
			if (sort.equals("date")) {
				if (order.equals("asc")) {
					bookList.sort(Comparator.comparing(BookResult::getStartDate));
				} else if (order.equals("desc")) {
					bookList.sort(Comparator.comparing(BookResult::getStartDate).reversed());
				}
			} else if (sort.equals("title")) {
				if (order.equals("asc")) {
					bookList.sort(Comparator.comparing(BookResult::getTitle));
				} else if (order.equals("desc")) {
					bookList.sort(Comparator.comparing(BookResult::getTitle).reversed());
				}
			}
		}
		model.addAttribute("bookList", bookList);
		SearchInput searchInput = new SearchInput();
		model.addAttribute("searchInput", searchInput);
		BookInput bookInput = new BookInput();
		bookInput.setStartDate(LocalDate.now());
		model.addAttribute("bookInput", bookInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		model.addAttribute("genre", genre);
		return "user/book/reading-booklist";
	}
	
	@GetMapping("/book-detail/{id}")
	public String displayDetail(Model model, @PathVariable String id) throws NumberFormatException {
		EditBookInput editBookInput = bookService.findById(Integer.valueOf(id));
		model.addAttribute("editBookInput", editBookInput);
		return "user/book/book-detail";
	}
	
	// 検索
	@PostMapping("/search-reading-booklist")
	public String searchBook(Model model, SearchInput searchInput, @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
//		if (searchInput == null) {
//			return "redirect:/user/home";
//		}
		String title = searchInput.getTitle();
		String genre = searchInput.getGenre();
		String author = searchInput.getAuthor();
		YearMonth roughStartDate = searchInput.getRoughStartDate();
		LocalDate specificStartDate = searchInput.getSpecificStartDate();
		if (title.isBlank() && genre.isEmpty() && author.isBlank() && roughStartDate == null && specificStartDate == null) {
			return "redirect:/user/home?search-error";
		}
		
		Integer userId = getUserId();
		List<BookResult> searchBookList = readingListService.searchBook(userId, searchInput);
		if (sort.equals("date")) {
			if (order.equals("asc")) {
				searchBookList.sort(Comparator.comparing(BookResult::getStartDate));
			} else if (order.equals("desc")) {
				searchBookList.sort(Comparator.comparing(BookResult::getStartDate).reversed());
			}
		} else if (sort.equals("title")) {
			if (order.equals("asc")) {
				searchBookList.sort(Comparator.comparing(BookResult::getTitle));
			} else if (order.equals("desc")) {
				searchBookList.sort(Comparator.comparing(BookResult::getTitle).reversed());
			}
		}
		model.addAttribute("searchBookList", searchBookList);
		model.addAttribute("searchInput", searchInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		return "user/book/search-result";
	}
	
//	読了済みリスト関係
	@GetMapping("/shelf")
	public String displayFinishedList(Model model, @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort, @RequestParam(name="search-error", required=false) String searchError) throws IOException {
		List<BookResult> finishedBookList = finishedListService.findAll(getUserId());
		// sort
		if (finishedBookList != null) {
			if (sort.equals("date")) {
				if (order.equals("asc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getEndDate));
				} else if (order.equals("desc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getEndDate).reversed());
				}
			} else if (sort.equals("title")) {
				if (order.equals("asc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getTitle));
				} else if (order.equals("desc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getTitle).reversed());
				}
			}
		}
		model.addAttribute("finishedBookList", finishedBookList);
		FinishedSearchInput finishedSearchInput = new FinishedSearchInput();
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		if (searchError != null) {
			model.addAttribute("searchError", searchError);
		}
		return "user/finished-book/finished-booklist";
	}
	
	// by genre
	@GetMapping("/shelf/{genre}")
	public String displayFinishedListByGenre(Model model, @PathVariable(name= "genre", required = true) String genre, @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
		List<BookResult> finishedBookList = finishedListService.searchBookByGenre(getUserId(), genre);
		// sort
		if (finishedBookList != null) {
			if (sort.equals("date")) {
				if (order.equals("asc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getStartDate));
				} else if (order.equals("desc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getStartDate).reversed());
				}
			} else if (sort.equals("title")) {
				if (order.equals("asc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getTitle));
				} else if (order.equals("desc")) {
					finishedBookList.sort(Comparator.comparing(BookResult::getTitle).reversed());
				}
			}
		}
		model.addAttribute("finishedBookList", finishedBookList);
		model.addAttribute("finishedSearchInput", new FinishedSearchInput());
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		model.addAttribute("genre", genre);
		return "user/finished-book/finished-booklist";
	}
	
	@GetMapping("/finished-book-detail/{id}")
	public String displayFinishedDetail(Model model, @PathVariable String id) throws NumberFormatException {
		FinishedEditBookInput finishedEditBookInput = bookService.findFinishedBookById(Integer.valueOf(id));
		model.addAttribute("finishedEditBookInput", finishedEditBookInput);
		return "user/finished-book/finished-book-detail";
	}
	
    // 検索
	@PostMapping("/search-finished-booklist")
	public String searchFinishedBook(Model model, FinishedSearchInput finishedSearchInput,  @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
//		if (finishedSearchInput == null) {
//			return "redirect:/user/shelf";
//		}
		String title = finishedSearchInput.getTitle();
		String genre = finishedSearchInput.getGenre();
		String author = finishedSearchInput.getAuthor();
		YearMonth roughStartDate = finishedSearchInput.getRoughStartDate();
		YearMonth roughEndDate = finishedSearchInput.getRoughEndDate();
		LocalDate specificEndDate = finishedSearchInput.getSpecificEndDate();
		if (title.isBlank() && genre.isEmpty() && author.isBlank() && roughStartDate == null && roughEndDate == null && specificEndDate == null) {
			return "redirect:/user/shelf?search-error";
		}
		
		List<BookResult> finishedBookList = finishedListService.searchBook(getUserId(), finishedSearchInput);
		if (sort.equals("date")) {
			if (order.equals("asc")) {
				finishedBookList.sort(Comparator.comparing(BookResult::getEndDate));
			} else if (order.equals("desc")) {
				finishedBookList.sort(Comparator.comparing(BookResult::getEndDate).reversed());
			}
		} else if (sort.equals("title")) {
			if (order.equals("asc")) {
				finishedBookList.sort(Comparator.comparing(BookResult::getTitle));
			} else if (order.equals("desc")) {
				finishedBookList.sort(Comparator.comparing(BookResult::getTitle).reversed());
			}
		}
		model.addAttribute("finishedBookList", finishedBookList);
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		return "user/finished-book/finished-search-result";
	}
}
