package com.changhongit.loving.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.repository.TerminalRestRepository;

@Component
public class TerminalAddValidator implements Validator {
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Terminal.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		Terminal terminal = (Terminal) target;
		ValidationUtils.rejectIfEmpty(errors, "imei", "imei.empty");
		if (!StringUtils.isEmpty(terminal.getImei())) {
			if (cp150Repository.findByImei(terminal.getImei()) != null) {
				errors.rejectValue("imei", "imei.unique");
			}
		}
		
		ValidationUtils.rejectIfEmpty(errors, "sn", "sn.empty");
		ValidationUtils.rejectIfEmpty(errors, "modelNumber",
				"modelNumber.empty");
		ValidationUtils.rejectIfEmpty(errors, "checkCode", "checkCode.empty");
	}
	
}
