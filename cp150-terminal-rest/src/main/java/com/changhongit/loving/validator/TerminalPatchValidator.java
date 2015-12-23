package com.changhongit.loving.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.repository.TerminalUserRepository;

@Component
public class TerminalPatchValidator implements Validator {
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Terminal.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		Terminal terminalRequest = (Terminal) target;
		Terminal terminal = cp150Repository.findByImei(terminalRequest
				.getImei());
		ValidationUtils.rejectIfEmpty(errors, "imei", "imei.empty");
		if (!terminalRequest.equals(terminal)) {
			errors.rejectValue("imei", "imei.changed");
		}
		
		if (terminalRequest.getSn() != null
				&& terminalRequest.getSn().equals("")) {
			errors.rejectValue("sn", "sn.empty");
		}
		if (terminalRequest.getModelNumber() != null
				&& terminalRequest.getModelNumber().equals("")) {
			errors.rejectValue("modelNumber", "modelNumber.empty");
		}
		if (terminalRequest.getCheckCode() != null
				&& terminalRequest.getCheckCode().equals("")) {
			errors.rejectValue("checkCode", "checkCode.empty");
		}
		if (!errors.hasErrors() && terminalRequest.getCheckCode() != null) {
			TerminalUser terminalUser = terminalUserRepository
					.findByTerminalImei(terminalRequest.getImei());
			terminalUser.setTerminalCheckCode(terminalRequest.getCheckCode());
			terminalUserRepository.save(terminalUser);
		}
		
	}
	
}
