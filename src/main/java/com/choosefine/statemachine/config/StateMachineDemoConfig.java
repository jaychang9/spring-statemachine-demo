package com.choosefine.statemachine.config;

import com.choosefine.statemachine.bean.MyRedisStateMachineContextRepository;
import java.util.EnumSet;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.redis.RedisStateMachinePersister;

/**
 * 任务状态机配置
 *
 * @author 张洁
 * @date 2017/11/14
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class StateMachineDemoConfig implements ApplicationContextAware{

	private ApplicationContext applicationContext;

	@Value("${taskStateMachine.maxSize:2}")
	private int taskStateMachineMaxSize;

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
		pool.setMaxSize(taskStateMachineMaxSize);
		pool.setTargetBeanName("stateMachineTarget");
		return pool;
	}

	@Bean(name = "stateMachineTarget")
	@Scope(scopeName="prototype")
	public StateMachine<States, Events> stateMachineTarget() throws Exception {
		Builder<States, Events> builder = StateMachineBuilder.<States, Events>builder();

		builder.configureConfiguration()
			.withConfiguration().beanFactory(applicationContext.getAutowireCapableBeanFactory())
				.autoStartup(true);

		builder.configureStates()
			.withStates()
				.initial(States.S1)
				.end(States.S3)
				.states(EnumSet.allOf(States.class));

		builder.configureTransitions()
			.withExternal()// 审核未通过 -关闭任务-》 任务已关闭
				.source(States.S1).target(States.S2)
				.action(e1Action())
				.event(Events.E1)
				.and()
			.withExternal()
				.source(States.S2).target(States.S3)
				.action(e2Action())
				.event(Events.E2);

		return builder.build();
	}

	@Bean
	public Action<States,Events> e1Action(){
		return new Action(){

			@Override
			public void execute(StateContext context) {
				System.out.println("e1Action");
			}
		};
	}

	@Bean
	public Action<States,Events> e2Action(){
		return new Action(){

			@Override
			public void execute(StateContext context) {
				System.out.println("e2Action");
			}
		};
	}

	@SuppressWarnings("SpringJavaAutowiringInspection")
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

	public static enum States{
		S1,S2,S3
	}

	public static enum Events{
		E1,E2
	}
}
