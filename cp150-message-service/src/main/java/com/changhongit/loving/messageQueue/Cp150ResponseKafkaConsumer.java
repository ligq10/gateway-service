package com.changhongit.loving.messageQueue;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.changhongit.loving.entity.MessagesToCp150;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.model.ResponseMsgPack;

public class Cp150ResponseKafkaConsumer implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessagesToCp150Repository messagesToCp150Repository;
	
	private KafkaStream m_stream;
	
	private MessagePack messagePack;
	
	public Cp150ResponseKafkaConsumer(KafkaStream m_stream,
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
				ResponseMsgPack message = messagePack.read(body,
						ResponseMsgPack.class);
				MessagesToCp150 toCp150 = messagesToCp150Repository
						.findByImeiAndSeq(message.getImei(), message.getSeq());
				if (toCp150 != null) {
					messagesToCp150Repository.delete(toCp150);
				}
			} catch (Exception e) {
				logger.error("Error:", e);
			}
		}
	}
	
	public void setMessagesToCp150Repository(
			MessagesToCp150Repository messagesToCp150Repository) {
		this.messagesToCp150Repository = messagesToCp150Repository;
	}
	
}
