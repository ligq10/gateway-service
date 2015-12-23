package com.changhongit.loving.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.changhongit.loving.message.Cp150Message;

@Component
public class SettingValidator implements Validator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean supports(Class<?> clazz) {
		return Cp150Message.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Cp150Message cp150Message = (Cp150Message) target;
		if (StringUtils.isEmpty(cp150Message.getImei())) {
			errors.rejectValue("imei", "imei.empty");
		}

	}
}
