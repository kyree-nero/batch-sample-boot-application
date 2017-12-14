package sample.configuration;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile({"!test"})
public class TomcatJNDIConfiguration {
	@Autowired Environment environment;
	
	@Bean public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected void postProcessContext(Context context) {
				super.postProcessContext(context);
				
				initializeDataSource(context);
				initializeJms(context);
			}
			
			
			private void initializeDataSource(Context context) {

				System.out.println("...initializing Tomcat DataSource");
				ContextResource dataSourceContextResource = new ContextResource();
				
				dataSourceContextResource.setName("jdbc/dataSource");
				dataSourceContextResource.setType("javax.sql.DataSource");
				dataSourceContextResource.setProperty("driverClassName", "com.mysql.jdbc.Driver");
				dataSourceContextResource.setProperty("username", "root");
				dataSourceContextResource.setProperty("password", "x");
				dataSourceContextResource.setProperty("url", "jdbc:mysql://localhost:3306/sample");
				dataSourceContextResource.setProperty("factory","org.apache.tomcat.jdbc.pool.DataSourceFactory");
				
				context.getNamingResources().addResource(dataSourceContextResource);
			}

			private void initializeJms(Context context) {
				System.out.println("...initializing Tomcat JMS");
				
				ContextResource jmsConnectionFactoryContextResource = new ContextResource();
//				if(Arrays.asList(environment.getActiveProfiles()).stream().anyMatch(s -> s.equals("master"))) {
					
					/*
					 * <Resource auth="Container" 
	name="jms/ConnectionFactory" 
	type="org.apache.activemq.ActiveMQConnectionFactory" 
	description="JMS Connection Factory" 
	factory="org.apache.activemq.jndi.JNDIReferenceFactory" 
	brokerURL="broker:(tcp://0.0.0.0:61616)" 
	brokerName="MyActiveMQBroker" 
	useEmbeddedBroker="true"/> 
	
	<Resource 
		name="jms/ConnectionFactory" 
		auth="Container" 
		type="org.apache.activemq.ActiveMQConnectionFactory" 
		description="JMS Connection Factory"
		factory="org.apache.activemq.jndi.JNDIReferenceFactory" 
		brokerURL="vm://localhost" 
		brokerName="LocalActiveMQBroker"/>
	
					 */
					jmsConnectionFactoryContextResource.setAuth("Container");
					jmsConnectionFactoryContextResource.setName("jms/connectionFactory");
					jmsConnectionFactoryContextResource.setType("org.apache.activemq.ActiveMQConnectionFactory");
					jmsConnectionFactoryContextResource.setProperty("description", "JMS Connection Factory");
					jmsConnectionFactoryContextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
					//jmsConnectionFactoryContextResource.setProperty("brokerURL", "broker:(tcp://0.0.0.0:61616)?persistent=false&useJmx=true");
					//jmsConnectionFactoryContextResource.setProperty("brokerURL", "broker:tcp://localhost:61616");
					//jmsConnectionFactoryContextResource.setProperty("brokerURL", "vm://localhost");
					jmsConnectionFactoryContextResource.setProperty("brokerURL", "tcp://localhost:61616");
					
					jmsConnectionFactoryContextResource.setProperty("brokerName", "LocalActiveMQBroker");
					
					//jmsConnectionFactoryContextResource.setProperty("useEmbeddedBroker", "true");
					
	//				jmsConnectionFactoryContextResource.setName("jms/connectionFactory");
	//				jmsConnectionFactoryContextResource.setType("org.apache.activemq.ActiveMQConnectionFactory");
	//				jmsConnectionFactoryContextResource.setProperty("brokerURL", "tcp://localhost:61616");
	//				jmsConnectionFactoryContextResource.setProperty("description", "JMS Connection Factory");
	//				jmsConnectionFactoryContextResource.setProperty("brokerName", "activeMQBroker");
	//				jmsConnectionFactoryContextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
					
					
				
//				}else {
					/*
					 * <Resource auth="Container" 
	name="jms/ConnectionFactory" 
	type="org.apache.activemq.ActiveMQConnectionFactory" 
	description="JMS Connection Factory" 
	factory="org.apache.activemq.jndi.JNDIReferenceFactory" 
	brokerURL="broker:(tcp://tomcathosttwo:61616,network:static:tcp://tomcathostone:61616)" 
	brokerName="MyActiveMQBroker" 
	useEmbeddedBroker="true"/>
					
					<Resource name="jms/ConnectionFactory" auth="Container" type="org.apache.activemq.ActiveMQConnectionFactory" description="JMS Connection Factory"
					        factory="org.apache.activemq.jndi.JNDIReferenceFactory" brokerURL="vm://localhost" brokerName="LocalActiveMQBroker"/>
				 */	
					
//					jmsConnectionFactoryContextResource.setName("jms/connectionFactory");
//					jmsConnectionFactoryContextResource.setType("org.apache.activemq.ActiveMQConnectionFactory");
//					jmsConnectionFactoryContextResource.setProperty("brokerURL", "broker:(tcp://0.0.0.0:61617,network:static:tcp://0.0.0.0:61616)?persistent=false&useJmx=true");
//					jmsConnectionFactoryContextResource.setProperty("description", "JMS Connection Factory");
//					jmsConnectionFactoryContextResource.setProperty("brokerName", "activeMQBroker");
//					jmsConnectionFactoryContextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
//					jmsConnectionFactoryContextResource.setProperty("useEmbeddedBroker", "true");
					
					jmsConnectionFactoryContextResource.setAuth("Container");
					jmsConnectionFactoryContextResource.setName("jms/connectionFactory");
					jmsConnectionFactoryContextResource.setType("org.apache.activemq.ActiveMQConnectionFactory");
					//jmsConnectionFactoryContextResource.setProperty("brokerURL", "vm://localhost");
					jmsConnectionFactoryContextResource.setProperty("brokerURL", "tcp://localhost:61616");
					//jmsConnectionFactoryContextResource.setProperty("brokerURL", "broker:(tcp://0.0.0.0:61617,network:static:tcp://0.0.0.0:61616)?persistent=false&useJmx=true");
					jmsConnectionFactoryContextResource.setProperty("description", "JMS Connection Factory");
					jmsConnectionFactoryContextResource.setProperty("brokerName", "activeMQBroker");
					jmsConnectionFactoryContextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
					jmsConnectionFactoryContextResource.setProperty("useEmbeddedBroker", "true");
					
//				}
				context.getNamingResources().addResource(jmsConnectionFactoryContextResource);
				
				
				
				ContextResource jmsBatchQueueContextResource = new ContextResource();
				
			
				jmsBatchQueueContextResource.setName("jms/queue/batchQueue");
				jmsBatchQueueContextResource.setType("org.apache.activemq.command.ActiveMQQueue");
				jmsBatchQueueContextResource.setProperty("description", "JMS Batch Queue");
				jmsBatchQueueContextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
				jmsBatchQueueContextResource.setAuth("Container");
				
//				jmsBatchQueueContextResource.setName("jms/queue/batchQueue");
//				jmsBatchQueueContextResource.setType("org.apache.activemq.command.ActiveMQQueue");
//				jmsBatchQueueContextResource.setProperty("description", "JMS Batch Queue");
//				jmsBatchQueueContextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
//				jmsBatchQueueContextResource.setProperty("description", "JMS Batch Queue");
				context.getNamingResources().addResource(jmsBatchQueueContextResource);
			}


			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatEmbeddedServletContainer(tomcat);
			}
			
		};
		
		
	}
}
