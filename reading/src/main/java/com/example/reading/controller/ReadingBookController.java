package com.example.reading.controller;

import java.io.FileNotFoundException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.reading.dto.BookResult;
import com.example.reading.dto.UserProfile;
import com.example.reading.input.BookInput;
import com.example.reading.input.EditBookInput;
import com.example.reading.input.SearchInput;
import com.example.reading.model.Book;
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
@RequestMapping("/user/reading-books")
public class ReadingBookController {
	
	private final BookService bookService;
	private final ReadingListService readingListService;
	private final FinishedListService finishedListService;
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	
	// userIdを返すメソッド
	public Integer getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
        Integer userId = userService.getUserIdbyUsername(authentication.getName());
        return userId;
	}
	
	// UserProfileに値をセットするメソッド
	public void addUserProfileData(Model model) {
		// ユーザー名の取得
		SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        
        // 値をセット
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        
        Integer userId = userService.getUserIdbyUsername(username);
        UserStatus userStatus = userStatusService.findById(userId);
        userProfile.setReadingNumber(userStatus.getReadingBookNumber());
        userProfile.setFinishedNumber(userStatus.getFinishedBookNumber());
        model.addAttribute("userProfile", userProfile);
	}
	
//	表示系
	
	@GetMapping
	public String displayList(Model model,
			@RequestParam(name="order", defaultValue="asc") String order,
			@RequestParam(name="sort", defaultValue="date") String sort,
			@RequestParam(name="search-error", required=false) String searchError) throws IOException {
		List<BookResult> bookList = readingListService.findAll(getUserId());
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
		model.addAttribute("searchInput", new SearchInput());
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
	
	// ジャンル別
	@GetMapping("/{genre:\\D+}")
	public String displayListByGenre(Model model,
			@PathVariable(name= "genre", required = true) String genre,
			@RequestParam(name="order", defaultValue="asc") String order,
			@RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
		List<BookResult> bookList = readingListService.searchBookByGenre(getUserId(), genre);
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
		model.addAttribute("searchInput", new SearchInput());
		BookInput bookInput = new BookInput();
		bookInput.setStartDate(LocalDate.now());
		model.addAttribute("bookInput", bookInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		model.addAttribute("genre", genre);
		return "user/book/reading-booklist";
	}
	
	// 個別
	@GetMapping("/{id:\\d+}")
	public String displayDetail(Model model, @PathVariable Integer id) {
		EditBookInput editBookInput = bookService.findById(id);
		model.addAttribute("editBookInput", editBookInput);
		return "user/book/book-detail";
	}
	
	// 検索
	@PostMapping("/search")
	public String search(Model model, SearchInput searchInput, @RequestParam(name="order", defaultValue="asc") String order, @RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
		// 入力値の取得
		String title = searchInput.getTitle();
		String genre = searchInput.getGenre();
		String author = searchInput.getAuthor();
		YearMonth roughStartDate = searchInput.getRoughStartDate();
		LocalDate specificStartDate = searchInput.getSpecificStartDate();
		// 何も入力されなかった場合
		if (title.isBlank() && genre.isEmpty() && author.isBlank() && roughStartDate == null && specificStartDate == null) {
			return "redirect:/user/reading-books?search-error";
		}
		
		List<BookResult> bookList = readingListService.searchBook(getUserId(), searchInput);
		// sort
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
		model.addAttribute("bookList", bookList);
		model.addAttribute("searchInput", searchInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		return "user/book/search-result";
	}
	
//	更新系
	
	// 登録フォームの表示
	@GetMapping("/register")
	public String displayRegisterForm(Model model) {
		BookInput bookInput = new BookInput();
		bookInput.setStartDate(LocalDate.now());
		model.addAttribute("bookInput", bookInput);
		return "user/book/registerform";
	}
	
	// 本の登録
	@PostMapping("/register")
	public String register(@Validated BookInput bookInput, BindingResult bindingResult, Model model) throws IOException {
		if (bindingResult.hasErrors()) {
			List<BookResult> bookList = readingListService.findAll(getUserId());
			model.addAttribute("bookList", bookList);
			model.addAttribute("searchInput", new SearchInput());
			addUserProfileData(model);
			return "user/book/register-error";
		}
		// 本の登録
        Integer userId = getUserId();
		Book book = bookService.insert(bookInput, getUserId());
		// 登録情報の追加
		ReadingListRegistration registration = new ReadingListRegistration(userId, book.getBookId(), bookInput.getStartDate());
		readingListService.insert(registration);
		// 冊数の更新
		userStatusService.updateReadingNumber(userId, 1);
		return "redirect:/user/reading-books";
	}
	
	@PostMapping("/{id}/edit")
	public String edit(@PathVariable Integer id, @RequestParam(name = "isFinished", required = false, defaultValue = "no") String isFinished, @Validated EditBookInput editBookInput, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			EditBookInput nowBook = bookService.findById(id);
			model.addAttribute("nowBook", nowBook);
			addUserProfileData(model);
			return "user/book/edit-error";
		}
		// 登録情報の更新
		readingListService.update(editBookInput);
        Integer userId = getUserId();
        // 本の更新
		try {
			bookService.update(editBookInput, userId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (isFinished.equals("yes")) {
			// 読書リストへ移動
			ReadingListRegistration readingListRegistration = readingListService.findByBookId(editBookInput.getBookId());
			finishedListService.toFinishedList(readingListRegistration);
			// 冊数の更新
			userStatusService.updateReadingNumber(userId, -1); // 読み終えた本
			userStatusService.updateFinishedNumber(userId, 1); // 読書中の本
			return "redirect:/user/reading-books";
		}
		return "redirect:/user/reading-books/" + editBookInput.getBookId();
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Integer id) {
		// 登録情報の削除
		readingListService.deleteByBookId(id);
		// 本の削除
		try {
			bookService.delete(id);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 冊数の更新
		userStatusService.updateReadingNumber(getUserId(), -1);
		return "redirect:/user/reading-books";
	}
	
}
