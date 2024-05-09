package com.example.reading.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.model.PasswordResetToken;
import com.example.reading.repository.PasswordResetTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailSenderService {
	
	private final JavaMailSender mailSender;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Value("${host.email.address}")
	private String hostEmail;
	
	@Value("${password.reset.link}")
	private String resetLink;
	
	private String signature = "<br>--------------------------------------------------------------------------------<br>"
			+ "MyLibrary運営局<br>"
			+ "Email：mylibraryorganization@gmail.com<br>"
			+ "--------------------------------------------------------------------------------";
	
	// メールアドレス登録時
	public void sendEmail(String toEmail, String subject, String username, String body) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(hostEmail);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			// html対応していない場合
			String text = username + "様、" + body;
			// 対応している場合
			String html = "<html><body>";
			html += username + "様<br><br>";
			html += body;
			html += signature;
			html += "<body><html>";
			helper.setText(text, html);
			
			mailSender.send(message);
		} catch(MessagingException e) {
			throw new RuntimeException("メールの送信に失敗しました", e);
		}
	}
	
	// パスワードリセット時
	public void sendEmailWithResetLink(String toEmail, String secretWord, Integer userId) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(hostEmail);
			helper.setTo(toEmail);
			helper.setSubject("パスワードリセットリンクの送信");
			
			// ワンタイムトークンの生成＆保存
			String token = UUID.randomUUID().toString();
			PasswordResetToken passwordResetToken = new PasswordResetToken();
			passwordResetToken.setUserId(userId);
			passwordResetToken.setOnetimeToken(token);
			passwordResetToken.setGeneratedTime(LocalDateTime.now());
			passwordResetToken.setSecretWord(secretWord);
			passwordResetTokenRepository.save(passwordResetToken);
			String link = resetLink + token;
			
			// html対応していない場合
			String text = "次のURLを直接ブラウザに入力してください、パスワードリセット画面に遷移します。" + link;
			// 対応している場合
			String html = "<html><body>";
			html += "以下のURLをクリックしてください、パスワードリセット画面に遷移します。<br>";
			html += "<a href='" + link + "'>" + link + "</a><br>";
			html += "※有効時間は3分です";
			html += signature;
			html += "<body><html>";
			helper.setText(text, html);
			
			mailSender.send(message);
		} catch(MessagingException e) {
			throw new RuntimeException("メールの送信に失敗しました", e);
		}
	}

}
