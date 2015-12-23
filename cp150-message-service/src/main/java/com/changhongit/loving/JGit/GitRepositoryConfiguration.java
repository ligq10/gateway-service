package com.changhongit.loving.JGit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created by Jerry on 2015/11/11 0011.
 */
@Configuration
@ConditionalOnMissingBean(JGitRepository.class)
public class GitRepositoryConfiguration {
	
	@Autowired
	private ConfigurableEnvironment environment;
	
	@Bean
	public JGitRepository jGitRepository() {
		return new JGitEnvironmentRepository();
	}
}
