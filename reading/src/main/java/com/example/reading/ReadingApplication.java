package com.example.reading;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ReadingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadingApplication.class, args);
	}
	
	@PostConstruct
    public void init() {
        // アプリケーション全体でのデフォルトタイムゾーンを設定
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
    }

}