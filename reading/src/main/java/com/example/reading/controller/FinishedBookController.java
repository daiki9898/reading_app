package com.example.reading.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import com.example.reading.input.FinishedEditBookInput;
import com.example.reading.input.FinishedSearchInput;
import com.example.reading.model.FinishedListRegistration;
import com.example.reading.model.UserStatus;
import com.example.reading.service.BookService;
import com.example.reading.service.CustomUserDetailsService;
import com.example.reading.service.FinishedListService;
import com.example.reading.service.ReadingListService;
import com.example.reading.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/finished-books")
public class FinishedBookController {
	
	private final BookService bookService;
	private final FinishedListService finishedListService;
	private final ReadingListService readingListService;
	private final CustomUserDetailsService userService;
	private final UserStatusService userStatusService;
	
	@Value("${page.size}")
	private int pageSize; // 1ページあたりの冊数
	
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
			@RequestParam(name="page", defaultValue="1") int page,
			@RequestParam(name="order", defaultValue="asc") String order,
			@RequestParam(name="sort", defaultValue="date") String sort,
			@RequestParam(name="search-error", required=false) String searchError) throws IOException {
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
		// pagenation
	    int totalBooks = finishedBookList.size();
	    int fromIndex = (page - 1) * pageSize;  // ページ開始インデックス
	    int toIndex = Math.min(fromIndex + pageSize, totalBooks);  // ページ終了インデックス（リストのサイズを超えないように）
	    List<BookResult> pageContent = finishedBookList.subList(fromIndex, toIndex); // fromIndexから始まり、toIndexの前まで抜き取る

		model.addAttribute("finishedBookList", pageContent);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", (totalBooks + pageSize - 1) / pageSize); // 切り上げ除算
		model.addAttribute("finishedSearchInput", new FinishedSearchInput());
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		if (searchError != null) {
			model.addAttribute("searchError", searchError);
		}
		return "user/finished-book/finished-booklist";
	}
	
	// ジャンル別
	@GetMapping("/{genre:\\D+}")
	public String displayListByGenre(Model model,
			@RequestParam(name="page", defaultValue="1") int page,
			@PathVariable(name= "genre", required = true) String genre,
			@RequestParam(name="order", defaultValue="asc") String order,
			@RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
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
		// pagenation
	    int totalBooks = finishedBookList.size();
	    int fromIndex = (page - 1) * pageSize;  // ページ開始インデックス
	    int toIndex = Math.min(fromIndex + pageSize, totalBooks);  // ページ終了インデックス（リストのサイズを超えないように）
	    List<BookResult> pageContent = finishedBookList.subList(fromIndex, toIndex); // fromIndexから始まり、toIndexの前まで抜き取る

		model.addAttribute("finishedBookList", pageContent);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", (totalBooks + pageSize - 1) / pageSize); // 切り上げ除算
		model.addAttribute("finishedSearchInput", new FinishedSearchInput());
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		model.addAttribute("genre", genre);
		return "user/finished-book/finished-booklist";
	}
	
	// 個別
	@GetMapping("/{id:\\d+}")
	public String displayDetail(Model model, @PathVariable Integer id) {
		FinishedEditBookInput finishedEditBookInput = bookService.findFinishedBookById(id);
		model.addAttribute("finishedEditBookInput", finishedEditBookInput);
		return "user/finished-book/finished-book-detail";
	}
	
    // 検索
	@PostMapping("/search")
	public String search(Model model, FinishedSearchInput finishedSearchInput,
			@RequestParam(name="page", defaultValue="1") int page,
			@RequestParam(name="order", defaultValue="asc") String order,
			@RequestParam(name="sort", defaultValue="date") String sort) throws IOException {
		// 入力値の取得
		String title = finishedSearchInput.getTitle();
		String genre = finishedSearchInput.getGenre();
		String author = finishedSearchInput.getAuthor();
		YearMonth roughStartDate = finishedSearchInput.getRoughStartDate();
		YearMonth roughEndDate = finishedSearchInput.getRoughEndDate();
		LocalDate specificEndDate = finishedSearchInput.getSpecificEndDate();
		// 何も入力されなかった場合
		if (title.isBlank() && genre.isEmpty() && author.isBlank() && roughStartDate == null && roughEndDate == null && specificEndDate == null) {
			return "redirect:/user/finished-books?search-error";
		}
		
		List<BookResult> finishedBookList = finishedListService.searchBook(getUserId(), finishedSearchInput);
		// sort
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
		// pagenation
	    int totalBooks = finishedBookList.size();
	    int fromIndex = (page - 1) * pageSize;  // ページ開始インデックス
	    int toIndex = Math.min(fromIndex + pageSize, totalBooks);  // ページ終了インデックス（リストのサイズを超えないように）
	    List<BookResult> pageContent = finishedBookList.subList(fromIndex, toIndex); // fromIndexから始まり、toIndexの前まで抜き取る

		model.addAttribute("finishedBookList", pageContent);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", (totalBooks + pageSize - 1) / pageSize); // 切り上げ除算
		model.addAttribute("finishedSearchInput", finishedSearchInput);
		model.addAttribute("order", order);
		model.addAttribute("sort", sort);
		return "user/finished-book/finished-search-result";
	}
	
//	更新系処理
	
	@PostMapping("/{id}/edit")
	public String edit(@PathVariable Integer id, @RequestParam(name = "isFinished", required = false, defaultValue = "no") String isFinished, @Validated FinishedEditBookInput finishedEditBookInput, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			FinishedEditBookInput nowBook = bookService.findFinishedBookById(id);
			model.addAttribute("nowBook", nowBook);
			addUserProfileData(model);
			return "/user/finished-book/finished-edit-error";
		}
		// 登録情報の更新
		finishedListService.update(finishedEditBookInput);
		Integer userId = getUserId();
		// 本の更新
		try {
			bookService.updateFinishedBook(finishedEditBookInput, userId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (!isFinished.equals("yes")) {
			// 読了済みリストへ移動
			FinishedListRegistration finishedListRegistration = finishedListService.findByBookId(finishedEditBookInput.getBookId());
			readingListService.toReadingList(finishedListRegistration);
			// 冊数の更新
			userStatusService.updateFinishedNumber(userId, -1); // 読み終えた本
			userStatusService.updateReadingNumber(userId, 1); // 読書中の本
			return "redirect:/user/finished-books";
		}
		return "redirect:/user/finished-books/" + finishedEditBookInput.getBookId();
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Integer id) {
		// 登録情報の削除
		finishedListService.deleteByBookId(id);
		// 本の削除
		try {
			bookService.delete(id);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 冊数の更新
		userStatusService.updateFinishedNumber(getUserId(), -1);
		return "redirect:/user/finished-books";
	}

}
