package com.example.reading.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.reading.dto.BookInfoDto;
import com.example.reading.dto.FinishedListDto;
import com.example.reading.dto.ReadingListDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ListRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
//	読書リスト
	public List<ReadingListDto> getReadingListByUserId(Integer userId) {
		String query = "SELECT r.user_id, r.book_id, r.start_date, b.title, b.img_src FROM reading_list_registration r INNER JOIN bookinfo b ON r.book_id = b.book_id WHERE r.user_id = ?";
		return jdbcTemplate.query(query, new RegingListRowMapper(), userId);
	}
	
	// 検索
	public List<ReadingListDto> searchBook(Integer userId, String title, String genre, String author, YearMonth roughStartDate, LocalDate specificStartDate) {
		String query = "SELECT r.user_id, r.book_id, r.start_date, b.title, b.img_src FROM reading_list_registration r INNER JOIN bookinfo b ON r.book_id = b.book_id WHERE r.user_id = ?";
		StringBuilder sb = new StringBuilder();
		sb.append(query);
	    // クエリのappend
		if (!(title.isBlank())) { // タイトル以上
			sb.append(" AND b.title LIKE ?");
			String paramTitle = "%" + title + "%";
			if (!(genre.isBlank())) {
				sb.append(" AND b.genre = ?");
				if (!(author.isBlank())) {
					sb.append(" AND b.author LIKE ?");
					String paramAuthor = "%" + author + "%";
					if (roughStartDate != null && specificStartDate != null) {
						sb.append(" AND r.start_date >= ?");
						return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramTitle, genre, paramAuthor, specificStartDate);
					} else if (roughStartDate != null) {
						sb.append(" AND r.start_date >= ?");
						LocalDate newDate = roughStartDate.atDay(1);
						return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramTitle, genre, paramAuthor, newDate);
					}  else if (specificStartDate != null) {
						sb.append(" AND r.start_date >= ?");
						return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramTitle, genre, paramAuthor, specificStartDate);
					}
					return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramTitle, genre, paramAuthor);
				}
				return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramTitle, genre);
			}
			return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramTitle);
		} else if (!(genre.isBlank())) { // ジャンル以上
			sb.append(" AND b.genre = ?");
			if (!(author.isBlank())) {
				sb.append(" AND b.author LIKE ?");
				String paramAuthor = "%" + author + "%";
				if (roughStartDate != null && specificStartDate != null) {
					sb.append(" AND r.start_date >= ?");
					return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, genre, paramAuthor, specificStartDate);
				} else if (roughStartDate != null) {
					sb.append(" AND r.start_date >= ?");
					LocalDate newDate = roughStartDate.atDay(1);
					return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, genre, paramAuthor, newDate);
				} else if (specificStartDate != null) {
					sb.append(" AND r.start_date >= ?");
					return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, genre, paramAuthor, specificStartDate);
				}
				return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, genre, paramAuthor);
			}
			return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, genre);
		} else if (!(author.isBlank())) { // 著者以上
			sb.append(" AND b.author LIKE ?");
			String paramAuthor = "%" + author + "%";
			if (roughStartDate != null && specificStartDate != null) {
				sb.append(" AND r.start_date >= ?");
				return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramAuthor, specificStartDate);
			} else if (roughStartDate != null) {
				sb.append(" AND r.start_date >= ?");
				LocalDate newDate = roughStartDate.atDay(1);
				return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramAuthor, newDate);
			} else if (specificStartDate != null) {
				sb.append(" AND r.start_date >= ?");
				return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramAuthor, specificStartDate);
			}
			return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, paramAuthor);
		} else if (roughStartDate != null && specificStartDate != null) { //　詳細日優先
			sb.append(" AND r.start_date >= ?");
			return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, specificStartDate);
		} else if (roughStartDate != null) {
			sb.append(" AND r.start_date >= ?");
			LocalDate newDate = roughStartDate.atDay(1);
			return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, newDate);
		} else if (specificStartDate != null) {
			sb.append(" AND r.start_date >= ?");
			return jdbcTemplate.query(sb.toString(), new RegingListRowMapper(), userId, specificStartDate);
		}
		return new ArrayList<ReadingListDto>();
	}
	
	// by genre
	public List<ReadingListDto> searchBookByGenre(Integer userId, String genre) {
		String query = "SELECT r.user_id, r.book_id, r.start_date, b.title, b.img_src FROM reading_list_registration r INNER JOIN bookinfo b ON r.book_id = b.book_id WHERE r.user_id = ? AND b.genre = ?";
		return jdbcTemplate.query(query, new RegingListRowMapper(), userId, genre);
	}
	
	static class RegingListRowMapper implements RowMapper<ReadingListDto> {
		@Override
		public ReadingListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReadingListDto readingListDto = new ReadingListDto();
			readingListDto.setUserId(rs.getInt("user_id"));
			readingListDto.setBookId(rs.getInt("book_id"));
			readingListDto.setStartDate(LocalDate.parse(rs.getString("start_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			BookInfoDto bookInfoDto = new BookInfoDto(rs.getString("title"), rs.getString("img_src"));
			readingListDto.setBookInfoDto(bookInfoDto);
			return readingListDto;
		}
		
	}
	
	
//    読了済みリスト
	public List<FinishedListDto> getFinishedListByUserId(Integer userId) {
		String query = "SELECT f.user_id, f.book_id, f.start_date, f.end_date, b.title, b.img_src FROM finished_list_registration f INNER JOIN bookinfo b ON f.book_id = b.book_id WHERE f.user_id = ?";
		return jdbcTemplate.query(query, new FinishedListRowMapper(), userId);
	}
	
	// 検索
	public List<FinishedListDto> searchFinishedBook(Integer userId, String title, String genre, String author, YearMonth roughStartDate, YearMonth roughEndDate, LocalDate specificEndDate) {
		String query = "SELECT f.user_id, f.book_id, f.start_date, f.end_date, b.title, b.img_src FROM finished_list_registration f INNER JOIN bookinfo b ON f.book_id = b.book_id WHERE f.user_id = ?";
		StringBuilder sb = new StringBuilder();
		sb.append(query);
	    // クエリのappend
		if (!(title.isBlank())) { // タイトル以上
			sb.append(" AND b.title LIKE ?");
			String paramTitle = "%" + title + "%";
			if (!(genre.isBlank())) {
				sb.append(" AND b.genre = ?");
				if (!(author.isBlank())) {
					sb.append(" AND b.author LIKE ?");
					String paramAuthor = "%" + author + "%";
					if (roughStartDate != null && roughEndDate != null && specificEndDate != null || roughStartDate != null && specificEndDate != null || roughEndDate != null && specificEndDate != null) {
						sb.append(" AND f.end_date = ?");
						return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre, paramAuthor, specificEndDate);
					} else if (roughStartDate != null) {
						LocalDate newStartDate = roughStartDate.atDay(1);
						if (roughEndDate != null) {
							sb.append(" AND f.end_date BETWEEN ? AND ?");
							LocalDate newEndDate = roughEndDate.atEndOfMonth();
							return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre, paramAuthor, newStartDate, newEndDate);
						}
						sb.append(" AND f.end_date >= ?");
						return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre, paramAuthor, newStartDate);
					} else if (roughEndDate != null) {
						sb.append(" AND f.end_date <= ?");
						LocalDate newEndDate = roughEndDate.atEndOfMonth();
						return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre, paramAuthor, newEndDate);
					} else if (specificEndDate != null) {
						sb.append(" AND f.end_date = ?");
						return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre, paramAuthor, specificEndDate);
					}
					return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre, paramAuthor);
				}
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle, genre);
			}
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramTitle);
		} else if (!(genre.isBlank())) { // ジャンル以上
			sb.append(" AND b.genre = ?");
			if (!(author.isBlank())) {
				sb.append(" AND b.author LIKE ?");
				String paramAuthor = "%" + author + "%";
				if (roughStartDate != null && roughEndDate != null && specificEndDate != null || roughStartDate != null && specificEndDate != null || roughEndDate != null && specificEndDate != null) {
					sb.append(" AND f.end_date = ?");
					return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre, paramAuthor, specificEndDate);
				} else if (roughStartDate != null) {
					LocalDate newStartDate = roughStartDate.atDay(1);
					if (roughEndDate != null) {
						sb.append(" AND f.end_date BETWEEN ? AND ?");
						LocalDate newEndDate = roughEndDate.atEndOfMonth();
						return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre, paramAuthor, newStartDate, newEndDate);
					}
					sb.append(" AND f.end_date >= ?");
					return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre, paramAuthor, newStartDate);
				} else if (roughEndDate != null) {
					sb.append(" AND f.end_date <= ?");
					LocalDate newEndDate = roughEndDate.atEndOfMonth();
					return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre, paramAuthor, newEndDate);
				} else if (specificEndDate != null) {
					sb.append(" AND f.end_date = ?");
					return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre, paramAuthor, specificEndDate);
				}
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre, paramAuthor);
			}
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, genre);
		} else if (!(author.isBlank())) { // 著者以上
			sb.append(" AND b.author LIKE ?");
			String paramAuthor = "%" + author + "%";
			if (roughStartDate != null && roughEndDate != null && specificEndDate != null || roughStartDate != null && specificEndDate != null || roughEndDate != null && specificEndDate != null) {
				sb.append(" AND f.end_date = ?");
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramAuthor, specificEndDate);
			} else if (roughStartDate != null) {
				LocalDate newStartDate = roughStartDate.atDay(1);
				if (roughEndDate != null) {
					sb.append(" AND f.end_date BETWEEN ? AND ?");
					LocalDate newEndDate = roughEndDate.atEndOfMonth();
					return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramAuthor, newStartDate, newEndDate);
				}
				sb.append(" AND f.end_date >= ?");
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramAuthor, newStartDate);
			} else if (roughEndDate != null) {
				sb.append(" AND f.end_date <= ?");
				LocalDate newEndDate = roughEndDate.atEndOfMonth();
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramAuthor, newEndDate);
			} else if (specificEndDate != null) {
				sb.append(" AND f.end_date = ?");
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramAuthor, specificEndDate);
			}
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, paramAuthor);
		} else if (roughStartDate != null && roughEndDate != null && specificEndDate != null || roughStartDate != null && specificEndDate != null || roughEndDate != null && specificEndDate != null) { // 詳細日優先
			sb.append(" AND f.end_date = ?");
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, specificEndDate);
		} else if (roughStartDate != null) { // 期間開始日 or 期間
			LocalDate newStartDate = roughStartDate.atDay(1);
			if (roughEndDate != null) {
				sb.append(" AND f.end_date BETWEEN ? AND ?");
				LocalDate newEndDate = roughEndDate.atEndOfMonth();
				return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, newStartDate, newEndDate);
			}
			sb.append(" AND f.end_date >= ?");
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, newStartDate);
		} else if (roughEndDate != null) { // 期間終了日のみ
			sb.append(" AND f.end_date <= ?");
			LocalDate newEndDate = roughEndDate.atEndOfMonth();
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, newEndDate);
		} else if (specificEndDate != null) { // 詳細日程のみ
			sb.append(" AND f.end_date = ?");
			return jdbcTemplate.query(sb.toString(), new FinishedListRowMapper(), userId, specificEndDate);
		}
		return new ArrayList<FinishedListDto>();
	}
	
	// by genre
	public List<FinishedListDto> searchFinishedBookByGenre(Integer userId, String genre) {
		String query = "SELECT f.user_id, f.book_id, f.start_date, f.end_date, b.title, b.img_src FROM finished_list_registration f INNER JOIN bookinfo b ON f.book_id = b.book_id WHERE f.user_id = ? AND b.genre = ?";
		return jdbcTemplate.query(query, new FinishedListRowMapper(), userId, genre);
	}
	
	static class FinishedListRowMapper implements RowMapper<FinishedListDto> {
		@Override
		public FinishedListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinishedListDto finishedListDto = new FinishedListDto();
			finishedListDto.setUserId(rs.getInt("user_id"));
			finishedListDto.setBookId(rs.getInt("book_id"));
			finishedListDto.setStartDate(LocalDate.parse(rs.getString("start_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			finishedListDto.setEndDate(LocalDate.parse(rs.getString("end_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			BookInfoDto bookInfoDto = new BookInfoDto(rs.getString("title"), rs.getString("img_src"));
			finishedListDto.setBookInfoDto(bookInfoDto);
			return finishedListDto;
		}
	}
}
