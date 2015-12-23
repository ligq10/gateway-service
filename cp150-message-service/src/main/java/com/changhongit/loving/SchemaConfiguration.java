package com.changhongit.loving;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.changhongit.loving.JGit.JGitRepository;

@Component
public class SchemaConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment environment;
	
	@Autowired
	JGitRepository jGitRepository;
	
	public Schema getAvroSchema(String key) {
		String filePath = environment.getProperty(key);
		return new Schema.Parser().parse(jGitRepository.findOne(filePath,
				"master"));
	}
	
}
