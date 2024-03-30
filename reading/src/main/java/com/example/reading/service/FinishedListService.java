package com.example.reading.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.dto.BookResult;
import com.example.reading.dto.FinishedListDto;
import com.example.reading.input.FinishedEditBookInput;
import com.example.reading.input.FinishedSearchInput;
import com.example.reading.model.FinishedListRegistration;
import com.example.reading.model.ReadingListRegistration;
import com.example.reading.repository.FinishedRegistrationRepository;
import com.example.reading.repository.ListRepository;
import com.example.reading.repository.ReadingRegistrationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FinishedListService {
	private final FinishedRegistrationRepository finishedRepository;
	private final ReadingRegistrationRepository readingRepository;
	private final ListRepository listRepository;
	
	@Value("${image.folder}")
	private String imgFolder;
	
	@Value("${default.pic}")
	private String defaultPath;
	
	// 読了済みリスト
	public List<BookResult> findAll(Integer userId) throws IOException {
		List<FinishedListDto> finishedBookList =  listRepository.getFinishedListByUserId(userId);
		List<BookResult> resultList = new ArrayList<BookResult>();
		for (FinishedListDto book : finishedBookList) {
			BookResult result = new BookResult();
			result.setBookId(book.getBookId());
			result.setTitle(book.getBookInfoDto().getTitle());
			result.setStartDate(book.getStartDate());
			result.setEndDate(book.getEndDate());
			// 画像のエンコード
			Path filePath = Paths.get(book.getBookInfoDto().getImgSrc());
			byte[] bytes = Files.readAllBytes(filePath);
			String textData = Base64.getEncoder().encodeToString(bytes);
			result.setImgFile("data:image/jpg;base64," + textData);
			resultList.add(result);
		}
		return resultList;
	}
	
	public List<BookResult> searchBook(Integer userId, FinishedSearchInput finishedSearchInput) throws IOException {
		List<FinishedListDto> finishedBookList =  listRepository.searchFinishedBook(userId, finishedSearchInput.getTitle(), finishedSearchInput.getGenre(), finishedSearchInput.getAuthor(), finishedSearchInput.getRoughStartDate(), finishedSearchInput.getRoughEndDate(), finishedSearchInput.getSpecificEndDate());
		List<BookResult> resultList = new ArrayList<BookResult>();
		for (FinishedListDto book : finishedBookList) {
			BookResult result = new BookResult();
			result.setBookId(book.getBookId());
			result.setTitle(book.getBookInfoDto().getTitle());
			result.setStartDate(book.getStartDate());
			result.setEndDate(book.getEndDate());
			// 画像のエンコード
			Path filePath = Paths.get(book.getBookInfoDto().getImgSrc());
			byte[] bytes = Files.readAllBytes(filePath);
			String textData = Base64.getEncoder().encodeToString(bytes);
			result.setImgFile("data:image/jpg;base64," + textData);
			resultList.add(result);
		}
		return resultList;
	}
	
	public List<BookResult> searchBookByGenre(Integer userId, String genre) throws IOException {
		List<FinishedListDto> finishedBookList =  listRepository.searchFinishedBookByGenre(userId, genre);
		List<BookResult> resultList = new ArrayList<BookResult>();
		for (FinishedListDto book : finishedBookList) {
			BookResult result = new BookResult();
			result.setBookId(book.getBookId());
			result.setTitle(book.getBookInfoDto().getTitle());
			result.setStartDate(book.getStartDate());
			result.setEndDate(book.getEndDate());
			// 画像のエンコード
			Path filePath = Paths.get(book.getBookInfoDto().getImgSrc());
			byte[] bytes = Files.readAllBytes(filePath);
			String textData = Base64.getEncoder().encodeToString(bytes);
			result.setImgFile("data:image/jpg;base64," + textData);
			resultList.add(result);
		}
		return resultList;
	}
	
	// 登録情報のCRUD
	public FinishedListRegistration findByBookId(Integer bookId) {
		FinishedListRegistration registration  = finishedRepository.findByBookId(bookId).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		return registration;
	}
	
	public void insert(FinishedListRegistration registration) {
		finishedRepository.save(registration);
	}
	
	public void update(FinishedEditBookInput finishedEditBookInput) {
		FinishedListRegistration registration = finishedRepository.findByBookId(finishedEditBookInput.getBookId()).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		registration.setStartDate(finishedEditBookInput.getStartDate());
		registration.setEndDate(finishedEditBookInput.getEndDate());
		finishedRepository.save(registration);
	}
	
	public void delete(Integer id) {
		finishedRepository.deleteById(id);
	}
	
	public List<FinishedListRegistration> deleteByUserId(Integer userId) {
		List<FinishedListRegistration> registrations = finishedRepository.getListByUserId(userId).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		readingRepository.deleteByUserId(userId);
		return registrations;
	}
	
	public void deleteByBookId(Integer bookId) {
		finishedRepository.deleteByBookId(bookId);
	}
	
	public void toFinishedList(ReadingListRegistration readingListRegistration) {
		readingRepository.deleteById(readingListRegistration.getId());
		finishedRepository.save(new FinishedListRegistration(readingListRegistration.getUserId(), readingListRegistration.getBookId(), readingListRegistration.getStartDate(), LocalDate.now()));
	}

}
