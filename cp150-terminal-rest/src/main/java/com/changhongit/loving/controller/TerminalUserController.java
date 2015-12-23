package com.changhongit.loving.controller;

import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.TerminalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import static org.springframework.util.StringUtils.isEmpty;

@Controller
public class TerminalUserController {

    @Autowired
    private TerminalUserRepository terminalUserRepository;

    @Autowired
    private GroupRepository groupRepository;

    @RequestMapping(value = "terminalusers/advancesearch")
    public ResponseEntity<?> searchTerminalUser(@RequestParam(required = false) String keyword, @RequestParam(required = false) String gender, @RequestParam(required = false) String medicalHistory, @RequestParam(required = false) Integer minAge, @RequestParam(required = false) Integer maxAge, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer size, @RequestParam String ownerGroupId) {

        List<String> groupIds = new ArrayList<>();
        Group rootGroup = groupRepository.findOne(ownerGroupId);
        if (rootGroup == null){
            return new ResponseEntity<>("Invalid ownerGroupId.", HttpStatus.BAD_REQUEST);
        }
        groupIds.add(rootGroup.getId());
        List<Group> childrens = rootGroup.getChildrens();
        if (childrens.size() > 0){
            addChildrenGroupIds(groupIds, childrens);
        }

        if (isEmpty(keyword) && isEmpty(gender) && isEmpty(medicalHistory) && (isEmpty(minAge) || isEmpty(maxAge))) {
            return new ResponseEntity<>("At least one search param is request.", HttpStatus.BAD_REQUEST);
        }
        String searchKeyword = "";
        String searchGender = "";
        if (!isEmpty(keyword)) {
            searchKeyword = keyword;
        }
        if (!isEmpty(gender)) {
            searchGender = gender;
        }
        Date minBirthday = null;
        Date maxBirthday = null;
        if (!isEmpty(minAge) && !isEmpty(maxAge)) {

            GregorianCalendar now = new GregorianCalendar();
            now.setTime(new Date());

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(now.get(Calendar.YEAR) - maxAge, now.get(Calendar.MONTH), now.get(Calendar.DATE));
            minBirthday = calendar.getTime();

            calendar.set(now.get(Calendar.YEAR) - minAge, now.get(Calendar.MONTH), now.get(Calendar.DATE));
            maxBirthday = calendar.getTime();
        }

        if (isEmpty(medicalHistory)) {
            Page<TerminalUser> terminalUsers;
            if (!isEmpty(minAge) && !isEmpty(maxAge)) {
                terminalUsers = terminalUserRepository.searchByKeywordAndGenderAndAgeAndBirthdayBetween(searchKeyword, searchGender, minBirthday, maxBirthday,groupIds, new PageRequest(page, size));
            } else {
                terminalUsers = terminalUserRepository.searchByKeywordAndGenderAndAge(searchKeyword, searchGender,groupIds, new PageRequest(page, size));
            }
            return new ResponseEntity<>(terminalUsers, HttpStatus.OK);
        } else {
            List<String> searchMedicalHistory = new ArrayList<>();
            String[] split = medicalHistory.split(",");
            for (int i = 0; i < split.length; i++) {
                searchMedicalHistory.add(split[i].trim());
            }
            Page<TerminalUser> terminalUsers;
            if (!isEmpty(minAge) && !isEmpty(maxAge)) {
                terminalUsers = terminalUserRepository.searchByKeywordAndGenderAndAgeAndMedicalHistoryAndBirthdayBetween(searchKeyword, searchGender, minBirthday, maxBirthday, searchMedicalHistory,groupIds, new PageRequest(page, size));
            } else {
                terminalUsers = terminalUserRepository.searchByKeywordAndGenderAndAgeAndMedicalHistory(searchKeyword, searchGender, searchMedicalHistory, groupIds, new PageRequest(page, size));
            }
            return new ResponseEntity<>(terminalUsers, HttpStatus.OK);
        }
    }

    private void addChildrenGroupIds(List<String> groupIds, List<Group> childrens) {
        for (Group group: childrens){
            groupIds.add(group.getId());
            List<Group> subChildren = group.getChildrens();
            if (subChildren.size() > 0){
                addChildrenGroupIds(groupIds, subChildren);
            }
        }
    }
}
