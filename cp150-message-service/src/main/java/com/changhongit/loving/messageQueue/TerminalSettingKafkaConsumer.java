package com.changhongit.loving.messageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import com.changhongit.loving.Cp100SettingKey;
import com.changhongit.loving.document.Cp150Contacts;
import com.changhongit.loving.model.Contact;
import com.changhongit.loving.model.ContactView;
import com.changhongit.loving.model.Cp150ContactsSetting;
import com.changhongit.loving.repository.Cp150ContactsRepository;

public class TerminalSettingKafkaConsumer implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private KafkaStream m_stream;
	
	private MessagePack messagePack;
	
	private Cp150ContactsRepository contactsRepository;
	
	public TerminalSettingKafkaConsumer(KafkaStream m_stream,
			MessagePack messagePack) {
		this.m_stream = m_stream;
		this.messagePack = messagePack;
	}
	
	@Override
	public void run() {
		
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		
		while (it.hasNext()) {
			try {
				byte[] body = it.next().message();
				Cp150ContactsSetting cp150Setting = messagePack.read(body,
						Cp150ContactsSetting.class);
				String imei = cp150Setting.getImei();
				Cp150Contacts cp150Contacts = contactsRepository
						.findByImei(imei);
				if (cp150Contacts == null) {
					cp150Contacts = new Cp150Contacts();
					cp150Contacts.setImei(imei);
				}
				List<ContactView> contactViews = new ArrayList<>();
				if (!CollectionUtils.isEmpty(cp150Setting.getSetting())) {
					for (Entry<String, Contact> entry : cp150Setting
							.getSetting().entrySet()) {
						ContactView contactView = new ContactView();
						BeanUtils.copyProperties(entry.getValue(), contactView);
						contactView.setIndex(entry.getKey());
						contactViews.add(contactView);
					}
				}
				if (cp150Setting.getKey().equals(Cp100SettingKey.CONTACT_LIST)) {
					cp150Contacts.setContacts(contactViews);
				} else if (cp150Setting.getKey().equals(
						Cp100SettingKey.WHITE_LIST)) {
					cp150Contacts.setWhiteList(contactViews);
				}
				contactsRepository.save(cp150Contacts);
			} catch (Exception e) {
				logger.error("Error:", e);
			}
		}
	}
	
	public void setContactsRepository(Cp150ContactsRepository contactsRepository) {
		this.contactsRepository = contactsRepository;
	}
	
}
