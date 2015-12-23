package com.changhongit.loving.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.entity.WarningDetail;
import com.changhongit.loving.model.GpsLocation;
import com.changhongit.loving.model.WarningDetailResponse;
import com.changhongit.loving.model.WarningResponse;
import com.changhongit.loving.repository.WarningDetailRepository;

@Controller
public class WarningController {
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private WarningDetailRepository warningDetailRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value = "warnings/view/byimeis", method = RequestMethod.POST)
	public ResponseEntity<?> getWarningLists(@RequestBody List<String> imeis,
			@PageableDefault(page = 0, size = 8) Pageable pageRequest) {
		Page<WarningDetail> warnings = warningDetailRepository
				.findByImeiInOrderByImeiDesc(imeis, pageRequest);
		List<WarningResponse> warningResponses = new ArrayList<>();
		for (WarningDetail warning : warnings) {
			WarningResponse warningResponse = new WarningResponse();
			warningResponse.setId(warning.getId());
			warningResponse.setName(warning.getOwner());
			warningResponse.setDate(dateFormat.format(warning.getDate()));
			warningResponse.setWarning(warning.getContent());
			warningResponse.setType(warning.getType());
			warningResponses.add(warningResponse);
		}
		PagedResources<WarningResponse> response = new PagedResources<>(
				warningResponses, new PagedResources.PageMetadata(
						warnings.getSize(), warnings.getNumber(),
						warnings.getTotalElements(), warnings.getTotalPages()));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "warnings/view/byconditions", method = RequestMethod.GET)
	public ResponseEntity<?> getWarningLists(
			@RequestParam(required = false, defaultValue = "") String imei,
			@RequestParam(required = false, defaultValue = "") String type,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String end,
			@PageableDefault(page = 0, size = 20, sort = "date", direction = Direction.DESC) Pageable pageRequest)
			throws ParseException {
		Date startDate = dateFormat.parse(start);
		Date endDate = dateFormat.parse(end);
		Page<WarningDetail> warnings;
		if (StringUtils.isEmpty(type) || type.equals("ALL")) {
			warnings = warningDetailRepository.findByImeiAndDateBetween(imei,
					startDate, endDate, pageRequest);
		} else {
			
			warnings = warningDetailRepository.findByImeiAndTypeAndDateBetween(
					imei, type, startDate, endDate, pageRequest);
		}
		List<WarningResponse> warningResponses = new ArrayList<>();
		for (WarningDetail warning : warnings) {
			WarningResponse warningResponse = new WarningResponse();
			warningResponse.setId(warning.getId());
			warningResponse.setName(warning.getOwner());
			warningResponse.setDate(dateFormat.format(warning.getDate()));
			warningResponse.setWarning(warning.getContent());
			warningResponse.setType(warning.getType());
			warningResponses.add(warningResponse);
		}
		PagedResources<WarningResponse> response = new PagedResources<>(
				warningResponses, new PagedResources.PageMetadata(
						warnings.getSize(), warnings.getNumber(),
						warnings.getTotalElements(), warnings.getTotalPages()));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "warnings/view/byid", method = RequestMethod.GET)
	public ResponseEntity<?> getWarningDetail(@RequestParam String id) {
		WarningDetail warning = warningDetailRepository.findOne(id);
		if (warning == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (warning.getGpsStatus().equals(0)) {
			GpsLocation gpsLocationByBaseLocation = getGpsLocationByBaseLocation(
					warning.getMcc(), warning.getMnc(), warning.getLac(),
					warning.getCell());
			warning.setLongitude(gpsLocationByBaseLocation.getLongitude());
			warning.setLatitude(gpsLocationByBaseLocation.getLatitude());
		}
		WarningDetailResponse warningDetailResponse = new WarningDetailResponse();
		BeanUtils.copyProperties(warning, warningDetailResponse);
		warningDetailResponse.setName(warning.getOwner());
		warningDetailResponse.setType(warning.getType());
		warningDetailResponse.setWarning(warning.getContent());
		warningDetailResponse.setDate(dateFormat.format(warning.getDate()));
		return new ResponseEntity<>(warningDetailResponse, HttpStatus.OK);
	}
	
	GpsLocation getGpsLocationByBaseLocation(String mcc, String mnc,
			String lac, String cell) {
		return new GpsLocation();
	}
}
