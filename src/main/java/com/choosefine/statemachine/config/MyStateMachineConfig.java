/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.choosefine.statemachine.config;

import java.util.EnumSet;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.redis.RedisStateMachinePersister;

//@Configuration
public class MyStateMachineConfig implements ApplicationContextAware{

	private ApplicationContext applicationContext;

	@Autowired
	private E1Action e1Action;

	@Autowired
	private E2Action e2Action;

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private AddAction addAction;

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ProxyFactoryBean stateMachine() {
		ProxyFactoryBean pfb = new ProxyFactoryBean();
		pfb.setTargetSource(poolTargetSource());
		return pfb;
	}

	@Bean
	public CommonsPool2TargetSource poolTargetSource() {
		CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
		pool.setMaxSize(3);
		pool.setTargetBeanName("stateMachineTarget");
		return pool;
	}
//end::snippetB[]

//tag::snippetC[]
	@Bean(name = "stateMachineTarget")
	@Scope(scopeName="prototype")
	public StateMachine<States, Events> stateMachineTarget() throws Exception {
		Builder<States, Events> builder = StateMachineBuilder.<States, Events>builder();

		builder.configureConfiguration()
			.withConfiguration().beanFactory(applicationContext.getAutowireCapableBeanFactory()).autoStartup(true);

		builder.configureStates()
			.withStates()
				.initial(States.S1)
				.states(EnumSet.allOf(States.class));

		builder.configureTransitions()
			.withInternal()
				.source(States.S1).event(Events.ADD)
				.action(addAction)
				.and()
			.withExternal()
				.source(States.S1).target(States.S2)
				.action(e1Action)
				.event(Events.E1)
				.and()
			.withExternal()
				.source(States.S2).target(States.S3)
				.action(e2Action)
				.event(Events.E2);

		return builder.build();
	}

// 	@Bean
// 	public RedisConnectionFactory redisConnectionFactory() {
// 		return new JedisConnectionFactory();
// 	}

	@Bean
	public StateMachinePersist<States, Events, String> stateMachinePersist(RedisConnectionFactory connectionFactory) {
		RedisStateMachineContextRepository<States, Events> repository =
				new RedisStateMachineContextRepository<States, Events>(connectionFactory);
		return new RepositoryStateMachinePersist<States, Events>(repository);
	}

	@Bean
	public RedisStateMachinePersister<States, Events> redisStateMachinePersister(
			StateMachinePersist<States, Events, String> stateMachinePersist) {
		return new RedisStateMachinePersister<States, Events>(stateMachinePersist);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public enum States {
		S1,S2,S3
	}

	public enum Events {
		E1,E2,ADD,DEL
	}
}
