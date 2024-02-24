package com.example.reading.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TimeoutUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public TimeoutUrlAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	 @Override
	 protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
		 
		 String redirectUrl = super.buildRedirectUrlToLoginPage(request, response, authException);
	     if (isRequestedSessionInvalid(request)) {
	            redirectUrl += redirectUrl.contains("?") ? "&" : "?";
	            redirectUrl += "timeout";
	        }
	        return redirectUrl;
	    }

	    private boolean isRequestedSessionInvalid(HttpServletRequest request) {
	        return request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
	    }
}
