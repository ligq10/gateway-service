package com.changhongit.loving.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.repository.TerminalUserRepository;
import com.changhongit.loving.request.UserSyncRequest;
import com.google.common.collect.Lists;

@Component
public class TerminalSaveValidator implements Validator {
	
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
		creatTerminalUser(terminal);
		userSync(env.getProperty(X_TOKEN), terminal);
		
	}
	
	private void creatTerminalUser(Terminal terminal) {
		TerminalUser terminalUser = new TerminalUser();
		terminalUser.init();
		terminalUser.setTerminalImei(terminal.getImei());
		terminalUser.setTerminalCheckCode(terminal.getCheckCode());
		terminalUser.setTerminal(terminal);
		terminalUserRepository.save(terminalUser);
	}
	
	private void userSync(String token, Terminal terminal) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.put(
				"Content-Type",
				Lists.newArrayList("application/vnd.jiahua.commands.syncaccount+json"));
		headers.put(X_TOKEN, Lists.newArrayList(token));
		UserSyncRequest userSyncRequest = new UserSyncRequest();
		userSyncRequest.setLoginName(terminal.getImei());
		userSyncRequest.setPassword(terminal.getImei());
		userSyncRequest.setUuid(terminal.getId());
		restTemplate.exchange(env.getRequiredProperty("users.syncaccount"),
				HttpMethod.POST, new HttpEntity<>(userSyncRequest, headers),
				ResponseEntity.class);
	}
}
