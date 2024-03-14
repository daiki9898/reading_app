package com.example.reading.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = {UserEmailValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmail {
	
	String message() default "このメールアドレスは既に他のアカウントと紐づけられています";
	
	Class<?>[] groups() default {};
	 
	Class<? extends Payload>[] payload() default {};
}
