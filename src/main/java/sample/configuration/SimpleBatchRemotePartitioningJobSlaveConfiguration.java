package sample.configuration;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.jms.ChannelPublishingJmsMessageListener;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.messaging.PollableChannel;

@Configuration
@EnableIntegration
@Profile({"slave"})
@Import(SimpleBatchRemotePartitioningJobCommonConfiguration.class)
public class SimpleBatchRemotePartitioningJobSlaveConfiguration {
	@Autowired ConnectionFactory connectionFactory;
	@Autowired Destination inboundRequestsDestination;
	@Autowired ApplicationContext applicationContext;
	@Autowired JobExplorer jobExplorer;
	public static AtomicInteger executorIdCounter = new AtomicInteger(0);
	
	@Bean SimpleMessageListenerContainer inboundListenerContainer() {
		SimpleMessageListenerContainer bean = new SimpleMessageListenerContainer();
		bean.setDestination(inboundRequestsDestination);
		bean.setConnectionFactory(connectionFactory);
		return bean;
	}
	
	@Bean public DirectChannel inboundRequests() {
		return new DirectChannel();
	}
	
	@Bean ChannelPublishingJmsMessageListener inboundMessageListener() {
		ChannelPublishingJmsMessageListener bean = new ChannelPublishingJmsMessageListener();
		bean.setExpectReply(false);
		bean.setRequestChannel(inboundRequests());
		bean.setBeanFactory(applicationContext);
		return bean;
	}
	
	@Bean JmsMessageDrivenEndpoint inboundEndpoint() {
		return new JmsMessageDrivenEndpoint(inboundListenerContainer(), inboundMessageListener()); 
	}
	
	@Bean @ServiceActivator(inputChannel="inboundRequests", outputChannel="outboundStaging")
	//@ServiceActivator(inputChannel="inboundRequests")
	public StepExecutionRequestHandler stepExecutionRequestHandler() {
		StepExecutionRequestHandler bean = new StepExecutionRequestHandler();
		BeanFactoryStepLocator stepLocator = new BeanFactoryStepLocator();
		stepLocator.setBeanFactory(applicationContext);
		bean.setStepLocator(stepLocator);
		bean.setJobExplorer(jobExplorer);
		return bean;
		
	}
	
	@Bean
	public PollableChannel outboundStaging() {
		return new NullChannel();
	}	
	
	@Bean @Qualifier("executorId") public String executorId() { 
		
		String i = String.valueOf(executorIdCounter.incrementAndGet()); System.out.println("executorId " + i); return i; } 
}
