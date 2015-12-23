package com.changhongit.loving.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.changhongit.loving.request.TerminalRegisterRequest;

@Component
public class TerminalRegisterValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return TerminalRegisterRequest.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		TerminalRegisterRequest terminal = (TerminalRegisterRequest) target;
		ValidationUtils.rejectIfEmpty(errors, "imei", "imei.empty");
	}
	
}
