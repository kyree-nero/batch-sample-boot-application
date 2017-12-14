package sample.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@Import(
	{
		SimpleBatchJobConfiguration.class, 
	}
)

public class BatchConfiguration {
	@Autowired DataSource dataSource;
	@Autowired PlatformTransactionManager transactionManager;
	
	@Bean public JobRepositoryFactoryBean jobRepositoryFactoryBean() {
		JobRepositoryFactoryBean bean = new JobRepositoryFactoryBean();
		bean.setDataSource(dataSource);
		bean.setTransactionManager(transactionManager);
		return bean;
	}
	
	@Bean public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher bean = new SimpleJobLauncher();
		bean.setJobRepository(jobRepositoryFactoryBean().getObject());
		return bean;
	}
} 