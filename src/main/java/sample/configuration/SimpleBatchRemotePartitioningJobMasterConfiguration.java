package sample.configuration;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.jms.DynamicJmsTemplate;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHandler;

import sample.batch.RemoteRangePartitioner;
import sample.batch.domain.SimpleRemotePartitioningBatchJobObject;
import sample.batch.domain.SimpleRemotePartitioningBatchJobOutputObject;

@Configuration
@EnableIntegration
@Profile("master")
@Import(SimpleBatchRemotePartitioningJobCommonConfiguration.class)
public class SimpleBatchRemotePartitioningJobMasterConfiguration {
	@Autowired ConnectionFactory connectionFactory;
	@Autowired Destination inboundRequestsDestination;
	
	@Autowired JobBuilderFactory jobBuilderFactory;
	@Autowired StepBuilderFactory stepBuilderFactory;
	@Autowired JobExplorer jobExplorer;
	@Autowired MessagingTemplate messagingTemplate;
	@Autowired Step simpleBatchRemotePartitioningJobStep;
	
	@Bean public MessagingTemplate messagingTemplate() {
		MessagingTemplate bean = new MessagingTemplate();
		bean.setReceiveTimeout(60000000L);
		bean.setDefaultDestination(outboundRequests());
		return bean;
	}
	
	@Bean public DirectChannel outboundRequests() {
		return new DirectChannel();
	}
	
	@Bean @ServiceActivator(inputChannel="outboundRequests")
	public MessageHandler jmsOutboundAdapter() {
		JmsTemplate jmsTemplate = new DynamicJmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		JmsSendingMessageHandler jmsSendingMessageHandler = new JmsSendingMessageHandler(jmsTemplate);
		jmsSendingMessageHandler.setDestination(inboundRequestsDestination);
		return jmsSendingMessageHandler;
	}
	
	@Bean @Qualifier("executorId") public String executorId() {
		return null;
	}
	
	
	
	@Bean RemoteRangePartitioner remoteRangePartitioner() {
		return new RemoteRangePartitioner();
	}
	
	@Bean PartitionHandler partitionHandler() {
		MessageChannelPartitionHandler bean = new MessageChannelPartitionHandler();
		bean.setStepName("simpleBatchRemotePartitioningJobStep");
		bean.setGridSize(5);
		bean.setMessagingOperations(messagingTemplate);
		bean.setPollInterval(5000L);
		bean.setJobExplorer(jobExplorer);
		return bean;
	}
	 
	
	@Bean @Qualifier("simpleRemotePartitioningBatchJobPartitionStep") public Step simpleRemotePartitioningBatchJobPartitionStep(
			ItemReader<SimpleRemotePartitioningBatchJobObject> reader, 
			ItemProcessor<SimpleRemotePartitioningBatchJobObject, SimpleRemotePartitioningBatchJobOutputObject> processor, 
			ItemWriter<SimpleRemotePartitioningBatchJobOutputObject> writer
	) {
		return stepBuilderFactory.get("simpleRemotePartitioningBatchJobPartitionStep")
			.partitioner("simpleBatchRemotePartitioningJobStep",remoteRangePartitioner())
			.step(simpleBatchRemotePartitioningJobStep)
			.partitionHandler(partitionHandler())
			.build();
	}
	
	@Bean @Qualifier("simpleRemotePartitionBatchJob") public Job simpleRemotePartitionBatchJob(
			@Qualifier("simplePartitioningBatchJobCleanStep") Step simplePartitioningBatchJobCleanStep, 
			@Qualifier("simpleRemotePartitioningBatchJobPartitionStep") Step simpleRemotePartitioningBatchJobPartitionStep
	) {
		return jobBuilderFactory.get("simpleRemotePartitionBatchJob")
				.start(simplePartitioningBatchJobCleanStep)
				.next(simpleRemotePartitioningBatchJobPartitionStep)
				.build();
	}
}
