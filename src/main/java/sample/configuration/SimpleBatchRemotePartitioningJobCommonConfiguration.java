package sample.configuration;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import sample.batch.domain.SimpleRemotePartitioningBatchJobObject;
import sample.batch.domain.SimpleRemotePartitioningBatchJobOutputObject;

@Configuration
public class SimpleBatchRemotePartitioningJobCommonConfiguration {
	@Autowired StepBuilderFactory stepBuilderFactory;
	@Autowired DataSource dataSource;
	//@Autowired @Qualifier("executorId") String executorId;
	
	@Bean protected Step simpleBatchRemotePartitioningJobStep(
			ItemReader<SimpleRemotePartitioningBatchJobObject> reader, 
			ItemProcessor<SimpleRemotePartitioningBatchJobObject, SimpleRemotePartitioningBatchJobOutputObject> processor, 
			ItemWriter<SimpleRemotePartitioningBatchJobOutputObject> writer
	) {
		StepBuilder stepBuilder = stepBuilderFactory.get("simpleBatchRemotePartitioningJobStep");
		SimpleStepBuilder<SimpleRemotePartitioningBatchJobObject, SimpleRemotePartitioningBatchJobOutputObject> simpleBuilder = stepBuilder.chunk(1);
		simpleBuilder.reader(reader);
		simpleBuilder.processor(processor);
		simpleBuilder.writer(writer);
		return simpleBuilder.build();
	}
	
	@Bean @StepScope public JdbcCursorItemReader<SimpleRemotePartitioningBatchJobObject> itemReader(
			DataSource dataSource, 
			@Value("#{stepExecutionContext[fromId]}") String startId, 
			@Value("#{stepExecutionContext[toId]}") String endId 
			
	){
		JdbcCursorItemReader<SimpleRemotePartitioningBatchJobObject> bean = new JdbcCursorItemReader<SimpleRemotePartitioningBatchJobObject>();
		RowMapper<SimpleRemotePartitioningBatchJobObject> rowMapper = new RowMapper<SimpleRemotePartitioningBatchJobObject>() {

			@Override
			public SimpleRemotePartitioningBatchJobObject mapRow(ResultSet resultSet, int arg1) throws SQLException {
				SimpleRemotePartitioningBatchJobObject obj = new SimpleRemotePartitioningBatchJobObject();
				obj.setId(new Long(resultSet.getLong("ID")));
				obj.setContent(resultSet.getString("CONTENT"));
				return obj;
			}
			
		};
		bean.setRowMapper(rowMapper);
		bean.setDataSource(dataSource);
		bean.setSql("SELECT * FROM BATCH_SAMPLE WHERE ID BETWEEN " + startId + " AND " + endId);
		return bean;
	}
	
//	@Bean @StepScope public ItemProcessor<SimpleRemotePartitioningBatchJobObject, SimpleRemotePartitioningBatchJobOutputObject> itemProcessor(
//			@Value("#{stepExecutionContext[fromId]}") String startId
//			
//	){
//		return new ItemProcessor<SimpleRemotePartitioningBatchJobObject, SimpleRemotePartitioningBatchJobOutputObject>(){
//			
//			@Override
//			public SimpleRemotePartitioningBatchJobOutputObject process(SimpleRemotePartitioningBatchJobObject item)
//					throws Exception {
//				SimpleRemotePartitioningBatchJobOutputObject obj = new SimpleRemotePartitioningBatchJobOutputObject();
//				obj.setId(item.getId());
//				obj.setContent(item.getContent());
//				obj.setRunGroupName(executorId);
//				System.out.println("id " + obj.getId() + " | content " + obj.getContent() + " | runId " + obj.getRunGroupName());
//				Thread.sleep(100);
//				return obj;
//			}
//			
//		};
//	}
	
	
	@Bean @StepScope public JdbcBatchItemWriter<SimpleRemotePartitioningBatchJobOutputObject> itemWriter(
			@Value("#{stepExecutionContext[fromId]}") String startId, 
			@Value("#{stepExecutionContext[toId]}") String endId, 
			@Value("#{stepExecutionContext[groupName]}") String groupName
	){
		JdbcBatchItemWriter<SimpleRemotePartitioningBatchJobOutputObject> bean = new JdbcBatchItemWriter<SimpleRemotePartitioningBatchJobOutputObject>();
		bean.setDataSource(dataSource);
		bean.setSql("INSERT INTO BATCH_SAMPLE_OUTPUT (ID, CONTENT, RUN_ID) VALUES (:id, :content, :runId)");
		bean.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<SimpleRemotePartitioningBatchJobOutputObject>() {
			
			@Override
			public SqlParameterSource createSqlParameterSource(SimpleRemotePartitioningBatchJobOutputObject obj) {
				MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
				sqlParameterSource.addValue("id", obj.getId());
				sqlParameterSource.addValue("content", obj.getContent());
				sqlParameterSource.addValue("runId", obj.getRunGroupName());
				System.out.println("id " + obj.getId() + " | content " + obj.getContent() + " | runId " + obj.getRunGroupName());
				return sqlParameterSource;
			}
		});
		return bean;
	}
	
}
