package sample;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcOperations;




public class SimpleRemotePartitionBatchJobIT extends AbstractBatchIT{
	
	@Autowired JdbcOperations jdbcOperations;
	@Autowired JobLauncher jobLauncher;
	@Autowired @Qualifier("simpleRemotePartitionBatchJob") Job simpleRemotePartitionBatchJob;
	
	
	@Test(timeout=60000) public void test() throws Exception{
		
		ExecutorService executorService = Executors.newFixedThreadPool(2);	
	
		AbstractApplicationContext slaveOneContext = startNewSlave(executorService);
		AbstractApplicationContext slaveTwoContext = startNewSlave(executorService);
		
		
	try {
	
		JobExecution jobExecution = jobLauncher.run(simpleRemotePartitionBatchJob, new JobParameters());
		BatchStatus batchStatus =  jobExecution.getStatus();
				
		Assert.assertEquals(BatchStatus.COMPLETED, batchStatus);
		Assert.assertEquals(10, jdbcOperations.queryForObject("select count(*) from BATCH_SAMPLE", Integer.class).intValue()); 
		Assert.assertEquals(10, jdbcOperations.queryForObject("select count(*) from BATCH_SAMPLE_OUTPUT", Integer.class).intValue()); 
		if(jdbcOperations.queryForObject("select count(distinct run_id) from BATCH_SAMPLE_OUTPUT", Integer.class).intValue() == 1) {
			System.out.println("Only one of the slave threads contributed!");
		}else {
			System.out.println("Both of the main threads contributed");
		}
		
		
	}finally {
		slaveOneContext.close();
		slaveTwoContext.close();
		
	}
	
} 
	
	protected AbstractApplicationContext startNewSlave(ExecutorService executorService) {
		
		AnnotationConfigApplicationContext slaveContext = new AnnotationConfigApplicationContext();
		Callable<Integer> slave = createSlave(slaveContext);
		executorService.submit(slave);
		return slaveContext;
	}private Callable<Integer> createSlave(AnnotationConfigApplicationContext context){
		return new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				
				context.register(
						Application.class
				);
				ConfigurableEnvironment convenv = (ConfigurableEnvironment)context.getEnvironment();
				convenv.merge(env);
				context.getEnvironment().setActiveProfiles("test", "slave");
				context.refresh();
				return 0;
			}
			
		};
	}
	
	
	
}
