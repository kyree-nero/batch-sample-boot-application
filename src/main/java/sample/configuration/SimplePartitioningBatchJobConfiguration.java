package sample.configuration;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import sample.batch.LocalRangePartitioner;
import sample.batch.TableCleanupTasklet;
import sample.batch.domain.SimplePartitioningBatchJobObject;
import sample.batch.domain.SimplePartitioningBatchJobOutputObject;

@Configuration
public class SimplePartitioningBatchJobConfiguration {
	@Autowired JobBuilderFactory jobBuilderFactory;
	@Autowired StepBuilderFactory stepBuilderFactory;
	@Autowired DataSource dataSource;
	
	@Bean @Qualifier("simplePartitionBatchJob") public Job simplePartitionBatchJob(
			@Qualifier("simplePartitioningBatchJobCleanStep") Step simplePartitioningBatchJobCleanStep, 
			@Qualifier("simplePartitioningBatchJobPartitionStep") Step simplePartitioningBatchJobPartitionStep
	) {
		return jobBuilderFactory.get("simplePartitionBatchJob")
				.start(simplePartitioningBatchJobCleanStep)
				.next(simplePartitioningBatchJobPartitionStep)
				.build();
	}
	
	
	@Bean TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor bean = new ThreadPoolTaskExecutor();
		bean.setMaxPoolSize(4);
		bean.setCorePoolSize(2);
		bean.setThreadNamePrefix("group-");
		return bean;
	}
	
	@Bean LocalRangePartitioner simplePartitioningBatchJobPartitioner() {
		return new LocalRangePartitioner();
	}
	
	@Bean TableCleanupTasklet tableCleanupTasklet() {
		return new TableCleanupTasklet();
	}
	
	@Bean Step simplePartitioningBatchJobCleanStep() {
		StepBuilder stepBuilder = stepBuilderFactory.get("simplePartitioningBatchJobCleanStep");
		
		TaskletStepBuilder taskletStepBuilder = new TaskletStepBuilder(stepBuilder);
		taskletStepBuilder.tasklet(tableCleanupTasklet());
		
		return taskletStepBuilder.build();
		
	}
	@Bean Step simplePartitioningBatchJobStep(
			ItemReader<SimplePartitioningBatchJobObject> reader, 
			ItemProcessor<SimplePartitioningBatchJobObject, SimplePartitioningBatchJobOutputObject> processor, 
			ItemWriter<SimplePartitioningBatchJobOutputObject> writer
	) {
			StepBuilder stepBuilder = stepBuilderFactory.get("simplePartitioningBatchJobStep");
			SimpleStepBuilder<SimplePartitioningBatchJobObject, SimplePartitioningBatchJobOutputObject> simpleBuilder = stepBuilder.chunk(2);
			simpleBuilder.reader(reader).processor(processor).writer(writer);
			return simpleBuilder.build();
	}
	
	
	@Bean Step simplePartitioningBatchJobPartitionStep(
			ItemReader<SimplePartitioningBatchJobObject> reader, 
			ItemProcessor<SimplePartitioningBatchJobObject, SimplePartitioningBatchJobOutputObject> processor, 
			ItemWriter<SimplePartitioningBatchJobOutputObject> writer
	) {
		return stepBuilderFactory.get("simplePartitioningBatchJobPartitionStep")
				.partitioner(simplePartitioningBatchJobStep(reader, processor, writer))
				.partitioner("simplePartitioningBatchJobStep", simplePartitioningBatchJobPartitioner())
				
				.taskExecutor(taskExecutor()).build();
				
	}
	
	@Bean @StepScope public JdbcCursorItemReader<SimplePartitioningBatchJobObject> simplePartitioningBatchJobReader(
			@Value("#{stepExecutionContext[fromId]}") int startId, 
			@Value("#{stepExecutionContext[toId]}") int endId
			
	){;
		JdbcCursorItemReader<SimplePartitioningBatchJobObject> bean = new JdbcCursorItemReader<SimplePartitioningBatchJobObject>();
		RowMapper<SimplePartitioningBatchJobObject> rowMapper = new RowMapper<SimplePartitioningBatchJobObject>() {
			
			@Override
			public SimplePartitioningBatchJobObject mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				SimplePartitioningBatchJobObject simplePartitioningBatchJobObject = new SimplePartitioningBatchJobObject();
				simplePartitioningBatchJobObject.setId(resultSet.getLong("ID"));
				simplePartitioningBatchJobObject.setContent(resultSet.getString("CONTENT"));
				return simplePartitioningBatchJobObject;
			}
			
		};
		bean.setRowMapper(rowMapper);
		bean.setDataSource(dataSource);
		bean.setSql("SELECT * FROM BATCH_SAMPLE WHERE ID BETWEEN " + startId + " AND " + endId);
		
		return bean;
	}
	
	@Bean @StepScope public ItemProcessor<SimplePartitioningBatchJobObject, SimplePartitioningBatchJobOutputObject> simplePartitioningBatchJobProcessor(
			@Value("#{stepExecutionContext[fromId]}") int startId , 
			@Value("#{stepExecutionContext[groupName]}") String groupName
	){
		return new ItemProcessor<SimplePartitioningBatchJobObject, SimplePartitioningBatchJobOutputObject>() {

			@Override
			public SimplePartitioningBatchJobOutputObject process(SimplePartitioningBatchJobObject source)
					throws Exception {
				SimplePartitioningBatchJobOutputObject target = new SimplePartitioningBatchJobOutputObject();
				target.setId(source.getId());
				target.setContent(source.getContent());
				target.setRunGroupName(groupName);
				return target;
			}
		};
	}
	
	@Bean @StepScope public JdbcBatchItemWriter<SimplePartitioningBatchJobOutputObject> simplePartitioningBatchJobWriter(){
		JdbcBatchItemWriter<SimplePartitioningBatchJobOutputObject> bean = new JdbcBatchItemWriter<SimplePartitioningBatchJobOutputObject>();
		bean.setDataSource(dataSource);
		bean.setSql("INSERT INTO BATCH_SAMPLE_OUTPUT (ID, CONTENT, RUN_ID) VALUES (:id, :content, :runId)");
		bean.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<SimplePartitioningBatchJobOutputObject>() {
			
			@Override
			public SqlParameterSource createSqlParameterSource(SimplePartitioningBatchJobOutputObject item) {
				MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
				sqlParameterSource.addValue("id", item.getId());
				sqlParameterSource.addValue("content", item.getContent());
				sqlParameterSource.addValue("runId", item.getRunGroupName());
				return sqlParameterSource;
			}
		});
		return bean;
	}
	
}
