package com.changhongit.loving.validator;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.request.TerminalBindGroupRequest;

@Component
public class TerminalBindGroupValidator implements Validator {
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	private static final String FORMAT_STRING = "第%s行%s字段:%s   %s";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return TerminalBindGroupRequest.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		TerminalBindGroupRequest terminalRequest = (TerminalBindGroupRequest) target;
		String imei = terminalRequest.getImei().trim();
		Group group = groupRepository.findOne(terminalRequest.getGroupid());
		Group parent = groupRepository.findOne(terminalRequest.getParentid());
		Terminal terminal = cp150Repository.findByImei(imei);
		int row = terminalRequest.getRowNum();
		if (parent == null) {
			errors.rejectValue("parentid", "proupid.invalid", "父组ID无效");
		} else if (group == null || !groupInParent(parent, group)) {
			errors.rejectValue("groupid", "groupid.invalid", "导入的目标分组无效");
		} else if (!Pattern.matches("[0-9]{15}", imei) || terminal == null) {
			errors.rejectValue("imei", "imei.invalid", String.format(
					FORMAT_STRING, row, "IMEI", imei, "无效终端号，无该终端记录"));
		} else {
			Group from = groupRepository.findOne(terminal.getGroupId());
			if (from == null) {
				errors.rejectValue("imei", "imei.invalid", String.format(
						FORMAT_STRING, row, "IMEI", imei, "无效终端号，无该终端记录"));
			} else if (group.equals(from)) {
				errors.rejectValue("imei", "imei.invalid", String.format(
						FORMAT_STRING, row, "IMEI", imei, "改终端已在目标分组中，不能重复导入"));
			} else if (!groupInParent(parent, from)) {
				errors.rejectValue("imei", "imei.invalid", String.format(
						FORMAT_STRING, row, "IMEI", imei, "无权限导入该IMEI终端"));
			}
		}
		
	}
	
	private boolean groupInParent(Group parent, Group group) {
		if (parent.equals(group)) {
			return true;
		} else {
			for (Group child : parent.getChildrens()) {
				if (groupInParent(child, group)) {
					return true;
				}
			}
		}
		return false;
	}
}
