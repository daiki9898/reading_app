package com.example.reading.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.reading.input.BookInput;
import com.example.reading.input.EditBookInput;
import com.example.reading.input.FinishedEditBookInput;
import com.example.reading.model.Book;
import com.example.reading.model.FinishedListRegistration;
import com.example.reading.model.ReadingListRegistration;
import com.example.reading.repository.BookRepository;
import com.example.reading.repository.FinishedRegistrationRepository;
import com.example.reading.repository.ReadingRegistrationRepository;
import com.example.reading.repository.UserStatusRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
	
	private final BookRepository bookRepository;
	private final ReadingRegistrationRepository readingRepository;
	private final FinishedRegistrationRepository finishedRepository;
	private final UserStatusRepository userStatusRepository;
	
	@Value("${image.folder}")
	private String imgFolder;
	
	@Value("${default.pic}")
	private String defaultPath;
	
    // Method to hash userid
	public String caluculateHash(Integer userId) {
		String id = userId.toString();
		String hexString = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] shaBytes = messageDigest.digest(id.getBytes());
			HexFormat hex = HexFormat.of().withLowerCase();
			hexString = hex.formatHex(shaBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hexString;
	}
	
	public EditBookInput findById(Integer bookId) {
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("本が見つかりませんでした"));
		EditBookInput editBookInput = new EditBookInput();
		editBookInput.setBookId(book.getBookId());
		editBookInput.setTitle(book.getTitle());
		editBookInput.setAuthor(book.getAuthor());
		editBookInput.setGenre(book.getGenre());
		editBookInput.setComment(book.getComment());
		ReadingListRegistration registration = readingRepository.findByBookId(bookId).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		editBookInput.setStartDate(registration.getStartDate());
		return editBookInput;
	}
	
	public FinishedEditBookInput findFinishedBookById(Integer bookId) {
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("本が見つかりませんでした"));
		FinishedEditBookInput finishedEditBookInput = new FinishedEditBookInput();
		finishedEditBookInput.setBookId(book.getBookId());
		finishedEditBookInput.setTitle(book.getTitle());
		finishedEditBookInput.setAuthor(book.getAuthor());
		finishedEditBookInput.setGenre(book.getGenre());
		finishedEditBookInput.setComment(book.getComment());
		FinishedListRegistration registration  = finishedRepository.findByBookId(bookId).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		finishedEditBookInput.setStartDate(registration.getStartDate());
		finishedEditBookInput.setEndDate(registration.getEndDate());
		return finishedEditBookInput;
	}
	
	public Book insert(BookInput bookInput, Integer userId) {
		Book book = new Book();
		book.setTitle(bookInput.getTitle());
		book.setAuthor(bookInput.getAuthor());
		book.setGenre(bookInput.getGenre());
		book.setComment(bookInput.getComment());
		if (!(bookInput.getImgFile().isEmpty())) {
			// update user_status
			MultipartFile imgFile = bookInput.getImgFile();
			userStatusRepository.updateImageRegistrationStatus(userId, 1, imgFile.getSize());
			// create derectory for user's image storage
			String fileDirectory = imgFolder + caluculateHash(userId);
			try {
	            Files.createDirectories(Paths.get(fileDirectory));
	        } catch (IOException e) {
	            throw new RuntimeException("ディレクトリの作成に失敗しました", e);
	        }
			String fileName = bookInput.getTitle() + "_" + UUID.randomUUID().toString() + ".jpg";
			String fullPath =  fileDirectory + "/" + fileName;
			book.setImgSrc(fullPath);
			// save image
			Path filePath = Paths.get(fullPath);
			try (OutputStream stream = Files.newOutputStream(filePath)) {
				byte[] bytes = imgFile.getBytes();
				stream.write(bytes);
			} catch (IOException e) {
				throw new RuntimeException("画像の保存に失敗しました", e);
			}
		} else {
			book.setImgSrc(defaultPath);
		}
		bookRepository.save(book);
		return book;
	}
	
	public void update(EditBookInput editBookInput, Integer userId) throws FileNotFoundException {
		Book nowBook = bookRepository.findById(editBookInput.getBookId()).orElseThrow(() -> new EntityNotFoundException(editBookInput.getTitle() + "was not found"));
		Book book = new Book();
		book.setBookId(editBookInput.getBookId());
		book.setTitle(editBookInput.getTitle());
		book.setAuthor(editBookInput.getAuthor());
		book.setGenre(editBookInput.getGenre());
		book.setComment(editBookInput.getComment());
		if (!(editBookInput.getImgFile().isEmpty())) {
			long fileSize = 0;
			if (!(nowBook.getImgSrc().equals(defaultPath))) {
				File file = new File(nowBook.getImgSrc());
				if (file.exists()) {
					fileSize -= file.length();
					file.delete();
				} else {
					throw new FileNotFoundException("指定されたファイルは存在しません：" + nowBook.getImgSrc());
				}
			}
			// update user_status
			MultipartFile imgFile = editBookInput.getImgFile();
			userStatusRepository.updateImageRegistrationStatus(userId, 0, fileSize + imgFile.getSize());
			
			// create derectory for user's image storage
			String fileDirectory = imgFolder + caluculateHash(userId);
			try {
	            Files.createDirectories(Paths.get(fileDirectory));
	        } catch (IOException e) {
	            throw new RuntimeException("ディレクトリの作成に失敗しました", e);
	        }
			String fileName = editBookInput.getTitle() + "_" + UUID.randomUUID().toString() + ".jpg";
			String fullPath =  fileDirectory + "/" + fileName;
			book.setImgSrc(fullPath);
			// sava image
			Path filePath = Paths.get(fullPath);
			try (OutputStream stream = Files.newOutputStream(filePath);) {
				byte[] bytes = imgFile.getBytes();
				stream.write(bytes);
			} catch (IOException e) {
				throw new RuntimeException("画像の保存に失敗しました", e);
			}
		} else {
			if (editBookInput.getCheckBoxValue() != null && !(nowBook.getImgSrc().equals(defaultPath))) {
				File file = new File(nowBook.getImgSrc());
				if (file.exists()) {
					userStatusRepository.updateImageRegistrationStatus(userId, -1, -file.length());
					file.delete();
					book.setImgSrc(defaultPath);
				} else {
					throw new FileNotFoundException("指定されたファイルは存在しません：" + nowBook.getImgSrc());
				}
			} else {
				book.setImgSrc(nowBook.getImgSrc());
			}
		}
		bookRepository.save(book);
	}
	
	public void updateFinishedBook(FinishedEditBookInput finishedEditBookInput, Integer userId) throws FileNotFoundException {
		Book nowBook = bookRepository.findById(finishedEditBookInput.getBookId()).orElseThrow(() -> new EntityNotFoundException(finishedEditBookInput.getTitle() + "was not found"));
		Book book = new Book();
		book.setBookId(finishedEditBookInput.getBookId());
		book.setTitle(finishedEditBookInput.getTitle());
		book.setAuthor(finishedEditBookInput.getAuthor());
		book.setGenre(finishedEditBookInput.getGenre());
		book.setComment(finishedEditBookInput.getComment());
		if (!(finishedEditBookInput.getImgFile().isEmpty())) {
			long fileSize = 0;
			if (!(nowBook.getImgSrc().equals(defaultPath))) {
				File file = new File(nowBook.getImgSrc());
				if (file.exists()) {
					fileSize -= file.length();
					file.delete();
				} else {
					throw new FileNotFoundException("指定されたファイルは存在しません：" + nowBook.getImgSrc());
				}
			}
			// update user_status
			MultipartFile imgFile = finishedEditBookInput.getImgFile();
			userStatusRepository.updateImageRegistrationStatus(userId, 0, fileSize + imgFile.getSize());
			
			// create derectory for user's image storage
			String fileDirectory = imgFolder + caluculateHash(userId);
			try {
	            Files.createDirectories(Paths.get(fileDirectory));
	        } catch (IOException e) {
	            throw new RuntimeException("ディレクトリの作成に失敗しました", e);
	        }
			String fileName = finishedEditBookInput.getTitle() + "_" + UUID.randomUUID().toString() + ".jpg";
			String fullPath =  fileDirectory + "/" + fileName;
			book.setImgSrc(fullPath);
			// save image
			Path filePath = Paths.get(fullPath);
			try (OutputStream stream = Files.newOutputStream(filePath);) {
				byte[] bytes = imgFile.getBytes();
				stream.write(bytes);
			} catch (IOException e) {
				throw new RuntimeException("画像の保存に失敗しました", e);
			}
		} else {
			if (finishedEditBookInput.getCheckBoxValue() != null && !(nowBook.getImgSrc().equals(defaultPath))) {
				File file = new File(nowBook.getImgSrc());
				if (file.exists()) {
					userStatusRepository.updateImageRegistrationStatus(userId, -1, -file.length());
					file.delete();
					book.setImgSrc(defaultPath);
				} else {
					throw new FileNotFoundException("指定されたファイルは存在しません：" + nowBook.getImgSrc());
				}
			} else {
				book.setImgSrc(nowBook.getImgSrc());
			}
		}
		bookRepository.save(book);
	}
	
	public void delete(Integer bookId) throws FileNotFoundException {
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("本が見つかりませんでした"));
		if (!(book.getImgSrc().equals(defaultPath))) {
			File file = new File(book.getImgSrc());
			if (file.exists()) {
				userStatusRepository.updateImageRegistrationStatus(bookId, -1, -file.length());
				file.delete();
			} else {
				throw new FileNotFoundException("指定されたファイルは存在しません" + book.getImgSrc());
			}
		}
		bookRepository.deleteById(bookId);
	}
}
