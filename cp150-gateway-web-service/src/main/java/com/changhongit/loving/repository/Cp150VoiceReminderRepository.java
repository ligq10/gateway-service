package com.changhongit.loving.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.document.Cp150VoiceReminder;

@RepositoryRestResource(exported = false)
public interface Cp150VoiceReminderRepository extends
		CrudRepository<Cp150VoiceReminder, String> {
	
	Cp150VoiceReminder findByImei(String imei);
}
