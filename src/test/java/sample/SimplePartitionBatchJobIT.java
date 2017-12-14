package sample;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;


public class SimplePartitionBatchJobIT extends AbstractBatchIT{
	@Autowired  @Qualifier("simplePartitionBatchJob") Job simplePartitionBatchJob;
	@Autowired JdbcOperations jdbcOperations;
	
	@Test public void test() throws Exception {
		JobExecution jobExecution = jobLauncher.run(simplePartitionBatchJob, new JobParameters());
		Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		
		Assert.assertEquals(10,  jdbcOperations.queryForObject("select count(*) FROM BATCH_SAMPLE", Integer.class).intValue()); 
		Assert.assertEquals(10,  jdbcOperations.queryForObject("select count(*) FROM BATCH_SAMPLE_OUTPUT", Integer.class).intValue()); 
		Assert.assertEquals(5,  jdbcOperations.queryForObject("select count(distinct run_id) FROM BATCH_SAMPLE_OUTPUT", Integer.class).intValue()); 
	}
}