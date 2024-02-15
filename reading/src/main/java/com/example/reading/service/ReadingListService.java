package com.example.reading.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.dto.BookResult;
import com.example.reading.dto.ReadingListDto;
import com.example.reading.input.EditBookInput;
import com.example.reading.input.SearchInput;
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
public class ReadingListService {
	
	private final ReadingRegistrationRepository readingRepository;
	private final FinishedRegistrationRepository finishedRepository;
	private final ListRepository listRepository;
	
	// display Booklist
	public List<BookResult> findAll(Integer userId) throws IOException {
		List<ReadingListDto> readingBookList =  listRepository.getReadingListByUserId(userId);
		List<BookResult> resultList = new ArrayList<BookResult>();
		for (ReadingListDto book : readingBookList) {
			BookResult result = new BookResult();
			result.setBookId(book.getBookId());
			result.setTitle(book.getBookInfoDto().getTitle());
			result.setStartDate(book.getStartDate());
			// 画像のエンコード
			Path filePath = Paths.get(book.getBookInfoDto().getImgSrc());
			byte[] bytes = Files.readAllBytes(filePath);
			String textData = Base64.getEncoder().encodeToString(bytes);
			result.setImgFile("data:image/jpg;base64," + textData);
			resultList.add(result);
		}
		return resultList;
	}
	
	public List<BookResult> searchBook(Integer userId, SearchInput searchInput) throws IOException {
		List<ReadingListDto> readingBookList =  listRepository.searchBook(userId, searchInput.getTitle(), searchInput.getGenre(), searchInput.getAuthor(), searchInput.getRoughStartDate(), searchInput.getSpecificStartDate());
		List<BookResult> resultList = new ArrayList<BookResult>();
		for (ReadingListDto book : readingBookList) {
			BookResult result = new BookResult();
			result.setBookId(book.getBookId());
			result.setTitle(book.getBookInfoDto().getTitle());
			result.setStartDate(book.getStartDate());
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
		List<ReadingListDto> readingBookList = listRepository.searchBookByGenre(userId, genre);
		List<BookResult> resultList = new ArrayList<BookResult>();
		for (ReadingListDto book : readingBookList) {
			BookResult result = new BookResult();
			result.setBookId(book.getBookId());
			result.setTitle(book.getBookInfoDto().getTitle());
			result.setStartDate(book.getStartDate());
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
	public ReadingListRegistration findByBookId(Integer bookId) {
		ReadingListRegistration readingListRegistration  = readingRepository.findByBookId(bookId).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		return readingListRegistration;
	}
	
	public void insert(ReadingListRegistration registration) {
		readingRepository.save(registration);
	}
	
	public void update(EditBookInput editBookInput) {
		ReadingListRegistration registration  = readingRepository.findByBookId(editBookInput.getBookId()).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		registration.setStartDate(editBookInput.getStartDate());
		readingRepository.save(registration);
	}
	
	public void delete(Integer id) {
		readingRepository.deleteById(id);
	}
	
	public void deleteByBookId(Integer bookId) {
		readingRepository.deleteByBookId(bookId);
	}
	
	public void toReadingList(FinishedListRegistration finishedListRegistration) {
		finishedRepository.deleteById(finishedListRegistration.getId());
		readingRepository.save(new ReadingListRegistration(finishedListRegistration.getUserId(), finishedListRegistration.getBookId(), finishedListRegistration.getStartDate()));
	}
}
