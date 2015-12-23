package com.changhongit.loving.validator;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.changhongit.loving.UtilValue;
import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.request.TerminalImportRequest;

@Component
public class TerminalImportValidator implements Validator {
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	private static final String FORMAT_STRING = "第%s行%s字段:%s   %s";
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return TerminalImportRequest.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		TerminalImportRequest terminalRequest = (TerminalImportRequest) target;
		int row = terminalRequest.getRowNum();
		if (!terminalRequest.getGroupId().equals(UtilValue.GHT_ID)) {
			moveCauseToErrors(errors, terminalRequest, row);
		} else {
			
			importCauseToErroes(errors, terminalRequest, row);
		}
		
	}
	
	private void moveCauseToErrors(Errors errors,
			TerminalImportRequest terminalRequest, int row) {
		Group group = groupRepository.findOne(terminalRequest.getGroupId());
		if (group == null) {
			errors.rejectValue("groupId", "groupId.invalid", "groupid无效");
		} else {
			Group parent = group.getParent();
			if (parent == null) {
				errors.rejectValue("groupId", "groupId.invalid", "groupid无效");
			} else {
				Terminal terminal = cp150Repository.findByImei(terminalRequest
						.getImei());
				if (terminal == null) {
					errors.rejectValue("imei", "imei.invalid", String.format(
							FORMAT_STRING, row, "IMEI",
							terminalRequest.getImei(), "无该IMEI终端记录"));
				} else if (!terminal.getGroupId().equals(parent.getId())) {
					errors.rejectValue("imei", "imei.invalid", String.format(
							FORMAT_STRING, row, "IMEI", terminalRequest
									.getImei(), String.format(
									"在父分组%s中未找到该IMEI终端", parent.getName())));
				}
			}
		}
	}
	
	private void importCauseToErroes(Errors errors,
			TerminalImportRequest terminalRequest, int row) {
		if (!Pattern.matches("\\w{1,20}", terminalRequest.getSn())) {
			errors.rejectValue(
					"sn",
					"sn.invalid",
					String.format(FORMAT_STRING, row, "SN",
							terminalRequest.getSn(), "必填，长度1~20字符"));
		} else if (!Pattern.matches("[0-9]{15}", terminalRequest.getImei())) {
			errors.rejectValue(
					"imei",
					"imei.invalid",
					String.format(FORMAT_STRING, row, "IMEI",
							terminalRequest.getImei(), "必填，15位数字"));
		} else if (cp150Repository.findByImei(terminalRequest.getImei()) != null) {
			errors.rejectValue(
					"imei",
					"imei.invalid",
					String.format(FORMAT_STRING, row, "IMEI",
							terminalRequest.getImei(), "该终端数据已存在，不能重复导入"));
		} else if (!Pattern.matches("[a-z0-9A-Z]{4}",
				terminalRequest.getCheckCode())) {
			errors.rejectValue(
					"checkCode",
					"checkCode.invalid",
					String.format(FORMAT_STRING, row, "校验码",
							terminalRequest.getCheckCode(), "必填，4位数字或字母或组合"));
		} else if (!Pattern.matches("\\w{1,20}",
				terminalRequest.getModelNumber())) {
			errors.rejectValue(
					"modelNumber",
					"modelNumber.invalid",
					String.format(FORMAT_STRING, row, "终端型号",
							terminalRequest.getModelNumber(), "必填，长度1~20字符"));
		} else if (!StringUtils.isEmpty(terminalRequest.getSim())
				&& !Pattern.matches("[0-9]{11}", terminalRequest.getSim())) {
			errors.rejectValue(
					"sim",
					"sim.invalid",
					String.format(FORMAT_STRING, row, "SIM",
							terminalRequest.getSim(), "非必填，11位数字，手机号码"));
		}
	}
	
}
