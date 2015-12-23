package com.changhongit.loving.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.repository.TerminalUserRepository;

@Component
public class TerminalDeleteValidator implements Validator {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	private static final String X_TOKEN = "X-Token";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Environment env;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Terminal.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		Terminal terminal = (Terminal) target;
		deleteTerminalUser(terminal, errors);
		
	}
	
	private void deleteTerminalUser(Terminal terminal, Errors errors) {
		TerminalUser terminalUser = terminalUserRepository
				.findByTerminalImei(terminal.getImei());
		if (terminalUser != null) {
			try {
				terminalUserRepository.delete(terminalUser);
			} catch (Exception e) {
				logger.debug(e.toString());
				errors.rejectValue("imei", "imei.withuser", "终端关联用户");
			}
		}
	}
	
}
