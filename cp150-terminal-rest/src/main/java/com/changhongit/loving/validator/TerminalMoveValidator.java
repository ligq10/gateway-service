package com.changhongit.loving.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.request.TerminalMoveRequest;

@Component
public class TerminalMoveValidator implements Validator {
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return TerminalMoveRequest.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		TerminalMoveRequest moveRequest = (TerminalMoveRequest) target;
		Terminal terminal = cp150Repository.findOne(moveRequest.getId());
		Group group = groupRepository.findOne(moveRequest.getGroupid());
		if (terminal == null
				|| group == null
				|| group.getParent() == null
				|| groupRepository.findOne(terminal.getGroupId()) == null
				|| !groupInParent(group.getParent(),
						groupRepository.findOne(terminal.getGroupId()))) {
			errors.rejectValue("id", "id.invalid", "无权限删除该终端");
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
