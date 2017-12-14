package sample.configuration;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfiguration {

	@Value("${spring.activemq.broker-url}") private String activeMQBrokerUrl;
	@Value("${spring.activemq.user}") private String activeMQUser;
	@Value("${spring.activemq.password}") private String activeMQPassword;

	@Bean public ActiveMQConnectionFactory connectionFactory() throws JMSException{ 
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(); 
		connectionFactory.setBrokerURL(activeMQBrokerUrl); 
		connectionFactory.setPassword(activeMQUser); 
		connectionFactory.setUserName(activeMQPassword);
		connectionFactory.setTrustAllPackages(true);
		return connectionFactory; 
	
	}

	@Bean public Destination batchQueueDestination() {
		ActiveMQQueue queue = new ActiveMQQueue("batchQueue");
		return queue;
	}
}
	