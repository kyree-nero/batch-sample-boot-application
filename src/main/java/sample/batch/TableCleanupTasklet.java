package sample.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;

public class TableCleanupTasklet implements Tasklet {

	@Autowired JdbcOperations jdbcOperations;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		jdbcOperations.update("TRUNCATE TABLE BATCH_SAMPLE_OUTPUT");
		return RepeatStatus.FINISHED;
	}

}
